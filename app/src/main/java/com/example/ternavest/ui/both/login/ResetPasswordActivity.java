package com.example.ternavest.ui.both.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ternavest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.ternavest.utils.AppUtils.showToast;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private AlertDialog dialog;
    private FirebaseAuth firebaseAuth;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_email_reset);
        Button btnSend = findViewById(R.id.btn_email_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResetPassword(edtEmail.getText().toString());
            }
        });
    }

    private void sendResetPassword(String email){
        if (!validateForm(email)) return;

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "Email sent.");
                            showToast(getApplicationContext(), "Permintaan setel ulang kata sandi telah dikirim ke email.");
                        } else {
                            Log.d(TAG, "Email sent failed.");
                            showToast(getApplicationContext(), "Email tidak terdaftar atau koneksi sedang bermasalah.");
                        }
                        dialog.dismiss();
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
}