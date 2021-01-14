package com.example.ternavest.ui.both.portfolio;

import androidx.annotation.Nullable;
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
import com.example.ternavest.adapter.recycler.PaymentAdapter;
import com.example.ternavest.customview.LoadingDialog;
import com.example.ternavest.model.Payment;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.ui.both.profile.DetailProfileActivity;
import com.example.ternavest.ui.investor.portfolio.PaymentActivity;
import com.example.ternavest.ui.peternak.kelola.proyek.DetailFragment;
import com.example.ternavest.viewmodel.PaymentViewModel;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.ui.both.portfolio.AddUpdatePortfolioActivity.RC_UPDATE_PORTFOLIO;
import static com.example.ternavest.ui.both.profile.DetailProfileActivity.EXTRA_PROFILE;
import static com.example.ternavest.ui.investor.portfolio.PaymentActivity.RC_ADD_PAYMENT;
import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.DateUtils.differenceOfDates;
import static com.example.ternavest.utils.DateUtils.getCurrentDate;

public class DetailPortfolioActivity extends AppCompatActivity implements View.OnClickListener, DetailPaymentFragment.DetailPaymentListener {
    public static final String EXTRA_PAYMENT = "extra_payment";
    public static final String EXTRA_PORTFOLIO = "extra_portfolio";
    public static final String EXTRA_PROJECT = "extra_project";

    private ImageButton ibDelete;
    private CircleImageView civProfile;
    private TextView tvTitle, tvProject, tvTotalCost, tvCount, tvAction, tvProfile, tvStatusProject, tvStatusPayment, tvLevel, tvPayment;
    private Button btnUpdate, btnPayment;
    private CardView cvProfile, cvPortfolio;

    private LoadingDialog loadingDialog;
    private Profile profile;
    private Proyek project;
    private PaymentAdapter adapter;
    private Portfolio portfolio;
    private PaymentViewModel paymentViewModel;
    private ProfileViewModel profileViewModel;
    private PortfolioViewModel portfolioViewModel;
    private UserPreference userPreference;

    private ArrayList<Payment> paymentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_portfolio);

        userPreference = new UserPreference(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        RecyclerView recyclerView = findViewById(R.id.rv_payment_dpf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter();
        recyclerView.setAdapter(adapter);

        tvTitle = findViewById(R.id.tv_title_dpf);
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

        cvPortfolio = findViewById(R.id.cv_portfolio_portfolio);
        cvProfile = findViewById(R.id.cv_profile_profile);
        ibDelete = findViewById(R.id.ib_delete_dpf);
        btnUpdate = findViewById(R.id.btn_update_dpf);
        btnPayment = findViewById(R.id.btn_payment_dpf);
        cvPortfolio.setOnClickListener(this);
        cvProfile.setOnClickListener(this);
        ibDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnPayment.setOnClickListener(this);

        tvAction.setText("Lihat proyek");
        if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)){
            ibDelete.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnPayment.setVisibility(View.INVISIBLE);
        }
        cvProfile.setEnabled(false);

        // Default riwayat pembayaran -> size 0
        tvStatusPayment.setText("Belum bayar");
        tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));
        tvPayment.setVisibility(View.INVISIBLE);

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile result) {
                profile = result;

                // Atur informasi profil
                tvProfile.setText(profile.getName());
                loadImageFromUrl(civProfile, profile.getPhoto());
                cvProfile.setEnabled(true);

                loadingDialog.dismiss();
            }
        });

        paymentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PaymentViewModel.class);
        paymentViewModel.getData().observe(this, new Observer<ArrayList<Payment>>() {
            @Override
            public void onChanged(ArrayList<Payment> result) {
                paymentList = result;
                adapter.setData(paymentList);

                // Atur status pembayaran terakhir
                if (adapter.getItemCount() > 0){
                    setLastPaymentStatus(adapter.getData().get(adapter.getItemCount()-1).getStatus());
                } // Saat 0, sudah diatur di atas
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PORTFOLIO)){
            portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

            tvCount.setText(" / " + portfolio.getCount() + " ekor");

            if (intent.hasExtra(EXTRA_PROJECT)){
                project = intent.getParcelableExtra(EXTRA_PROJECT);

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

    private void setLastPaymentStatus(String lastStatus) {
        tvPayment.setVisibility(View.VISIBLE);
        if (lastStatus.equals(PAY_APPROVED)){
            tvStatusPayment.setText("Disetujui");
            tvStatusPayment.setTextColor(getResources().getColor(R.color.blue));
            tvStatusProject.setVisibility(View.VISIBLE);

            ibDelete.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnPayment.setVisibility(View.INVISIBLE);
        } else if (lastStatus.equals(PAY_PENDING)){
            tvStatusPayment.setText("Menunggu persetujuan");
            tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));

            ibDelete.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnPayment.setVisibility(View.INVISIBLE);
        } else if (lastStatus.equals(PAY_REJECT)){
            tvStatusPayment.setText("Belum bayar");
            tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));
        }
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
                                for (Payment payment : paymentList){
                                    paymentViewModel.delete(portfolio.getId(), payment.getId());
                                    paymentViewModel.deleteImage(payment.getImage());
                                }
                                onBackPressed();
                            }
                        }).create().show();
                break;

            case R.id.cv_portfolio_portfolio:
                Bundle bundle = new Bundle();
                bundle.putParcelable("proyek", project);
                DetailFragment bottomSheet = new DetailFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                break;

            case R.id.cv_profile_profile:
                Intent intentProfile = new Intent(this, DetailProfileActivity.class);
                intentProfile.putExtra(EXTRA_PROFILE, profile);
                startActivity(intentProfile);
                break;

            case R.id.btn_update_dpf:
                Intent intentUpdate = new Intent(this, AddUpdatePortfolioActivity.class);
                intentUpdate.putExtra(EXTRA_PORTFOLIO, portfolio);
                intentUpdate.putExtra(EXTRA_PROJECT, project);
                startActivityForResult(intentUpdate, RC_UPDATE_PORTFOLIO);
                break;

            case R.id.btn_payment_dpf:
                if (differenceOfDates(project.getWaktuMulai(), getCurrentDate()) > 0){ // Proyek masih belum mulai
                    Intent intentPayment = new Intent(this, PaymentActivity.class);
                    intentPayment.putExtra(EXTRA_PORTFOLIO, portfolio);
                    intentPayment.putExtra(EXTRA_PROJECT, project);
                    startActivityForResult(intentPayment, RC_ADD_PAYMENT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == RC_UPDATE_PORTFOLIO && resultCode == RC_UPDATE_PORTFOLIO){
                portfolio = data.getParcelableExtra(EXTRA_PORTFOLIO);
                tvCount.setText(" / " + portfolio.getCount() + " ekor");
                tvTotalCost.setText("Rp" + (portfolio.getCount() * project.getBiayaHewan()));
            } else if (requestCode == RC_ADD_PAYMENT && resultCode == RC_ADD_PAYMENT){
                Payment payment = data.getParcelableExtra(EXTRA_PAYMENT);
                paymentList.add(payment);
                adapter.setData(paymentList);

                tvStatusPayment.setText("Menunggu persetujuan");
                tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));

                ibDelete.setVisibility(View.INVISIBLE);
                btnUpdate.setVisibility(View.INVISIBLE);
                btnPayment.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void receiveData(Payment payment, String statusResult, String rejectionNote) {
        payment.setStatus(statusResult);
        payment.setRejectionNote(rejectionNote);

        if (statusResult.equals(PAY_APPROVED)){
            portfolio.setCost((long) project.getBiayaHewan());
            portfolio.setTotalCost((portfolio.getCount() * project.getBiayaHewan()));
            portfolio.setStatus(statusResult);

            portfolioViewModel.update(portfolio.getId(), portfolio.getCost(), portfolio.getTotalCost(), portfolio.getStatus());
            showToast(getApplicationContext(), "Pembayaran berhasil disetujui");
        } else if (statusResult.equals(PAY_REJECT)){
            showToast(getApplicationContext(), "Pembayaran berhasil ditolak");
        }

        paymentViewModel.update(portfolio.getId(), payment.getId(), payment.getStatus(), payment.getRejectionNote());

        // Refresh adapter setelah ganti status
        adapter.setLastPaymentStatus(payment.getStatus(), payment.getRejectionNote()); // Pasti yang keganti item terakhir
        setLastPaymentStatus(payment.getStatus());
    }
}