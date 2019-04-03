package com.t3h.filternews.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.t3h.filternews.R;
import com.t3h.filternews.model.News;
import com.t3h.filternews.sqlite.DAO;

import java.util.ArrayList;

public class Web_Views extends AppCompatActivity {
    private WebView webView;
    private DAO dao;
    private ArrayList<News> arrNew = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_views);
        webView = findViewById(R.id.web_path);
        Intent intent = getIntent();
        String path = intent.getStringExtra(FragmentSaveNew.KEY_PATH);
        webView.loadUrl("file://" + path);
    }
}
