package com.example.ternavest.adapter.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ternavest.ui.both.portfolio.PortfolioFragment;
import com.example.ternavest.ui.both.profile.ProfileFragment;
import com.example.ternavest.ui.investor.home.HomeFragment;
import com.example.ternavest.ui.peternak.kelola.proyek.KelolaFragment;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private final String USER_LEVEL;

    public MainPagerAdapter(@NonNull FragmentManager fm, String userLevel) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.USER_LEVEL = userLevel;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                if (USER_LEVEL.equals(LEVEL_PETERNAK)) fragment = new KelolaFragment();
                else if (USER_LEVEL.equals(LEVEL_INVESTOR)) fragment = new HomeFragment();
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
