package com.t3h.filternews.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.t3h.filternews.R;
import com.t3h.filternews.firebase.FireBaseActivity;
import com.t3h.filternews.main.ViewPagerActivity;
import com.t3h.filternews.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUser, edtPass, edtEmail, edtName;
    private Button btnRegister;
    private ImageView imgErrorUsername, imgStartUsername;
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
        imgErrorUsername = findViewById(R.id.img_error_username);
        imgStartUsername = findViewById(R.id.img_start_username);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String userName = edtUser.getText().toString();
        final String passWord = edtPass.getText().toString();
        final String email = edtEmail.getText().toString();
        final String name = edtName.getText().toString();
        if (userName.isEmpty() || passWord.isEmpty() || name.isEmpty()){
            Snackbar.make(btnRegister,"Không được để trống trường dữ liệu bắt buộc", Snackbar.LENGTH_SHORT).show();
        }else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child("user").orderByChild("username").equalTo(userName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Snackbar.make(btnRegister,"Tên tài khoản đã tồn tại", Snackbar.LENGTH_SHORT).show();
                        imgErrorUsername.setVisibility(View.VISIBLE);
                        imgStartUsername.setVisibility(View.INVISIBLE);
                    }else {
                        User user = new User.UserBuilder(userName, passWord)
                                .email(email)
                                .name(name)
                                .build();
                        fireBaseActivity.insertUser(user, name);
                        Intent intent = new Intent();
                        intent.putExtra(LoginActivity.KEY_USERNAME, userName);
                        intent.putExtra(LoginActivity.KEY_NAME, name);
                        setResult(RESULT_OK, intent);
                        Snackbar.make(btnRegister,"Đăng ký thành công", Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
