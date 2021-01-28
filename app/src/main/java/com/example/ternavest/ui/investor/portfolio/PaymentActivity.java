package com.example.ternavest.ui.investor.portfolio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.ternavest.R;
import com.example.ternavest.customview.LoadingDialog;
import com.example.ternavest.model.Payment;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.ui.investor.home.DetailProyekInvestasiFragment;
import com.example.ternavest.viewmodel.PaymentViewModel;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PAYMENT;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PORTFOLIO;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.createIdFromCurrentDate;
import static com.example.ternavest.utils.AppUtils.getRupiahFormat;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.DateUtils.differenceOfDates;
import static com.example.ternavest.utils.DateUtils.getCurrentDate;
import static com.example.ternavest.utils.DateUtils.getCurrentTime;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_PAYMENT_IMAGE = 100;
    public static final int RC_ADD_PAYMENT = 200;

    private LoadingDialog loadingDialog;
    private PaymentViewModel paymentViewModel;
    private Portfolio portfolio;
    private PortfolioViewModel portfolioViewModel;
    private Proyek project;

    private Button btnSend;
    private ImageView imgPayment;
    private TextView tvAccountName;
    private TextView tvAccountBank;
    private TextView tvAccountNumber;

    private Uri uriPaymentImage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        loadingDialog = new LoadingDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pembayaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView cvStatus = findViewById(R.id.cv_status_portfolio);
        TextView tvProject = findViewById(R.id.tv_project_portfolio);
        TextView tvTotalCost = findViewById(R.id.tv_total_cost_portfolio);
        TextView tvCount = findViewById(R.id.tv_count_portfolio);
        TextView tvStatus = findViewById(R.id.tv_status_portfolio);
        tvAccountName = findViewById(R.id.tv_account_name_payment);
        tvAccountBank = findViewById(R.id.tv_account_bank_payment);
        tvAccountNumber = findViewById(R.id.tv_account_number_payment);
        TextView tvNominal = findViewById(R.id.tv_total_cost_payment);
        imgPayment = findViewById(R.id.img_payment_payment);

        CardView cvPortfolio = findViewById(R.id.cv_portfolio_portfolio);
        Button btnUpload = findViewById(R.id.btn_upload_payment);
        btnSend = findViewById(R.id.btn_send_payment);
        cvPortfolio.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        btnSend.setEnabled(false);

        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, profile -> {
            tvAccountName.setText(profile.getAccountName());
            tvAccountBank.setText(profile.getAccountBank());
            tvAccountNumber.setText("No. rekening: " + profile.getAccountNumber());
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PORTFOLIO)){
            portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);
            tvCount.setText(" / " + portfolio.getCount() + " ekor");
            tvNominal.setText(getRupiahFormat(portfolio.getTotalCost()));
            Log.d(getClass().getSimpleName(), "onCreate: " + getRupiahFormat(portfolio.getTotalCost()));
            tvStatus.setText("Pending"); // Kalau bisa ke pembayaran, pasti statusnya pending
            cvStatus.setCardBackgroundColor(getResources().getColor(R.color.orange));

            if (intent.hasExtra(EXTRA_PROJECT)){
                project = intent.getParcelableExtra(EXTRA_PROJECT);
                tvProject.setText(project.getNamaProyek());
                tvTotalCost.setText(getRupiahFormat(portfolio.getCount() * project.getBiayaHewan()));
                tvNominal.setText(getRupiahFormat(portfolio.getCount() * project.getBiayaHewan()));

                Log.d(getClass().getSimpleName(), "onCreate: " + getRupiahFormat(portfolio.getCount() * project.getBiayaHewan()));

                // Proyek sudah mulai atau selesai
                if (!(differenceOfDates(project.getWaktuMulai(), getCurrentDate()) > 0)){
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Proyek sudah dimulai")
                            .setMessage("Kamu sudah tidak bisa mengajukan pembayaran.")
                            .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            })
                            .create().show();
                }
            }

            profileViewModel.loadData(portfolio.getBreederId());
        }

        paymentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PaymentViewModel.class);
        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_portfolio_portfolio:
                Bundle bundle = new Bundle();
                bundle.putParcelable("proyek", project);
                DetailProyekInvestasiFragment bottomSheet = new DetailProyekInvestasiFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                break;

            case R.id.btn_upload_payment:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Unggah bukti pembayaran"), RC_PAYMENT_IMAGE);
                break;

            case R.id.btn_send_payment:
                Payment payment = new Payment();
                payment.setId(createIdFromCurrentDate());
                payment.setDate(getCurrentDate());
                payment.setTime(getCurrentTime());
                payment.setRejectionNote(null);
                payment.setStatus(PAY_PENDING);

                showToast(getApplicationContext(), "Mengunggah bukti pembayaran...");
                loadingDialog.show();

                String fileName = payment.getId() + ".jpeg";
                paymentViewModel.uploadImage(this, portfolio.getId(), uriPaymentImage, fileName, imageUrl -> {
                    payment.setImage(imageUrl);
                    paymentViewModel.insert(portfolio.getId(), payment);
                    portfolioViewModel.update(portfolio.getId(), project.getBiayaHewan(), portfolio.getCount() * project.getBiayaHewan()); // Kunci harga ke portofolio

                    Intent intentResult = new Intent();
                    intentResult.putExtra(EXTRA_PAYMENT, payment);
                    setResult(RC_ADD_PAYMENT, intentResult);

                    loadingDialog.dismiss();
                    showToast(getApplicationContext(), "Pembayaran berhasil diajukan.");
                    finish();
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PAYMENT_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                if (data != null) if (data.getData() != null){
                    uriPaymentImage = data.getData();
                    loadImageFromUrl(imgPayment, uriPaymentImage.toString());
                    btnSend.setEnabled(true);
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