package com.e.novel.Navigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class VocalizationPagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;

    public VocalizationPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LikeActivity likeActivity = new LikeActivity();
                return likeActivity;
            case 1:
                FanActivity fanActivity = new FanActivity();
                return fanActivity;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}