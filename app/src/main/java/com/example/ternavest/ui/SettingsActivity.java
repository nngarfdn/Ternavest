package com.example.ternavest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.model.Profile;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.VERIF_APPROVED;
import static com.example.ternavest.utils.AppUtils.VERIF_PENDING;
import static com.example.ternavest.utils.AppUtils.VERIF_REJECT;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.EditTextUtils.getFixText;
import static com.example.ternavest.utils.EditTextUtils.isNull;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PROFILE = "extra_profile";

    private FirebaseUser firebaseUser;
    private Profile profile;
    private ProfileViewModel profileViewModel;

    private CircleImageView imgPhoto;
    private EditText edtName, edtAddress, edtPhone, edtWhatsApp, edtAccontBank, edtAccountNumber, edtAccountName;
    private ImageView imgKtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Button btnPhoto = findViewById(R.id.btn_photo_settings);
        btnPhoto.setOnClickListener(this);

        Button btnSave = findViewById(R.id.btn_save_settings);
        btnSave.setOnClickListener(this);

        imgPhoto = findViewById(R.id.img_photo_settings);
        imgKtp = findViewById(R.id.img_ktp_settings);

        CardView cvVerification = findViewById(R.id.cv_verification_settings);
        TextView tvVerification = findViewById(R.id.tv_verification_settings);

        edtName = findViewById(R.id.edt_name_settings);
        edtAddress = findViewById(R.id.edt_address_settings);
        edtPhone = findViewById(R.id.edt_phone_settings);
        edtWhatsApp = findViewById(R.id.edt_whatsapp_settings);
        edtAccontBank = findViewById(R.id.edt_account_bank_settings);
        edtAccountNumber = findViewById(R.id.edt_account_number_settings);
        edtAccountName = findViewById(R.id.edt_account_name_settings);

        LinearLayout layoutAccount = findViewById(R.id.layout_account_settings);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PROFILE)){
            profile = intent.getParcelableExtra(EXTRA_PROFILE);

            loadImageFromUrl(imgPhoto, firebaseUser.getPhotoUrl().toString());
            loadImageFromUrl(imgKtp, profile.getKtp());

            edtName.setText(firebaseUser.getDisplayName());
            edtAddress.setText(profile.getAddress());
            edtPhone.setText(profile.getPhone());
            edtWhatsApp.setText(profile.getWhatsApp());

            edtAccontBank.setText(profile.getAccountBank());
            edtAccountNumber.setText(profile.getAccountNumber());
            edtAccountName.setText(profile.getAccountName());

            switch (profile.getVerificationStatus()) {
                case VERIF_APPROVED:
                    tvVerification.setText("Terverifikasi");
                    cvVerification.setCardBackgroundColor(getResources().getColor(R.color.green));
                    break;
                case VERIF_PENDING:
                    tvVerification.setText("Menunggu verifikasi");
                    cvVerification.setCardBackgroundColor(getResources().getColor(R.color.orange));
                    break;
                case VERIF_REJECT:
                    tvVerification.setText("Ditolak");
                    cvVerification.setCardBackgroundColor(getResources().getColor(R.color.red));
                    break;
            }

            if (profile.getLevel().equals(LEVEL_INVESTOR)) layoutAccount.setVisibility(View.GONE);
        }

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_photo_settings:

                break;

            case R.id.btn_save_settings:
                String photo = "null";
                String ktp = "";
                String name = getFixText(edtName);
                String address = getFixText(edtAddress);
                String phone = getFixText(edtPhone);
                String whatsApp = getFixText(edtWhatsApp);

                if (isNull(photo) || isNull(name) || isNull(address) || isNull(phone) || isNull(whatsApp)){
                    if (isNull(photo)) showToast(this, "Mohon memasang foto profil");
                    if (isNull(name)) showToast(this, "Mohon masukkan nama lengkap");
                    if (isNull(address)) showToast(this, "Mohon masukkan alamat");
                    if (isNull(phone)) showToast(this, "Mohon masukkan nomor telepon yang dapat dihubungi");
                    if (isNull(whatsApp)) showToast(this, "Mohon masukkan nomor WhatsApp yang dapat dihubungi");
                    return;
                }

                profile.setAddress(address);
                profile.setPhone(phone);
                profile.setWhatsApp(whatsApp);

                if (profile.getLevel().equals(LEVEL_PETERNAK)){
                    String accountBank = getFixText(edtAccontBank);
                    String accountNumber = getFixText(edtAccountNumber);
                    String accountName = getFixText(edtAccountName);

                    if (isNull(accountBank) || isNull(accountNumber) || isNull(accountName)){
                        if (isNull(accountBank)) showToast(this, "Mohon pilih nama bank yang sesuai");
                        if (isNull(accountNumber)) showToast(this, "Mohon masukkan nomor rekening");
                        if (isNull(accountName)) showToast(this, "Mohon masukkan nama pemilik rekening");
                        return;
                    }

                    profile.setAccountBank(accountBank);
                    profile.setAccountNumber(accountNumber);
                    profile.setAccountName(accountName);
                }

                // Simpan ke Authentication
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        //.setPhotoUri(Uri.parse(photo))
                        .build();
                firebaseUser.updateProfile(profileUpdates);

                // Simpan ke Firestore
                profileViewModel.update(profile);

                finish();
                break;
        }
    }
}