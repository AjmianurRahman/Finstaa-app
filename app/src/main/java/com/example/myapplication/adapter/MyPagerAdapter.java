package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.fragments.AddFragment;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.NotificationFragment;
import com.example.myapplication.fragments.ProfileFragment;
import com.example.myapplication.fragments.SearchFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    int numberOfTab;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();

            case 1:
                return new SearchFragment();

            case 2:
                return new AddFragment();

            case 3:
               return new NotificationFragment();

            case 4:
               return new ProfileFragment();

            default:
                return null;
        }


    }

    public MyPagerAdapter(@NonNull FragmentManager fm, int numberOfTab) {
        super(fm);
        this.numberOfTab = numberOfTab;
    }

    @Override
    public int getCount() {
        return numberOfTab;
    }
}
