package com.t3h.filternews.main;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.t3h.filternews.R;
import com.t3h.filternews.viewpager.FragmentItemNews;
import com.t3h.filternews.viewpager.PageAdapter;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_news);
        initViews();
        getLanguageFromSharedPrefer();
//        toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
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
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
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
