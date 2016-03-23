package com.example.naveen.music.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.naveen.music.fragments.FragmentAlbums;
import com.example.naveen.music.fragments.FragmentArtists;
import com.example.naveen.music.fragments.FragmentFolders;
import com.example.naveen.music.fragments.FragmentSongs;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentSongs();
            case 2:
                return new FragmentArtists();
            case 1:
                return new FragmentAlbums();
            case 3:
                return new FragmentFolders();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
