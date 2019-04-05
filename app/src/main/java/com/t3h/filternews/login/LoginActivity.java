package com.t3h.filternews.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.t3h.filternews.R;
import com.t3h.filternews.main.ViewPagerActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private CheckBox cbSaveLogin;
    private LoginButton btnFacebook;
    private TextView txtRegister;
    private CallbackManager callbackManager;
    private boolean isCheckedSaveLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login);
        initViews();
        getInForUserFromSharePrefer();
    }

    private void initViews() {
        edtPassword = findViewById(R.id.edt_password);
        edtUsername = findViewById(R.id.edt_username);
        btnLogin = findViewById(R.id.btn_login);
        btnFacebook = findViewById(R.id.btn_facebook);
        txtRegister = findViewById(R.id.txt_register);

        cbSaveLogin = findViewById(R.id.cb_saveLogin);
        cbSaveLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LoginActivity.this.isCheckedSaveLogin = b;

            }
        });

        txtRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_register:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentRegister);
                break;
            case R.id.btn_login:
                String userName = edtUsername.getText().toString();
                String passWord = edtPassword.getText().toString();
                if (isCheckedSaveLogin){
                    saveUser(userName, passWord);
                }
                Intent intentNews  = new Intent(this, ViewPagerActivity.class);
                startActivity(intentNews);
                break;
            case R.id.btn_facebook:
                break;
        }
    }

    public void saveUser(String userName, String passWord){
        SharedPreferences preferences = getSharedPreferences("save_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("KEY_USER_NAME", userName);
        editor.putString("KEY_PASSWORD", passWord);
        editor.apply();
    }

    private String getIDUserName(){
        SharedPreferences preferences = getSharedPreferences("save_name", MODE_PRIVATE);
        String user = preferences.getString("KEY_USER_NAME", null);
        return user;
    }
    private String getIDPassWord(){
        SharedPreferences preferences = getSharedPreferences("save_name", MODE_PRIVATE);
        String pass = preferences.getString("KEY_PASSWORD", null);
        return pass;
    }

    public void getInForUserFromSharePrefer() {
        String user = getIDUserName();
        String pass = getIDPassWord();
        edtUsername.setText(user);
        edtPassword.setText(pass);
    }

//    private void printKeyHash() {
//        try{
//            PackageInfo info = getPackageManager().getPackageInfo("com.t3h.filternews",
//                    PackageManager.GET_SIGNATURES);
//            for(Signature signature:info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash",Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }
}
