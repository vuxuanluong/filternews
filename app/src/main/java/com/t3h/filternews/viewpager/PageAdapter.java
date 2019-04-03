package com.t3h.filternews.viewpager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    private FragmentItemNews fragmentItemNews = new FragmentItemNews();
    private FragmentSaveNew fragmentSaveNew = new FragmentSaveNew();
    private FragmentLikeNews fragmentLikeNews = new FragmentLikeNews();

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentItemNews;
            case 1:
                return fragmentSaveNew;
            default:
                return fragmentLikeNews;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
        if (fragmentSaveNew.dao != null) {
            fragmentSaveNew.updateDataSaveNews();
        }
        if (fragmentLikeNews.dao != null) {
            fragmentLikeNews.updateDataLikeNews();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Tin tức";
            case 1:
                return "Tin tức đã lưu";
            default:
                return "Yêu thích";
        }
    }
}
