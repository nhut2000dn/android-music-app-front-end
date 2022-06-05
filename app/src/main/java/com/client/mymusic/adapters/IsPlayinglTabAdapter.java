package com.client.mymusic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.client.mymusic.fragments.AudioArtistFragment;
import com.client.mymusic.fragments.AudioLyricFragment;
import com.client.mymusic.fragments.AudioPlayFragment;

public class IsPlayinglTabAdapter extends FragmentPagerAdapter {

    private int numoftabs;

    public IsPlayinglTabAdapter(@NonNull FragmentManager fm, int numofTabs) {
        super(fm);
        this.numoftabs = numofTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AudioArtistFragment();
            case 1:
                return new AudioPlayFragment();
            case 2:
                return new AudioLyricFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numoftabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

}
