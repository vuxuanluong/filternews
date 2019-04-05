package com.t3h.filternews.viewpager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.t3h.filternews.R;
import com.t3h.filternews.adapter.Adapter;
import com.t3h.filternews.model.News;
import com.t3h.filternews.parse.XMLAsync;
import com.t3h.filternews.sqlite.DAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class FragmentItemNews extends Fragment implements Adapter.OnItemEventCallback {
    private RecyclerView lvNews;
    private Adapter adapter;
    private ArrayList<News> arrNew = new ArrayList<>();
    public String keyWord = "%22%22";
    public String language = "VN:vi";
    private XMLAsync xmlAsync;

    private ProgressDialog dialog;
    private DownloadTask downloadTask;
    private DAO dao;
    private int currentIndex;
    private String path;
    private TextView tvNotify;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if(isNetworkAvailable()){
            parserData();
            tvNotify.setVisibility(View.INVISIBLE);
        }else {
            return;
        }
        dao = new DAO(getContext());

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;

    }

    public void setLanguage(String language){
        this.language = language;
    }

    public void parserData() {
        xmlAsync = new XMLAsync(getContext(), handler);
        keyWord = Uri.encode(keyWord);
        String link = "https://news.google.com/rss/search?pz=1&cf=vi_vn&q=" +keyWord+ "&hl=vi&gl=VN&ceid=" + language ;
        xmlAsync.execute(link);
    }

    public void updateData() {
        parserData();
        tvNotify.setVisibility(View.INVISIBLE);
    }

    private void initViews() {
        lvNews = getActivity().findViewById(R.id.lv_news);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lvNews.setLayoutManager(manager);
        adapter = new Adapter(arrNew, getActivity());
        lvNews.setAdapter(adapter);
        adapter.setOnItemEventCallback(this);
        downloadTask = new DownloadTask(getContext());
        tvNotify = getActivity().findViewById(R.id.tv_notify);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == XMLAsync.WHAT_DATA) {
                ArrayList<News> arr = (ArrayList<News>) msg.obj;
                arrNew.clear();
                arrNew.addAll(arr);
//                arrNew.remove(0);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onClickItem(int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrNew.get(position).getLink()));
        startActivity(browserIntent);
    }

    public void download(String link) {
        path = Environment.getExternalStorageDirectory().getPath() + "/data/test" + currentIndex + ".html";
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream outputStream =
                    new FileOutputStream(file, false);
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            byte[] b = new byte[1024];
            int count = inputStream.read(b);
            while (count != -1) {
                outputStream.write(b, 0, count);
                count = inputStream.read(b);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLongClickItem(int position) {
        currentIndex = position;
        downloadTask = new DownloadTask(getContext());
        downloadTask.execute(arrNew.get(position).getLink());
    }

    @Override
    public void onClickItemView(View view) {

    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                download(sUrl[0]);
            } catch (Exception e) {
                return e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            arrNew.get(currentIndex).setPathFile(path);
            dao.insert(arrNew.get(currentIndex));

        }
    }

}
