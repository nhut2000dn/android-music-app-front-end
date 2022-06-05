package com.client.mymusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.inputUsername)
    EditText inputEmail;
    @BindView(R.id.inputPassword)
    EditText inputPassword;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.form_container)
    LinearLayout formContainer;

    ApiService service;
    Call<AccessToken> call;
    TokenManager tokenManager;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        service = RetrofitBuilder.createService(ApiService.class);
        ButterKnife.bind(this);
        awesomeValidation = new AwesomeValidation(BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();
        Toast.makeText(this, TokenManager.getToken().getAccessToken(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.linkRegister)
    void linkRegister() {
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    @OnClick(R.id.btn_previous_page)
    void previousPage() {
        finish();
        overridePendingTransition(R.anim.no_change, R.anim.slide_out_up );
    }

    @OnClick(R.id.btnLogin)
    void login() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        inputEmail.setError(null);
        inputPassword.setError(null);
        awesomeValidation.clear();
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        if (awesomeValidation.validate()) {
            showLoading();
            call = service.login(email, password);
            call.enqueue(new Callback<AccessToken>() {

                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG, "clicked" + response);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Log.w(TAG, "onResponse: " + response.body());
                        tokenManager.saveToken(response.body());
                        Toast.makeText(LoginActivity.this, TokenManager.getToken().getRefreshToken(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                        finish();
                    } else {

                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }

                        if (response.code() == 401) {
                            ApiError apiError = ConvertErrors.convertErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        showForm();
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,":" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onResponse: " +  t.getMessage());
                    showForm();
                }
            });
        }
    }

    private void showLoading(){
        TransitionManager.beginDelayedTransition(container);
        formContainer.setVisibility(View.GONE);
    }

    private void showForm(){
        TransitionManager.beginDelayedTransition(container);
        formContainer.setVisibility(View.VISIBLE);
    }

//    @OnClick(R.id.btn_facebook)
//    void LoginFacebook() {
//        facebookManager.login(this, new FacebookManager.FacebookLoginListener() {
//            @Override
//            public void onSuccess() {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                finish();
//            }
//
//            @Override
//            public void onError(String message) {
//                Toast.makeText(LoginActivity.this,":" + TokenManager.getToken().getAccessToken() + message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = ConvertErrors.convertErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("username")) {
                inputEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                inputPassword.setError(error.getValue().get(0));
            }
        }

    }

    private void setupRules() {
        awesomeValidation.addValidation(LoginActivity.this, R.id.inputUsername, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(LoginActivity.this, R.id.inputPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
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