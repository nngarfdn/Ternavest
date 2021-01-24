package com.example.ternavest.testing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.ternavest.R;
import com.example.ternavest.model.Profile;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.ui.investor.main.InvestorActivity;
import com.example.ternavest.ui.peternak.main.PeternakActivity;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private Profile profile;
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        /**/
        userPreference = new UserPreference(this);

        if (firebaseUser != null){
            ProfileViewModel profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
            profileViewModel.loadData();
            profileViewModel.getData().observe(this, new Observer<Profile>() {
                @Override
                public void onChanged(Profile result) {
                    profile = result;
                    userPreference.setUserLevel(profile.getLevel());
                }
            });
        }

        if (userPreference.getUserLevel() == null){ // Nanti taro kode ini saat selesai buat akun/masuk
            // Isinya simpan level user ke preference
        }
        /**/

        // Testing
        Button btnTestWilayah = findViewById(R.id.btn_testwilayah);
        Button btnPeternak = findViewById(R.id.btn_peternak);
        Button btnInvestor = findViewById(R.id.btn_investor);
        btnTestWilayah.setOnClickListener(v -> startActivity(new Intent(this, WilayahTest.class)));
        btnPeternak.setOnClickListener(v -> startActivity(new Intent(this, PeternakActivity.class)));
        btnInvestor.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InvestorActivity.class)));
    }

    private void launchMain(String userLevel){
        Intent intent = new Intent();

        if (userLevel.equals(LEVEL_INVESTOR)) intent = new Intent(this, InvestorActivity.class);
        else if (userLevel.equals(LEVEL_PETERNAK)) intent = new Intent(this, PeternakActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Clear all previous activities
        startActivity(intent);
    }
}