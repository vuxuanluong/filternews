package com.t3h.filternews.main;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.t3h.filternews.R;
import com.t3h.filternews.login.LoginActivity;
import com.t3h.filternews.login.RegisterActivity;
import com.t3h.filternews.viewpager.FragmentItemNews;
import com.t3h.filternews.viewpager.PageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private SearchView svSearch;
    private TabLayout tabLayout;
    private FragmentItemNews fragmentItemNews;
    private Toolbar toolbar;
    private ImageView imgVoice;
    private ImageView imgLanguage;
    private static final int REQUEST_CODE = 111;
    private LoginActivity loginActivity;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_news);
        initViews();
        getLanguageFromSharedPrefer();
        initGoogle();
        loginActivity = new LoginActivity();
//        toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
    }

    private void initGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initViews() {
        viewPager = findViewById(R.id.pager_news);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        imgVoice = findViewById(R.id.img_voice);
        imgVoice.setOnClickListener(this);
        imgLanguage = findViewById(R.id.img_menu_language);
        imgLanguage.setOnClickListener(this);

        svSearch = findViewById(R.id.sv_search);
        svSearch.setBackgroundColor(Color.WHITE);

        fragmentItemNews = (FragmentItemNews) pageAdapter.getItem(0);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fragmentItemNews.setKeyWord(query);
                fragmentItemNews.updateData();
                Intent intent = getIntent();
                String name = intent.getStringExtra(LoginActivity.KEY_NAME);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference  = databaseReference.child("user").child(name).child("keyword").push();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                final String date = sdf.format(new Date());
                Map<String, Object> map = new HashMap<>();
                map.put(date, query );
                databaseReference.updateChildren(map);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                imgVoice.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_voice:
                Intent intent =  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Voice searching...");
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.img_menu_language:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.vietnamese:
                                String vietnamese = "VN:vi";
                                fragmentItemNews.setLanguage(vietnamese);
                                saveLanguage(vietnamese);
                                break;
                            case R.id.english:
                                String english = "US:en";
                                fragmentItemNews.setLanguage(english);
                                saveLanguage(english);
                                break;
                            case R.id.sign_out:
                                if (loginActivity.isLoggedInFaceBook()){
                                    LoginManager.getInstance().logOut();
                                    Intent intent1 = new Intent(ViewPagerActivity.this, LoginActivity.class);
                                    startActivity(intent1);
                                }
                                else if (GoogleSignIn.getLastSignedInAccount(ViewPagerActivity.this) != null){
                                    signOutGoogle();
                                } else {
                                    long endTime = Calendar.getInstance().getTimeInMillis();
                                    long totalTime = endTime - loginActivity.getStartTime();
//                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//                                    DatabaseReference cineIndustryRef = rootRef.child("cineIndustry").push();
//                                    String key = cineIndustryRef.getKey();
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put(key, totalTime );
//                                    cineIndustryRef.updateChildren(map);
                                    finish();
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    public void signOutGoogle() {
        mGoogleSignInClient.signOut().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK){
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(!matches.isEmpty()){
                final String query = matches.get(0);
                svSearch.setQuery(query, false);
                svSearch.clearFocus();
                fragmentItemNews.setKeyWord(query);
                fragmentItemNews.updateData();
            }
        }
    }

    public void saveLanguage(String language){
        SharedPreferences preferences = getSharedPreferences("save_language", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("KEY_LANGUAGE", language);
        editor.apply();
    }

    private String getIDLanguage(){
        SharedPreferences preferences = getSharedPreferences("save_language", MODE_PRIVATE);
        String language = preferences.getString("KEY_LANGUAGE", null);
        return language;
    }

    private void getLanguageFromSharedPrefer(){
        String language = getIDLanguage();
        fragmentItemNews.setLanguage(language);
    }

}


