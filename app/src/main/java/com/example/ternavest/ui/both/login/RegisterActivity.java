package com.example.ternavest.ui.both.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.ternavest.R;
import com.example.ternavest.customview.LoadingDialog;
import com.example.ternavest.model.Profile;
import com.example.ternavest.ui.both.main.MainActivity;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.VERIF_PENDING;
import static com.example.ternavest.utils.AppUtils.showToast;

public class RegisterActivity extends AppCompatActivity {
    public static final String EXTRA_LEVEL = "extra_level";
    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;
    private ProfileViewModel profileViewModel;

    private EditText edtName, edtEmail, edtPassword, edtKonfirmPass;

    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initalize
        loadingDialog = new LoadingDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize view
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtName = findViewById(R.id.edt_name_register);
        edtEmail = findViewById(R.id.edt_email_register);
        edtPassword = findViewById(R.id.edt_password_register);
        edtKonfirmPass = findViewById(R.id.edt_konfirm_register);

        Button btnRegister = findViewById(R.id.btn_email_register);
        btnRegister.setOnClickListener(view -> registerWithEmail(edtName.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString(), edtKonfirmPass.getText().toString()));

        level = getIntent().getStringExtra(EXTRA_LEVEL);
        if (level.equals(LEVEL_PETERNAK)) toolbar.setTitle("Daftar sebagai Peternak");
        else if (level.equals(LEVEL_INVESTOR)) toolbar.setTitle("Daftar sebagai Investor");

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
    }

    private void registerWithEmail(String name, String email, String password, String konfirm){
        if (!validateForm(name, email, password, konfirm)) return;
        Log.d(TAG, "createAccount: " + email);

        loadingDialog.show();

        // Start create user with email
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Profile updates
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> Log.d(TAG, "User profile updated."));

                        // Insert ke database
                        Profile profile = new Profile(
                                firebaseUser.getUid(),
                                name,
                                firebaseUser.getEmail(),
                                level,
                                VERIF_PENDING,
                                null);
                        profileViewModel.insert(profile);

                        Log.d(TAG, "createUserWithEmail: success");
                        launchMain();
                    } else {
                        // If sign in fails, display a message to the user
                        Log.w(TAG, "createUserWithEmail: failure", task.getException());
                        showToast(getApplicationContext(), "Email sudah terdaftar.");
                    }
                    loadingDialog.dismiss();
                });
    }

    private boolean validateForm(String name, String email, String password, String konfirm){
        boolean valid = true;

        if (TextUtils.isEmpty(name)){
            edtName.setError("Masukkan nama lengkap");
            valid = false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Masukkan email yang valid");
            valid = false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) { // Syarat Firebase Auth
            edtPassword.setError("Masukkan kata sandi minimal 6 karakter");
            valid = false;
        }

        if (!password.equals(konfirm)) {
            edtKonfirmPass.setError("Konfirmasi kata sandi tidak sama");
            valid = false;
        }

        return valid;
    }

    private void launchMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  // Clear all previous activities
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}