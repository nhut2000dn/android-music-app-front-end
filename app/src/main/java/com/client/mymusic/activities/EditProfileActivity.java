package com.client.mymusic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.client.mymusic.R;
import com.client.mymusic.entities.User;
import com.client.mymusic.networks.ApiService;
import com.client.mymusic.networks.RetrofitBuilder;
import com.client.mymusic.utils.TokenManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class EditProfileActivity extends AppCompatActivity {

    String imagePath = "";

    @BindView(R.id.inputFullName)
    EditText inputFullName;
    @BindView(R.id.imageProfile)
    CircularImageView imageProfile;

    ApiService service;
    Call<Boolean> callEditProfile;
    Call<User> callUser;
    TokenManager tokenManager;
    AwesomeValidation awesomeValidation;

    private static Boolean checkUpdate = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        awesomeValidation = new AwesomeValidation(BASIC);
        setupRules();
        loadData();
    }

    public void loadData() {
        callUser = service.user();
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    inputFullName.setText(response.body().getName());
//                    Picasso.get().load(response.body().getAvatar())
//                            .fit().placeholder(R.drawable.place_holder_profile).into(imageProfile);
                    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + response.body().getAvatar())
                            .fit().placeholder(R.drawable.place_holder_profile).into(imageProfile);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Falsessss:" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_previous_page)
    void previousPage() {
        Intent returnIntent = new Intent();
        if (checkUpdate) {
            returnIntent.putExtra("profileAccountChange",true);
        } else  {
            returnIntent.putExtra("profileAccountChange",false);
        }
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        overridePendingTransition( R.anim.no_change, R.anim.slide_out_right );
        checkUpdate = false;
    }

    @OnClick(R.id.btnUpdate)
    void btnUpdate() {
        inputFullName.setError(null);
        awesomeValidation.clear();
        if (awesomeValidation.validate()) {
            RequestBody name = RequestBody.create(
                    MediaType.parse("text/plain"),
                    inputFullName.getText().toString());

            File file = new File(imagePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            callEditProfile = (imagePath.isEmpty()) ? service.editProfile(name, null) : service.editProfile(name, body);
            callEditProfile.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        if (response.body()) {
                            Toast.makeText(EditProfileActivity.this, "Update successfull", Toast.LENGTH_SHORT).show();
                            checkUpdate = true;
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "False:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    @OnClick(R.id.btnChooseFile)
    void askPermission() {
        if (checkWriteExternalPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        } else {
            verifyStoragePermissions(EditProfileActivity.this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast.makeText(this, "Unable to choose image!", Toast.LENGTH_SHORT).show();
        } else {
            Uri imageUri = data.getData();
            imagePath = getRealPathFromUri(imageUri);
            File f = new File(imagePath);
//            Picasso.get().load(f).into(imageProfile);
            Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + f).into(imageProfile);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private boolean checkWriteExternalPermission()
    {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void setupRules() {
        awesomeValidation.addValidation(EditProfileActivity.this, R.id.inputFullName, RegexTemplate.NOT_EMPTY, R.string.err_name);
    }

}
