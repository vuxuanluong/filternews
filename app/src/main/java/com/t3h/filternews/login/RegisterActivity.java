package com.t3h.filternews.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.t3h.filternews.R;
import com.t3h.filternews.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUser, edtPass, edtEmail, edtPhone;
    private Button btnRegister;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_register);
        initViews();
    }

    private void initViews() {
        edtUser = findViewById(R.id.edt_username);
        edtPass = findViewById(R.id.edt_password);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String userName = edtUser.getText().toString();
        String passWord = edtPass.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        if (userName.equals("") || passWord.equals("") || email.equals("") || phone.equals("")){
            Toast.makeText(this,"Không được để trống thông tin", Toast.LENGTH_SHORT).show();
        }else {
            User user = new User(userName, passWord, email, phone);
        }
    }
}
