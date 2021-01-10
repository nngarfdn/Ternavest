package com.example.ternavest.ui.both.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.ternavest.callback.OnImageUploadCallback;
import com.example.ternavest.model.Profile;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.repository.ProfileRepository.FOLDER_KTP;
import static com.example.ternavest.repository.ProfileRepository.FOLDER_PROFILE;
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
    private static final int RC_PROFILE_IMAGE = 100;
    private static final int RC_KTP_IMAGE = 200;

    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Button btnPhoto = findViewById(R.id.btn_photo_settings);
        Button btnKtp = findViewById(R.id.btn_ktp_settings);
        Button btnSave = findViewById(R.id.btn_save_settings);
        btnPhoto.setOnClickListener(this);
        btnKtp.setOnClickListener(this);
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

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.loadData();
        profileViewModel.getData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile result) {
                profile = result;

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

                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_photo_settings:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih foto profil:"), RC_PROFILE_IMAGE);
                break;

            case R.id.btn_ktp_settings:
                new AlertDialog.Builder(this)
                        .setTitle("Ajukan verifikasi KTP")
                        .setMessage("Apakah Anda ingin mengunggah KTP dan mengajukan verifikasi akun?")
                        .setNeutralButton("Batal", null)
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intentKtp = new Intent(Intent.ACTION_PICK);
                                intentKtp.setType("image/*");
                                startActivityForResult(Intent.createChooser(intentKtp, "Pilih foto ktp:"), RC_KTP_IMAGE);
                            }
                        }).create().show();
                break;

            case R.id.btn_save_settings:
                String name = getFixText(edtName);
                String address = getFixText(edtAddress);
                String phone = getFixText(edtPhone);
                String whatsApp = getFixText(edtWhatsApp);

                if (isNull(name) || isNull(address) || isNull(phone) || isNull(whatsApp)){
                    if (isNull(name)) showToast(this, "Nama lengkap belum dimasukkan");
                    if (isNull(address)) showToast(this, "Alamat belum dimasukkan");
                    if (isNull(phone)) showToast(this, "Mohon masukkan nomor telepon yang dapat dihubungi");
                    if (isNull(whatsApp)) showToast(this, "Mohon masukkan nomor WhatsApp yang dapat dihubungi");
                    return;
                }

                profile.setName(name);
                profile.setAddress(address);
                profile.setPhone(phone);
                profile.setWhatsApp(whatsApp);

                if (profile.getLevel().equals(LEVEL_PETERNAK)){
                    String accountBank = getFixText(edtAccontBank);
                    String accountNumber = getFixText(edtAccountNumber);
                    String accountName = getFixText(edtAccountName);

                    if (isNull(accountBank) || isNull(accountNumber) || isNull(accountName)){
                        if (isNull(accountBank)) showToast(getApplicationContext(), "Mohon pilih nama bank yang sesuai");
                        if (isNull(accountNumber)) showToast(getApplicationContext(), "Mohon masukkan nomor rekening");
                        if (isNull(accountName)) showToast(getApplicationContext(), "Mohon masukkan nama pemilik rekening");
                        return;
                    }

                    profile.setAccountBank(accountBank);
                    profile.setAccountNumber(accountNumber);
                    profile.setAccountName(accountName);
                }

                // Ambil dari Firebase Auth
                profile.setId(firebaseUser.getUid());
                profile.setEmail(firebaseUser.getEmail());

                // Simpan nama ke Authentication
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                firebaseUser.updateProfile(profileUpdates);

                // Simpan ke Firestore
                profileViewModel.update(profile);

                showToast(getApplicationContext(), "Pengaturan profil berhasil disimpan.");
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PROFILE_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                if (data != null) if (data.getData() != null){
                    progressDialog.show();

                    Uri uriProfileImage = data.getData();
                    loadImageFromUrl(imgPhoto, uriProfileImage.toString());

                    String fileName = firebaseUser.getUid() + ".jpeg";
                    profileViewModel.uploadImage(this, uriProfileImage, FOLDER_PROFILE, fileName, new OnImageUploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            profile.setPhoto(imageUrl);

                            // Simpan foto ke Authentication
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(imageUrl))
                                    .build();
                            firebaseUser.updateProfile(profileUpdates);

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        } else if (requestCode == RC_KTP_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                if (data != null) if (data.getData() != null){
                    Uri uriKtpImage = data.getData();
                    loadImageFromUrl(imgKtp, uriKtpImage.toString());

                    showToast(getApplicationContext(), "Mengajukan verifikasi KTP...");

                    String fileName = firebaseUser.getUid() + ".jpeg";
                    profileViewModel.uploadImage(this, uriKtpImage, FOLDER_KTP, fileName, new OnImageUploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            profileViewModel.sendVerification(imageUrl);
                            showToast(getApplicationContext(), "Verifikasi KTP berhasil diajukan.");
                        }
                    });
                }
            }
        }
    }
}