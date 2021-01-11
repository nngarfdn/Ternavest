package com.example.ternavest.ui.both.portfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.adaper.PaymentAdapter;
import com.example.ternavest.model.Payment;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.ui.both.profile.DetailProfileActivity;
import com.example.ternavest.ui.invest.PaymentActivity;
import com.example.ternavest.viewmodel.PaymentViewModel;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.example.ternavest.viewmodel.ProyekViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.ui.both.profile.DetailProfileActivity.EXTRA_PROFILE;
import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.DateUtils.differenceOfDates;
import static com.example.ternavest.utils.DateUtils.getCurrentDate;

public class DetailPortfolioActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PORTFOLIO = "extra_portfolio";

    private ImageButton ibDelete;
    private CircleImageView civProfile;
    private TextView tvTitle, tvProject, tvTotalCost, tvCount, tvAction, tvProfile, tvStatusProject, tvStatusPayment, tvLevel, tvPayment;
    private Button btnUpdate, btnPayment;
    private CardView cvProfile;

    private Profile profile;
    private Proyek project;
    private PaymentAdapter adapter;
    private Portfolio portfolio;
    private PaymentViewModel paymentViewModel;
    private ProyekViewModel projectViewModel;
    private ProfileViewModel profileViewModel;
    private PortfolioViewModel portfolioViewModel;
    private UserPreference userPreference;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_portfolio);

        userPreference = new UserPreference(this);

        RecyclerView recyclerView = findViewById(R.id.rv_payment_dpf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter();
        recyclerView.setAdapter(adapter);

        tvTitle = findViewById(R.id.tv_title_portfolio);
        tvProject = findViewById(R.id.tv_project_portfolio);
        tvTotalCost = findViewById(R.id.tv_total_cost_portfolio);
        tvCount = findViewById(R.id.tv_count_portfolio);
        tvAction = findViewById(R.id.tv_status_portfolio);
        tvStatusPayment = findViewById(R.id.tv_status_payment_dpf);
        tvStatusProject = findViewById(R.id.tv_status_project_dpf);
        tvLevel = findViewById(R.id.tv_level_dpf);
        tvPayment = findViewById(R.id.tv_payment_dpf);
        tvProfile = findViewById(R.id.tv_name_profile);
        civProfile = findViewById(R.id.civ_photo_profile);

        cvProfile = findViewById(R.id.cv_profile_profile);
        ibDelete = findViewById(R.id.ib_delete_dpf);
        btnUpdate = findViewById(R.id.btn_update_dpf);
        btnPayment = findViewById(R.id.btn_payment_dpf);
        cvProfile.setOnClickListener(this);
        ibDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnPayment.setOnClickListener(this);

        tvAction.setText("Lihat proyek");
        if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)){
            ibDelete.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnPayment.setVisibility(View.INVISIBLE);
        }
        cvProfile.setEnabled(false);

        projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        projectViewModel.getResultByID().observe(this, new Observer<List<Proyek>>() {
            @Override
            public void onChanged(List<Proyek> projectList) {
                project = projectList.get(0);

                // Atur informasi proyek dan portfolio
                tvTitle.setText(project.getNamaProyek());
                tvProject.setText(project.getNamaProyek());
                tvTotalCost.setText("Rp" + (portfolio.getCount() * project.getBiayaHewan()));

                // Atur status proyek
                if (differenceOfDates(project.getWaktuMulai(), getCurrentDate()) > 0){
                    tvStatusProject.setText(", proyek belum dimulai");
                } else if (project.getWaktuSelesai().equals(getCurrentDate())){
                    tvStatusProject.setText(", proyek sudah selesai");
                } else {
                    tvStatusProject.setText(", proyek sedang berjalan");
                }
            }
        });

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile result) {
                profile = result;

                // Atur informasi profil
                tvProfile.setText(profile.getName());
                loadImageFromUrl(civProfile, profile.getPhoto());
                cvProfile.setEnabled(true);
            }
        });

        paymentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PaymentViewModel.class);
        paymentViewModel.getData().observe(this, new Observer<ArrayList<Payment>>() {
            @Override
            public void onChanged(ArrayList<Payment> paymentList) {
                adapter.setData(paymentList);

                // Atur status pembayaran terakhir
                if (adapter.getItemCount() > 0){
                    String lastStatus = adapter.getData().get(adapter.getItemCount()-1).getStatus();
                    if (lastStatus.equals(PAY_APPROVED)){
                        tvStatusPayment.setText("Disetujui");
                        tvStatusPayment.setTextColor(R.color.blue);
                        tvStatusProject.setVisibility(View.VISIBLE);

                        ibDelete.setVisibility(View.INVISIBLE);
                        btnUpdate.setVisibility(View.INVISIBLE);
                        btnPayment.setVisibility(View.INVISIBLE);
                    } else if (lastStatus.equals(PAY_PENDING)){
                        tvStatusPayment.setText("Menunggu persetujuan");
                        tvStatusPayment.setTextColor(R.color.orange);

                        ibDelete.setVisibility(View.INVISIBLE);
                        btnUpdate.setVisibility(View.INVISIBLE);
                        btnPayment.setVisibility(View.INVISIBLE);
                    } else if (lastStatus.equals(PAY_REJECT)){
                        tvStatusPayment.setText("Belum bayar");
                        tvStatusPayment.setTextColor(R.color.orange);
                    }
                } else {
                    tvStatusPayment.setText("Belum bayar");
                    tvStatusPayment.setTextColor(R.color.orange);

                    tvPayment.setVisibility(View.INVISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PORTFOLIO)){
            portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

            tvCount.setText(" / " + portfolio.getCount() + " ekor");

            projectViewModel.loadResultByID(portfolio.getProjectId());
            paymentViewModel.loadData(portfolio.getId());

            if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)){ // Tampilan untuk investor
                profileViewModel.loadData(portfolio.getBreederId());
                tvLevel.setText("Peternak");
            } if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) { // Tampilan untuk peternak
                profileViewModel.loadData(portfolio.getInvestorId());
                tvLevel.setText("Investor");
            }
        }

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_delete_dpf:
                new AlertDialog.Builder(this)
                        .setTitle("Batalkan investasi")
                        .setMessage("Apakah Anda yakin ingin membatalkan investasi pada proyek ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                portfolioViewModel.delete(portfolio);
                                onBackPressed();
                            }
                        }).create().show();
                break;

            case R.id.cv_profile_profile:
                Intent intentProfile = new Intent(this, DetailProfileActivity.class);
                intentProfile.putExtra(EXTRA_PROFILE, profile);
                startActivity(intentProfile);
                break;

            case R.id.btn_update_dpf:
                Intent intentUpdate = new Intent(this, AddUpdatePortfolioActivity.class);
                intentUpdate.putExtra(EXTRA_PORTFOLIO, portfolio);
                startActivity(intentUpdate);
                break;

            case R.id.btn_payment_dpf:
                if (differenceOfDates(project.getWaktuMulai(), getCurrentDate()) > 0){ // Proyek masih belum mulai
                    Intent intentPayment = new Intent(this, PaymentActivity.class);
                    intentPayment.putExtra(EXTRA_PORTFOLIO, portfolio);
                    startActivity(intentPayment);
                } else { // Proyek sudah mulai atau selesai
                    new AlertDialog.Builder(this)
                            .setTitle("Proyek sudah dimulai")
                            .setMessage("Anda sudah tidak bisa mengajukan pembayaran.")
                            .setPositiveButton("Tutup", null)
                            .create().show();
                }
                break;
        }
    }
}