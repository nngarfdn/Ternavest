package com.example.ternavest.ui.both.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.ternavest.R;
import com.example.ternavest.adapter.pager.MainPagerAdapter;
import com.example.ternavest.customview.LoadingDialog;
import com.example.ternavest.model.Profile;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private LoadingDialog loadingDialog;
    private Profile profile;
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialog(this);
        userPreference = new UserPreference(this);

        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile result) {
                profile = result;
                userPreference.setUserLevel(profile.getLevel());
                setBottomBar(userPreference.getUserLevel());
                loadingDialog.dismiss();
            }
        });

        if (userPreference.getUserLevel() == null){
            loadingDialog.show();
            // Simpan level user ke preference
            profileViewModel.loadData();
        } else {
            setBottomBar(userPreference.getUserLevel());
        }
    }

    private void setBottomBar(String userLevel) {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), userLevel);
        ViewPager viewPager = findViewById(R.id.view_pager_main);
        viewPager.setAdapter(pagerAdapter);

        ReadableBottomBar bottomBar = findViewById(R.id.bn_main);
        bottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int index) {
                viewPager.setCurrentItem(index);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                bottomBar.selectItem(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}