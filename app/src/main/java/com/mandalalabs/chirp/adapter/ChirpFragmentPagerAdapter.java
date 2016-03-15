package com.mandalalabs.chirp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mandalalabs.chirp.fragment.ChirpsFragment;
import com.mandalalabs.chirp.fragment.NeighborsFragment;
import com.mandalalabs.chirp.fragment.ProfileFragment;

public class ChirpFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] tabTitles = {"Scene", "Neighbors", "Profile"};
    ChirpsFragment chirpsFragment;
    NeighborsFragment neighborsFragment;
    ProfileFragment profileFragment;

    public ChirpFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                chirpsFragment = new ChirpsFragment();
                return chirpsFragment;
            case 1:
                neighborsFragment = new NeighborsFragment();
                return neighborsFragment;
            case 2:
                profileFragment = new ProfileFragment();
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position >= 0 && position < tabTitles.length) ? tabTitles[position] : "Unknown";
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
