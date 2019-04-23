package com.t3h.filternews.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
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

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_REGISTER = 20;
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_NAME = "key_name";
    private int RC_SIGN_IN = 10;
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private CheckBox cbSaveLogin;
    private LoginButton btnFacebook;
    private SignInButton btnGoogle;
    private TextView txtRegister;
    private CallbackManager callbackManager;
    private boolean isCheckedSaveLogin;
    private FacebookCallback<LoginResult> loginResult;
    private GoogleSignInClient mGoogleSignInClient;
    private long startTime;
    private Context context;
    private FireBaseActivity fireBaseActivity;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login);
        initViews();
        fireBaseActivity = new FireBaseActivity();
        fireBaseActivity.firebase();
        callbackManager = CallbackManager.Factory.create();
        initFaceBook();
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);
        initGoogle();
        context = LoginActivity.this;
        getInForUserFromSharePrefer();
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private void initGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initFaceBook() {
        loginResult = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        final String name = object.optString(getString(R.string.name));
                        final String email = object.optString(getString(R.string.email));
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("user").orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Intent intent = new Intent(context, ViewPagerActivity.class);
                                    startActivity(intent);
                                }else {
                                    User user = new User.UserBuilder("", "")
                                            .email(email)
                                            .name(name)
                                            .build();
                                    fireBaseActivity.insertUser(user, name);
                                    Intent intent = new Intent(context, ViewPagerActivity.class);
                                    intent.putExtra(KEY_NAME, name);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString(getString(R.string.fields), getString(R.string.fields_name));
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };

    }

    public void loginFaceBook(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public boolean isLoggedInFaceBook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void initViews() {
        edtPassword = findViewById(R.id.edt_password);
        edtUsername = findViewById(R.id.edt_username);
        btnLogin = findViewById(R.id.btn_login);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnGoogle = findViewById(R.id.btn_google);
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
        btnGoogle.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        // tra ve username va name tu Register
        if (requestCode == REQUEST_REGISTER && resultCode == RESULT_OK){
            name = data.getStringExtra(KEY_NAME);
            String username = data.getStringExtra(KEY_USERNAME);
            edtUsername.setText(username);
        }
    }

    //tra ve thong tin nguoi dung cua google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            final String email = account.getEmail().toString();
            final String name = account.getDisplayName().toString();
            //Kiem tra trong firebase email da co chua
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child("user").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Intent intent = new Intent(context, ViewPagerActivity.class);
                        intent.putExtra(KEY_NAME, name);
                        startActivity(intent);
                    }else {
                        User user = new User.UserBuilder("", "")
                                .email(email)
                                .name(name)
                                .build();
                        fireBaseActivity.insertUser(user, name);
                        Intent intent = new Intent(context, ViewPagerActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (ApiException e) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_register:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivityForResult(intentRegister, REQUEST_REGISTER);
                break;
            case R.id.btn_login:
                String username = edtUsername.getText().toString().trim();
                final String password = edtPassword.getText().toString().trim();
                if (isCheckedSaveLogin){
                    saveUser(username, password);
                }
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(this, "Mời quý đăng nhập vào hệ thống", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    Query query = databaseReference.child("user").orderByChild("username").equalTo(username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot data : dataSnapshot.getChildren()){
                                    User user = data.getValue(User.class);
                                    if (user.getPassword().equals(password)){
                                        Intent intent = new Intent(context, ViewPagerActivity.class);
                                        intent.putExtra(KEY_NAME, name);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(context, "Mật khẩu sai", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                Toast.makeText(context, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                startTime = SystemClock.elapsedRealtime();
                LoginActivity.this.setStartTime(startTime);
                break;
            case R.id.btn_facebook:
                loginFaceBook();
                break;
            case R.id.btn_google:
                loginGoogle();
                break;
        }
    }

    //luu thong tin dang nhap
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

}
