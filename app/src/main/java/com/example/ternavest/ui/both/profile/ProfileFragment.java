package com.example.ternavest.ui.both.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ternavest.MainActivity;
import com.example.ternavest.R;
import com.example.ternavest.model.Profile;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.VERIF_APPROVED;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Profile profile;

    private TextView tvLevel, tvAddress;
    private Button btnKtp, btnPhone, btnWhatsApp;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        CircleImageView imgPhoto = view.findViewById(R.id.img_photo_profile);
        TextView tvName = view.findViewById(R.id.tv_name_profile);
        tvLevel = view.findViewById(R.id.tv_level_profile);
        TextView tvEmail = view.findViewById(R.id.tv_email_profile);
        tvAddress = view.findViewById(R.id.tv_address_profile);

        Button btnVerification = view.findViewById(R.id.btn_verification_profile);
        btnVerification.setOnClickListener(this);
        if (firebaseUser.isEmailVerified()) btnVerification.setVisibility(View.GONE);

        btnKtp = view.findViewById(R.id.btn_ktp_profile);
        btnPhone = view.findViewById(R.id.btn_phone_profile);
        btnWhatsApp = view.findViewById(R.id.btn_whatsapp_profile);
        Button btnSettings = view.findViewById(R.id.btn_settings_profile);
        Button btnResetPassword = view.findViewById(R.id.btn_reset_password_profile);
        Button btnAbout = view.findViewById(R.id.btn_about_profile);
        Button btnLogOut = view.findViewById(R.id.btn_logout_profile);
        btnKtp.setOnClickListener(this);
        btnPhone.setEnabled(false);
        btnWhatsApp.setEnabled(false);
        btnSettings.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);

        btnKtp.setVisibility(View.GONE);
        loadImageFromUrl(imgPhoto, firebaseUser.getPhotoUrl().toString());
        tvName.setText(firebaseUser.getDisplayName());
        tvEmail.setText(firebaseUser.getEmail());

        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.loadData();
        profileViewModel.getData().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(Profile result) {
                profile = result;

                if (profile.getVerificationStatus().equals(VERIF_APPROVED)){
                    btnKtp.setText("Akun terverifikasi");
                    ((MaterialButton) btnKtp).setIcon(getResources().getDrawable(R.drawable.ic_verified));
                    btnKtp.setBackground(getResources().getDrawable(R.drawable.bg_button_menu_blue));
                } else {
                    btnKtp.setText("Akun belum diverifikasi");
                    ((MaterialButton) btnKtp).setIcon(getResources().getDrawable(R.drawable.ic_warning));
                    btnKtp.setBackground(getResources().getDrawable(R.drawable.bg_button_menu_red));
                }
                btnKtp.setVisibility(View.VISIBLE);

                if (profile.getLevel().equals(LEVEL_PETERNAK)) tvLevel.setText("Peternak");
                else if (profile.getLevel().equals(LEVEL_INVESTOR)) tvLevel.setText("Investor");

                tvAddress.setText(profile.getAddress());
                btnPhone.setText(profile.getPhone());
                btnWhatsApp.setText(profile.getWhatsApp());
            }
        });
        /*profileViewModel.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) Log.w(TAG, "Listen failed", error);
                else if (value != null && !value.isEmpty()){
                    profileViewModel.loadData();
                    Log.d(TAG, "Changes detected");
                }
            }
        });*/
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ktp_profile:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                if (profile.getVerificationStatus().equals(VERIF_APPROVED)){
                    dialog.setTitle("Akun terverifikasi");
                    dialog.setMessage("Akunmu telah diverifikasi oleh Tim Ternavest.");
                } else {
                    dialog.setTitle("Akun belum diverifikasi");
                    dialog.setMessage("Kamu bisa mengajukan verifikasi melalui pengaturan profil dengan cara mengunggah foto KTP.");
                }

                dialog.setNeutralButton("Tutup", null);
                dialog.create().show();
                break;

            case R.id.btn_verification_profile:
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Kirim tautan verifikasi")
                        .setMessage("Email Anda belum diverifikasi. Kirim tautan verifikasi ke email Anda?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseUser.sendEmailVerification();
                                showToast(getContext(), "Tautan verifikasi berhasil dikirim ke " + firebaseUser.getEmail() + ".");
                            }
                        }).create().show();
                break;

            case R.id.btn_settings_profile:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_reset_password_profile:
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Setel ulang kata sandi")
                        .setMessage("Kirim tautan penyetelan ulang kata sandi ke email Anda?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.sendPasswordResetEmail(firebaseUser.getEmail());
                                showToast(getContext(), "Tautan penyetelan ulang kata sandi berhasil dikirim ke " + firebaseUser.getEmail() + ".");
                            }
                        }).create().show();
                break;

            case R.id.btn_about_profile:
                break;

            case R.id.btn_logout_profile:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Keluar akun")
                        .setMessage("Apakah Anda yakin ingin keluar?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build();
                                GoogleSignIn.getClient(getActivity(), gso).signOut();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            }
                        })
                        .create().show();
                break;
        }
    }
}