package com.example.hotel_app.appointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private FragmentManager fragmentManager;
    protected Fragment currentFragment;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public PagerAdapter(@NonNull FragmentManager fm, FragmentManager fragmentManager) {
        super(fm);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SearchByRoomFragment();
            case 1:
                return new SearchByUserFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "By room";
        else if (position == 1)
            title = "By user";
        return title;
    }
}
