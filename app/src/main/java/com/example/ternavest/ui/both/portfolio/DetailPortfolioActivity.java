package com.example.ternavest.ui.both.portfolio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.PaymentAdapter;
import com.example.ternavest.customview.LoadingDialog;
import com.example.ternavest.model.Payment;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.ui.both.profile.DetailProfileActivity;
import com.example.ternavest.ui.investor.home.DetailProyekInvestasiFragment;
import com.example.ternavest.ui.investor.portfolio.AddUpdatePortfolioActivity;
import com.example.ternavest.ui.investor.portfolio.PaymentActivity;
import com.example.ternavest.ui.peternak.kelola.proyek.DetailFragment;
import com.example.ternavest.viewmodel.PaymentViewModel;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ternavest.ui.investor.portfolio.AddUpdatePortfolioActivity.RC_UPDATE_PORTFOLIO;
import static com.example.ternavest.ui.both.profile.DetailProfileActivity.EXTRA_PROFILE;
import static com.example.ternavest.ui.investor.portfolio.PaymentActivity.RC_ADD_PAYMENT;
import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.AppUtils.getRupiahFormat;
import static com.example.ternavest.utils.AppUtils.loadProfilePicFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.DateUtils.differenceOfDates;
import static com.example.ternavest.utils.DateUtils.getCurrentDate;

public class DetailPortfolioActivity extends AppCompatActivity implements View.OnClickListener, DetailPaymentFragment.DetailPaymentListener {
    public static final String EXTRA_PAYMENT = "extra_payment";
    public static final String EXTRA_PORTFOLIO = "extra_portfolio";
    public static final String EXTRA_PROJECT = "extra_project";
    public static final String PAY_EMPTY = "belum_ada_pembayaran";

    private Button btnUpdate, btnPayment;
    private CardView cvProfile;
    private CircleImageView civProfile;
    private MenuItem menuDelete;
    private TextView tvTotalCost;
    private TextView tvCount;
    private TextView tvProfile;
    private TextView tvStatusProject;
    private TextView tvStatusPayment;
    private TextView tvPayment;

    private LoadingDialog loadingDialog;
    private PaymentAdapter adapter;
    private PaymentViewModel paymentViewModel;
    private Portfolio portfolio;
    private PortfolioViewModel portfolioViewModel;
    private Profile profile;
    private Proyek project;
    private UserPreference userPreference;

    private ArrayList<Payment> paymentList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_portfolio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                new AlertDialog.Builder(DetailPortfolioActivity.this)
                        .setTitle("Batalkan investasi")
                        .setMessage("Apakah Anda yakin ingin membatalkan investasi pada proyek ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            portfolioViewModel.delete(portfolio); // Hapus portofolio
                            for (Payment payment : paymentList) { // Hapus pembayaran + foto bukti
                                paymentViewModel.delete(portfolio.getId(), payment.getId());
                                paymentViewModel.deleteImage(payment.getImage());
                            }
                            onBackPressed();
                        }).create().show();
            }
            return false;
        });

        userPreference = new UserPreference(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        RecyclerView recyclerView = findViewById(R.id.rv_payment_dpf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter();
        recyclerView.setAdapter(adapter);

        TextView tvProject = findViewById(R.id.tv_project_portfolio);
        tvTotalCost = findViewById(R.id.tv_total_cost_portfolio);
        tvCount = findViewById(R.id.tv_count_portfolio);
        TextView tvAction = findViewById(R.id.tv_status_portfolio);
        tvStatusPayment = findViewById(R.id.tv_status_payment_dpf);
        tvStatusProject = findViewById(R.id.tv_status_project_dpf);
        TextView tvLevel = findViewById(R.id.tv_level_dpf);
        tvPayment = findViewById(R.id.tv_payment_dpf);
        tvProfile = findViewById(R.id.tv_name_profile);
        civProfile = findViewById(R.id.civ_photo_profile);

        CardView cvPortfolio = findViewById(R.id.cv_portfolio_portfolio);
        cvProfile = findViewById(R.id.cv_profile_profile);
        btnUpdate = findViewById(R.id.btn_update_dpf);
        btnPayment = findViewById(R.id.btn_payment_dpf);
        cvPortfolio.setOnClickListener(this);
        cvProfile.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnPayment.setOnClickListener(this);

        tvAction.setText("Lihat proyek");
        if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)){
            btnUpdate.setVisibility(View.GONE);
            btnPayment.setVisibility(View.GONE);
        }
        cvProfile.setEnabled(false); // Tunggu selesai query dulu
        tvPayment.setVisibility(View.INVISIBLE);

        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, result -> {
            profile = result;

            tvProfile.setText(profile.getName());
            loadProfilePicFromUrl(civProfile, profile.getPhoto());
            cvProfile.setEnabled(true);

            loadingDialog.dismiss();
        });

        paymentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PaymentViewModel.class);
        paymentViewModel.getData().observe(this, result -> {
            paymentList = result;
            adapter.setData(paymentList);

            // Atur status pembayaran terakhir
            if (adapter.getItemCount() > 0){
                setLastPaymentStatus(adapter.getData().get(adapter.getItemCount()-1).getStatus());
                tvPayment.setVisibility(View.VISIBLE);
            } else {
                setLastPaymentStatus(PAY_EMPTY);
                tvPayment.setVisibility(View.INVISIBLE);
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PORTFOLIO)){
            portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

            tvCount.setText("/ " + portfolio.getCount() + " ekor");

            if (intent.hasExtra(EXTRA_PROJECT)){
                project = intent.getParcelableExtra(EXTRA_PROJECT);

                // Atur informasi proyek dan portfolio
                toolbar.setTitle(project.getNamaProyek());
                tvProject.setText(project.getNamaProyek());

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

    @SuppressLint("SetTextI18n")
    private void setLastPaymentStatus(String lastStatus) {
        if (lastStatus.equals(PAY_EMPTY)) tvPayment.setVisibility(View.INVISIBLE);
        else tvPayment.setVisibility(View.VISIBLE);

        switch (lastStatus) {
            case PAY_APPROVED:
                tvStatusPayment.setText("Disetujui");
                tvStatusPayment.setTextColor(getResources().getColor(R.color.blue));
                tvTotalCost.setText(getRupiahFormat(portfolio.getTotalCost()));
                tvStatusProject.setVisibility(View.VISIBLE);

                menuDelete.setVisible(false);
                btnUpdate.setVisibility(View.GONE);
                btnPayment.setVisibility(View.GONE);
                break;
            case PAY_PENDING:
                tvStatusPayment.setText("Menunggu persetujuan");
                tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));
                tvTotalCost.setText(getRupiahFormat(portfolio.getTotalCost()));

                menuDelete.setVisible(false);
                btnUpdate.setVisibility(View.GONE);
                btnPayment.setVisibility(View.GONE);
                break;
            case PAY_REJECT:
            case PAY_EMPTY:
                tvStatusPayment.setText("Belum bayar");
                tvStatusPayment.setTextColor(getResources().getColor(R.color.orange));
                tvTotalCost.setText(getRupiahFormat(portfolio.getCount() * project.getBiayaHewan()));

                if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) menuDelete.setVisible(true);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_portfolio_portfolio:
                BottomSheetDialogFragment bottomSheet;
                if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) bottomSheet = new DetailFragment();
                else if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) bottomSheet = new DetailProyekInvestasiFragment();
                else return;

                Bundle bundle = new Bundle();
                bundle.putParcelable("proyek", project);
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
                Intent intentPayment = new Intent(this, PaymentActivity.class);
                intentPayment.putExtra(EXTRA_PORTFOLIO, portfolio);
                intentPayment.putExtra(EXTRA_PROJECT, project);
                startActivityForResult(intentPayment, RC_ADD_PAYMENT);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == RC_UPDATE_PORTFOLIO && resultCode == RC_UPDATE_PORTFOLIO){
                portfolio = data.getParcelableExtra(EXTRA_PORTFOLIO);
                tvCount.setText("/ " + portfolio.getCount() + " ekor");
                tvTotalCost.setText(getRupiahFormat(portfolio.getCount() * project.getBiayaHewan()));
            } else if (requestCode == RC_ADD_PAYMENT && resultCode == RC_ADD_PAYMENT){
                Payment payment = data.getParcelableExtra(EXTRA_PAYMENT);
                paymentList.add(payment);
                adapter.setData(paymentList);
                setLastPaymentStatus(PAY_PENDING);
            }
        }
    }

    @Override
    public void receiveData(Payment payment, String statusResult, String rejectionNote) {
        payment.setStatus(statusResult);
        payment.setRejectionNote(rejectionNote);

        if (statusResult.equals(PAY_APPROVED)){
            portfolio.setStatus(statusResult);
            portfolioViewModel.update(portfolio.getId(), portfolio.getStatus());
            showToast(getApplicationContext(), "Pembayaran berhasil disetujui");
        } else if (statusResult.equals(PAY_REJECT)){
            portfolioViewModel.update(portfolio.getId(), 0, 0);
            showToast(getApplicationContext(), "Pembayaran berhasil ditolak");
        }

        paymentViewModel.update(portfolio.getId(), payment.getId(), payment.getStatus(), payment.getRejectionNote());

        // Refresh adapter setelah ganti status
        adapter.setLastPaymentStatus(payment.getStatus(), payment.getRejectionNote()); // Pasti yang keganti item terakhir
        setLastPaymentStatus(payment.getStatus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_delete, menu);

        menuDelete = menu.findItem(R.id.action_delete);
        if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) menuDelete.setVisible(false);
        else if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) menuDelete.setVisible(true);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}