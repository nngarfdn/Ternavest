package com.example.ternavest.ui.both.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.ternavest.R;
import com.example.ternavest.customview.LoadingDialog;
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
import static com.example.ternavest.utils.AppUtils.isValidPhone;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.loadProfilePicFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.EditTextUtils.getFixText;
import static com.example.ternavest.utils.EditTextUtils.isNull;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_PROFILE_IMAGE = 100;
    private static final int RC_KTP_IMAGE = 200;

    private FirebaseUser firebaseUser;
    private LoadingDialog loadingDialog;
    private Profile profile;
    private ProfileViewModel profileViewModel;
    private Uri uriKTP;

    private Button btnKtp;
    private CardView cvVerification;
    private CircleImageView imgPhoto;
    private EditText edtName, edtAddress, edtPhone, edtWhatsApp, edtAccontBank, edtAccountNumber, edtAccountName;
    private ImageView imgKtp;
    private TextView tvVerification, tvKtpRejectionNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnPhoto = findViewById(R.id.btn_photo_settings);
        btnKtp = findViewById(R.id.btn_ktp_settings);
        Button btnSave = findViewById(R.id.btn_save_settings);
        ImageButton ibKtpHelp = findViewById(R.id.ib_ktp_help_settings);
        btnPhoto.setOnClickListener(this);
        btnKtp.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        ibKtpHelp.setOnClickListener(this);

        imgPhoto = findViewById(R.id.img_photo_settings);
        imgKtp = findViewById(R.id.img_ktp_settings);

        cvVerification = findViewById(R.id.cv_verification_settings);
        tvVerification = findViewById(R.id.tv_verification_settings);
        tvKtpRejectionNote = findViewById(R.id.tv_ktp_rejection_note_settings);
        tvKtpRejectionNote.setVisibility(View.GONE);

        edtName = findViewById(R.id.edt_name_settings);
        edtAddress = findViewById(R.id.edt_address_settings);
        edtPhone = findViewById(R.id.edt_phone_settings);
        edtWhatsApp = findViewById(R.id.edt_whatsapp_settings);
        edtAccontBank = findViewById(R.id.edt_account_bank_settings);
        edtAccountNumber = findViewById(R.id.edt_account_number_settings);
        edtAccountName = findViewById(R.id.edt_account_name_settings);

        LinearLayout layoutAccount = findViewById(R.id.layout_account_settings);
        layoutAccount.setVisibility(View.GONE);

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.loadData();
        profileViewModel.getData().observe(this, result -> {
            profile = result;

            loadProfilePicFromUrl(imgPhoto, profile.getPhoto());
            loadImageFromUrl(imgKtp, profile.getKtp());

            edtName.setText(profile.getName());
            edtAddress.setText(profile.getAddress());
            edtPhone.setText(profile.getPhone());
            edtWhatsApp.setText(profile.getWhatsApp());

            edtAccontBank.setText(profile.getAccountBank());
            edtAccountNumber.setText(profile.getAccountNumber());
            edtAccountName.setText(profile.getAccountName());

            setVerificationStatus(profile.getVerificationStatus());

            if (profile.getLevel().equals(LEVEL_INVESTOR)) layoutAccount.setVisibility(View.GONE);
            else if (profile.getLevel().equals(LEVEL_PETERNAK)) layoutAccount.setVisibility(View.VISIBLE);

            loadingDialog.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setVerificationStatus(String verificationStatus){
        switch (verificationStatus) {
            case VERIF_APPROVED:
                tvVerification.setText("Terverifikasi");
                cvVerification.setCardBackgroundColor(getResources().getColor(R.color.blue));
                tvKtpRejectionNote.setVisibility(View.GONE);
                break;
            case VERIF_PENDING:
                tvVerification.setText("Menunggu verifikasi");
                cvVerification.setCardBackgroundColor(getResources().getColor(R.color.orange));
                tvKtpRejectionNote.setVisibility(View.GONE);
                break;
            case VERIF_REJECT:
                tvVerification.setText("Ditolak");
                cvVerification.setCardBackgroundColor(getResources().getColor(R.color.red));
                tvKtpRejectionNote.setVisibility(View.VISIBLE);
                tvKtpRejectionNote.setText("Pengajuanmu ditolak karena " + profile.getVerificationRejectionNote());
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_photo_settings:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih foto profil"), RC_PROFILE_IMAGE);
                break;

            case R.id.btn_ktp_settings:
                if (profile.getVerificationStatus().equals(VERIF_APPROVED)){
                    showToast(this, "Akunmu telah diverifikasi!");
                    btnKtp.setEnabled(false);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Ajukan verifikasi KTP")
                            .setMessage("Apakah Anda ingin mengunggah KTP dan mengajukan verifikasi akun?")
                            .setNeutralButton("Batal", null)
                            .setNegativeButton("Tidak", null)
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "Ambil foto KTP");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "Menggunakan kamera");
                                uriKTP = getContentResolver().insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, uriKTP);
                                startActivityForResult(intent1, RC_KTP_IMAGE);
                            }).create().show();
                }
                break;

            case R.id.btn_save_settings:
                String name = getFixText(edtName);
                String address = getFixText(edtAddress);
                String phone = getFixText(edtPhone);
                String whatsApp = getFixText(edtWhatsApp);

                if (isNull(name) || isNull(address) || isNull(phone) || isNull(whatsApp)){
                    if (isNull(name)) showToast(this, "Nama lengkap belum dimasukkan");
                    else if (isNull(address)) showToast(this, "Alamat belum dimasukkan");
                    else if (isNull(phone)) showToast(this, "Mohon masukkan nomor telepon yang dapat dihubungi");
                    else if (isNull(whatsApp)) showToast(this, "Mohon masukkan nomor WhatsApp yang dapat dihubungi");
                    return;
                }

                if (!isValidPhone(phone) || !isValidPhone(whatsApp)){
                    if (!isValidPhone(phone)) edtPhone.setError("Awali nomor telepon dengan 62");
                    if (!isValidPhone(whatsApp)) edtWhatsApp.setError("Awali nomor WhatsApp dengan 62");
                    showToast(getApplicationContext(), "Awali nomor telepon dan WhatsApp dengan 62");
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
                        else if (isNull(accountNumber)) showToast(getApplicationContext(), "Mohon masukkan nomor rekening");
                        else if (isNull(accountName)) showToast(getApplicationContext(), "Mohon masukkan nama pemilik rekening");
                        return;
                    }

                    profile.setAccountBank(accountBank);
                    profile.setAccountNumber(accountNumber);
                    profile.setAccountName(accountName);
                }

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

            case R.id.ib_ktp_help_settings:
                new AlertDialog.Builder(this)
                        .setTitle("Bantuan Verifikasi KTP")
                        .setMessage("Untuk mengajukan verifikasi akun, kamu harus mengunggah foto KTP asli dan memasukkan nama lengkap sesuai KTP.\n\n" +
                                "Informasi pada foto KTP yang diambil juga harus jelas dan dapat dibaca.\n\n" +
                                "Setelah akun terverifikasi, kamu tidak bisa lagi mengajukan verifikasi.")
                        .setPositiveButton("Tutup", null)
                        .create().show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PROFILE_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                if (data != null) if (data.getData() != null){
                    loadingDialog.show();

                    Uri uriProfileImage = data.getData();
                    loadProfilePicFromUrl(imgPhoto, uriProfileImage.toString());

                    String fileName = firebaseUser.getUid() + ".jpeg";
                    profileViewModel.uploadImage(this, uriProfileImage, FOLDER_PROFILE, fileName, imageUrl -> {
                        profileViewModel.update(imageUrl);
                        loadingDialog.dismiss();
                    });
                }
            }
        } else if (requestCode == RC_KTP_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                try {
                    loadImageFromUrl(imgKtp, uriKTP.toString());
                    showToast(getApplicationContext(), "Mengajukan verifikasi KTP...");
                    String fileName = firebaseUser.getUid() + ".jpeg";
                    profileViewModel.uploadImage(this, uriKTP, FOLDER_KTP, fileName, imageUrl -> {
                        profileViewModel.sendVerification(imageUrl);
                        setVerificationStatus(VERIF_PENDING);
                        showToast(getApplicationContext(), "Verifikasi KTP berhasil diajukan.");
                    });
                } catch (Exception e){
                    e.printStackTrace();
                    showToast(getApplicationContext(), "Pengajuan verifikasi KTP gagal. Mohon coba lagi.");
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}