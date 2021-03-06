package com.example.priceoffers.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.priceoffers.ui.AllOffersFragment;
import com.example.priceoffers.ui.UserCartFragment;

/**
 * A simple {@link FragmentPagerAdapter} for switching between the two fragments of the UI
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numOfTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllOffersFragment();
            case 1:
                return new UserCartFragment();
            default:
                return new AllOffersFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
