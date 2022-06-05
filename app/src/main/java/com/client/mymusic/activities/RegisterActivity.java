package com.client.mymusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.client.mymusic.R;
import com.client.mymusic.entities.AccessToken;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.inputFullname)
    EditText inputName;
    @BindView(R.id.inputEmail)
    EditText inputEmail;
    @BindView(R.id.inputPassword)
    EditText inputPassword;
    @BindView(R.id.inputComfirmed)
    EditText inputComfirmed;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation awesomeValidation;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        awesomeValidation = new AwesomeValidation(BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();
    }

    @OnClick(R.id.linkRegister)
    void linkRegister() {
        RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.btn_previous_page)
    void previousPage() {
        finish();
        overridePendingTransition(R.anim.no_change, R.anim.slide_out_up );
    }

    @OnClick(R.id.btnRegister)
    void register() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String passwordComfirmation = inputComfirmed.getText().toString();

        inputName.setError(null);
        inputEmail.setError(null);
        inputPassword.setError(null);

        awesomeValidation.clear();
        if (awesomeValidation.validate()) {
            call = service.register(name, email, password, passwordComfirmation);
            call.enqueue(new Callback<AccessToken>() {

                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG, "clicked" + response);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Log.w(TAG, "onResponse: " + response.body());
                        tokenManager.saveToken(response.body());
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        RegisterActivity.this.startActivity(intent);
                        finish();
                    } else {

                        handleErrors(response.errorBody());

                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this,":" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onResponse: " +  t.getMessage());
                }
            });
        }
    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = ConvertErrors.convertErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("name")) {
                inputName.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("email")) {
                inputEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                inputPassword.setError(error.getValue().get(0));
            }
        }

    }

    public void setupRules() {
        awesomeValidation.addValidation(RegisterActivity.this, R.id.inputFullname, RegexTemplate.NOT_EMPTY, R.string.err_name);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.inputEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.inputPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.inputComfirmed, R.id.inputPassword, R.string.err_password_comfirmed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null) {
            call.cancel();
            call = null;
        }
    }
}
