package com.client.mymusic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.client.mymusic.R;
import com.client.mymusic.entities.ApiError;
import com.client.mymusic.networks.ApiService;
import com.client.mymusic.networks.RetrofitBuilder;
import com.client.mymusic.utils.ConvertErrors;
import com.client.mymusic.utils.TokenManager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    @BindView(R.id.inputPassword)
    EditText inputPassword;
    @BindView(R.id.inputNewPassword)
    EditText inputNewPassword;
    @BindView(R.id.inputComfirmed)
    EditText inputComfirmed;

    ApiService service;
    Call<Boolean> call;
    TokenManager tokenManager;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);
        awesomeValidation = new AwesomeValidation(BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        setupRules();
        Toast.makeText(this, TokenManager.getToken().getAccessToken(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_previous_page)
    void previousPage() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
        overridePendingTransition( R.anim.no_change, R.anim.slide_out_right );
    }

    @OnClick(R.id.btnChangePassword)
    void ChangePassword() {
        String password = inputPassword.getText().toString();
        String newPassword = inputNewPassword.getText().toString();;

        inputPassword.setError(null);
        inputNewPassword.setError(null);
        inputComfirmed.setError(null);

        awesomeValidation.clear();
        if (awesomeValidation.validate()) {
            call = service.changePassword(password, newPassword);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body()) {
                            Toast.makeText(getApplicationContext(), "Change password successful", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition( R.anim.no_change, R.anim.slide_out_right );
                        }
                    } else {
                        handleErrors(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(ChangePasswordActivity.this,":" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onResponse: " +  t.getMessage());
                }
            });
        }
    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = ConvertErrors.convertErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("password")) {
                inputPassword.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("new_password")) {
                inputNewPassword.setError(error.getValue().get(0));
            }
        }

    }

    private void setupRules() {
        awesomeValidation.addValidation(ChangePasswordActivity.this, R.id.inputPassword, RegexTemplate.NOT_EMPTY, R.string.err_password);
        awesomeValidation.addValidation(ChangePasswordActivity.this, R.id.inputNewPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
        awesomeValidation.addValidation(ChangePasswordActivity.this, R.id.inputComfirmed,  R.id.inputNewPassword, R.string.err_password_comfirmed);
    }

}
