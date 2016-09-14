package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UniversitiesFragment.newInstance();
            case 1:
                return new GroupsFragment(); // TODO: 15.09.2016 change to 'newInstance()'
            default:
                return UniversitiesFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "University";
            case 1:
                return "Group";
        }
        return null;
    }
}