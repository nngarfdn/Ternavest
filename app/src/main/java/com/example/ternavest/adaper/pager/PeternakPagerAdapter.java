package com.example.ternavest.adaper.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ternavest.ui.both.portfolio.PortfolioFragment;
import com.example.ternavest.ui.both.profile.ProfileFragment;
import com.example.ternavest.ui.peternak.kelola.proyek.KelolaFragment;

public class PeternakPagerAdapter extends FragmentPagerAdapter {
    public PeternakPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new KelolaFragment();
                break;

            case 1:
                fragment = new PortfolioFragment();
                break;

            case 2:
                fragment = new ProfileFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
