package com.t3h.filternews.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.t3h.filternews.R;
import com.t3h.filternews.firebase.FireBaseActivity;
import com.t3h.filternews.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUser, edtPass, edtEmail, edtName;
    private Button btnRegister;
    private FireBaseActivity fireBaseActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_register);
        initViews();
        fireBaseActivity = new FireBaseActivity();
        fireBaseActivity.firebase();
    }

    private void initViews() {
        edtUser = findViewById(R.id.edt_username);
        edtPass = findViewById(R.id.edt_password);
        edtEmail = findViewById(R.id.edt_email);
        edtName = findViewById(R.id.edt_name);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String userName = edtUser.getText().toString();
        String passWord = edtPass.getText().toString();
        String email = edtEmail.getText().toString();
        String name = edtName.getText().toString();
        if (userName.isEmpty() || passWord.isEmpty() || name.isEmpty()){
            Toast.makeText(this,"Không được để trống", Toast.LENGTH_SHORT).show();
        }else {
            User user = new User.UserBuilder(userName, passWord)
                    .email(email)
                    .name(name)
                    .build();
            fireBaseActivity.insertUser(user, name);
            Intent intent = new Intent();
            intent.putExtra(LoginActivity.KEY_USERNAME, userName);
            setResult(RESULT_OK, intent);
            Snackbar.make(btnRegister,"Đăng ký thành công", Snackbar.LENGTH_LONG).show();
            finish();
        }
    }
}
