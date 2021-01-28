package com.example.ternavest.ui.both.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.ProyekInvestorAdapter;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.VERIF_APPROVED;
import static com.example.ternavest.utils.AppUtils.loadProfilePicFromUrl;

public class DetailProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PROFILE = "extra_profile";

    private Profile profile;
    private ProyekInvestorAdapter adapter;

    private final ArrayList<Proyek> projectList = new ArrayList<>();

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.rv_project_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProyekInvestorAdapter(projectList);
        recyclerView.setAdapter(adapter);

        CircleImageView imgPhoto = findViewById(R.id.img_photo_profile);
        TextView tvName = findViewById(R.id.tv_name_profile);
        TextView tvLevel = findViewById(R.id.tv_level_profile);
        TextView tvEmail = findViewById(R.id.tv_email_profile);
        TextView tvAddress = findViewById(R.id.tv_address_profile);
        TextView tvProject = findViewById(R.id.tv_project_profile);

        Button btnKtp = findViewById(R.id.btn_ktp_profile);
        Button btnPhone = findViewById(R.id.btn_phone_profile);
        Button btnWhatsApp = findViewById(R.id.btn_whatsapp_profile);
        btnKtp.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnWhatsApp.setOnClickListener(this);

        btnKtp.setVisibility(View.GONE);

        ProyekViewModel projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        // Jike melihat profil peternak
        projectViewModel.getResultByUUID().observe(this, proyeks -> {
            projectList.clear();
            projectList.addAll(proyeks);
            adapter.notifyDataSetChanged();
        });

        PortfolioViewModel portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.getData().observe(this, portfolioList -> {
            for (Portfolio portfolio : portfolioList){
                projectViewModel.loadResultByID(portfolio.getProjectId()); // Buat investor
            }
        });
        // Jika melihat profil investor
        projectViewModel.getResultByID().observe(this, proyeks -> {
            projectList.addAll(proyeks);
            adapter.notifyDataSetChanged();
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PROFILE)){
            profile = intent.getParcelableExtra(EXTRA_PROFILE);

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

            // Level dari profil yang dilihat
            if (profile.getLevel().equals(LEVEL_PETERNAK)){
                tvLevel.setText("Peternak");
                tvProject.setText("Daftar proyek yang diajukan");
                projectViewModel.loadResultByUUID(profile.getId());
            } else if (profile.getLevel().equals(LEVEL_INVESTOR)) {
                tvLevel.setText("Investor");
                tvProject.setText("Daftar portofolio proyek");
                portfolioViewModel.loadData(profile.getId(), profile.getLevel());
            }

            tvAddress.setText(profile.getAddress());
            btnPhone.setText(profile.getPhone());
            btnWhatsApp.setText(profile.getWhatsApp());

            loadProfilePicFromUrl(imgPhoto, profile.getPhoto());
            tvName.setText(profile.getName());
            tvEmail.setText(profile.getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_phone_profile:
                new  AlertDialog.Builder(this)
                        .setTitle("Panggil nomor telepon")
                        .setMessage("Pilih ya untuk memanggil nomor pengguna ini.")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            String phone = "tel:" + profile.getPhone();
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
                            startActivity(intent);
                        }).create().show();
                break;

            case R.id.btn_whatsapp_profile:
                new  AlertDialog.Builder(this)
                        .setTitle("Buka WhatsApp")
                        .setMessage("Pilih ya untuk membuka akun WhatsApp pengguna ini.")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            String url = "https://wa.me/" + profile.getWhatsApp();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }).create().show();
                break;

            case R.id.btn_ktp_profile:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                if (profile.getVerificationStatus().equals(VERIF_APPROVED)){
                    dialog.setTitle("Akun terverifikasi");
                    dialog.setMessage("Akun ini telah diverifikasi oleh Tim Ternavest.");
                } else {
                    dialog.setTitle("Akun belum diverifikasi");
                    dialog.setMessage("Akun ini belum mengajukan pemverifikasian akun.");
                }

                dialog.setPositiveButton("Tutup", null);
                dialog.create().show();
                break;
        }
    }
}