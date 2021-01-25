package com.example.ternavest.ui.both.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ternavest.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.ternavest.utils.AppUtils.showToast;

public class ResetPasswordActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private FirebaseAuth firebaseAuth;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtEmail = findViewById(R.id.edt_email_reset);
        Button btnSend = findViewById(R.id.btn_email_send);

        btnSend.setOnClickListener(view -> sendResetPassword(edtEmail.getText().toString()));
    }

    private void sendResetPassword(String email){
        if (!validateForm(email)) return;

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "Email sent.");
                        showToast(getApplicationContext(), "Permintaan setel ulang kata sandi telah dikirim ke email.");
                        finish();
                    } else {
                        Log.d(TAG, "Email sent failed.");
                        showToast(getApplicationContext(), "Email tidak terdaftar.");
                    }
                });
    }

    private boolean validateForm(String email){
        boolean valid = true;

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Masukkan email yang valid");
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}