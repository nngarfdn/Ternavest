package com.example.ternavest.testing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.ternavest.R;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.ui.investor.main.InvestorActivity;
import com.example.ternavest.ui.peternak.main.PeternakActivity;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private Profile profile;
    private UserPreference userPreference;

    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        /**/
        userPreference = new UserPreference(this);

        if (firebaseAuth.getCurrentUser() != null){
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

        Button btnTestWilayah = findViewById(R.id.btn_testwilayah);
        btnTestWilayah.setOnClickListener(v -> startActivity(new Intent(this, WilayahTest.class)));

        Button btnPeternak = findViewById(R.id.btn_peternak);
        btnPeternak.setOnClickListener(v -> {
            startActivity(new Intent(this, PeternakActivity.class));
        });

        Button btnGoogle = findViewById(R.id.btn_google_login);
        btnGoogle.setOnClickListener(v -> loginWithGoogle());
        Button btnInvestor = findViewById(R.id.btn_investor);
        btnInvestor.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InvestorActivity.class)));

        // Contoh query peminat
        PortfolioViewModel portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.queryPeminat("kTAS8lKMYuw9nlSAYRaW");
        portfolioViewModel.getData().observe(this, new Observer<ArrayList<Portfolio>>() {
            @Override
            public void onChanged(ArrayList<Portfolio> result) {
                for (Portfolio portfolio : result){
                    Log.d(TAG, "Peminat: " + portfolio.getInvestorId());
                }
            }
        });
    }

    private void loginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed or user press back button
                Log.w(TAG, "Google sign in failed", e);
                //showSnackbar(getActivity().findViewById(R.id.activity_login), "Email gagal diautentikasi.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//        loadingDialog.show();

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's informationZ
                            Log.d(TAG, "signInWithCredential: success");
                            launchMain();
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithCredential: failure", task.getException());
//                            showSnackbar(this.findViewById(R.id.activity_login), "Email gagal diautentikasi.");

                        }

//                        loadingDialog.dismiss();
                    }
                });
    }

    private void launchMain() {
        Intent intent = new Intent(MainActivity.this, PeternakActivity.class);
        startActivity(intent);
        this.finish();
    }

}