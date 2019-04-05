package com.t3h.filternews.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.t3h.filternews.R;
import com.t3h.filternews.adapter.Adapter;
import com.t3h.filternews.model.News;
import com.t3h.filternews.sqlite.DAO;

import java.util.ArrayList;

public class FragmentSaveNew extends Fragment implements Adapter.OnItemEventCallback {
    public static final String KEY_PATH = "key_path";
    private RecyclerView lvSaveNews;
    private Adapter adapter;
    private ArrayList<News> arrNew = new ArrayList<>();
    public DAO dao;
    private int currentIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_save_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        dao = new DAO(getContext());
    }

    public void updateDataSaveNews() {
        arrNew.clear();
        arrNew.addAll(dao.getData());
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        lvSaveNews = getActivity().findViewById(R.id.lv_saveNews);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lvSaveNews.setLayoutManager(manager);
        adapter = new Adapter(arrNew, getActivity());
        lvSaveNews.setAdapter(adapter);
        adapter.setOnItemEventCallback(this);

    }

    @Override
    public void onClickItem(int position) {
        String path = arrNew.get(position).getPathFile();
        Intent intent = new Intent(getActivity(), Web_Views.class);
        intent.putExtra(KEY_PATH, path );
        startActivity(intent);
    }

    @Override
    public void onLongClickItem(int position) {
        currentIndex = position;
    }

    @Override
    public void onClickItemView(View view) {
        final PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite:
                        arrNew.get(currentIndex).setFavorite(true);
                        dao.updateFavorite(arrNew.get(currentIndex));
                        popupMenu.dismiss();
                        break;
                    case R.id.delete:
                        int id = arrNew.get(currentIndex).getId();
                        int rows = dao.delete(id);
                        adapter.notifyDataSetChanged();
                        if (rows > 0) {
                            Snackbar.make(lvSaveNews, "Xóa thành công", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(lvSaveNews, "Xóa thất bại", Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
