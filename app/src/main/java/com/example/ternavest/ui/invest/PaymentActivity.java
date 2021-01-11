package com.example.ternavest.ui.invest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.callback.OnImageUploadCallback;
import com.example.ternavest.model.Payment;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Profile;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.PaymentViewModel;
import com.example.ternavest.viewmodel.ProfileViewModel;
import com.example.ternavest.viewmodel.ProyekViewModel;

import java.util.List;

import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.createIdFromCurrentDate;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.DateUtils.getCurrentDate;
import static com.example.ternavest.utils.DateUtils.getCurrentTime;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_PAYMENT_IMAGE = 100;
    public static final String EXTRA_PORTFOLIO = "extra_portfolio";

    private TextView tvProject, tvTotalCost, tvCount, tvStatus, tvAccountName, tvAccountBank, tvAccountNumber, tvNominal;
    private ImageView imgPayment;
    private Button btnUpload, btnSend;

    private PaymentViewModel paymentViewModel;
    private ProyekViewModel projectViewModel;
    private ProfileViewModel profileViewModel;
    private Portfolio portfolio;

    private Uri uriPaymentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvProject = findViewById(R.id.tv_project_portfolio);
        tvTotalCost = findViewById(R.id.tv_total_cost_portfolio);
        tvCount = findViewById(R.id.tv_count_portfolio);
        tvStatus = findViewById(R.id.tv_status_portfolio);
        tvAccountName = findViewById(R.id.tv_account_name_payment);
        tvAccountBank = findViewById(R.id.tv_account_bank_payment);
        tvAccountNumber = findViewById(R.id.tv_account_number_payment);
        tvNominal = findViewById(R.id.tv_total_cost_payment);
        imgPayment = findViewById(R.id.img_payment_payment);

        btnUpload = findViewById(R.id.btn_upload_payment);
        btnSend = findViewById(R.id.btn_send_payment);
        btnUpload.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        projectViewModel.getResultByID().observe(this, new Observer<List<Proyek>>() {
            @Override
            public void onChanged(List<Proyek> projectList) {
                Proyek project = projectList.get(0);
                tvProject.setText(project.getNamaProyek());
                tvTotalCost.setText("Rp" + (portfolio.getCount() * project.getBiayaHewan()));
                tvNominal.setText("Rp" + (portfolio.getCount() * project.getBiayaHewan()));
            }
        });

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel.class);
        profileViewModel.getData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                tvAccountName.setText(profile.getAccountName());
                tvAccountBank.setText(profile.getAccountBank());
                tvAccountNumber.setText("No. rekening: " + profile.getAccountNumber());
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PORTFOLIO)){
            portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

            tvCount.setText(" / " + portfolio.getCount() + " ekor");
            tvStatus.setText(portfolio.getStatus());

            projectViewModel.loadResultByID(portfolio.getProjectId());
            profileViewModel.loadData(portfolio.getBreederId());
        }

        paymentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PaymentViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_upload_payment:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih bukti pembayaran untuk diunggah:"), RC_PAYMENT_IMAGE);
                break;

            case R.id.btn_send_payment:
                if (uriPaymentImage == null){
                    showToast(this, "Mohon pilih bukti pembayaran terlebih dahulu");
                    return;
                }

                Payment payment = new Payment();
                payment.setId(createIdFromCurrentDate());
                payment.setDate(getCurrentDate());
                payment.setTime(getCurrentTime());
                payment.setRejectionNote(null);
                payment.setStatus(PAY_PENDING);

                showToast(getApplicationContext(), "Mengunggah bukti pembayaran...");
                String fileName = payment.getId() + ".jpeg";
                paymentViewModel.uploadImage(this, portfolio.getId(), uriPaymentImage, fileName, new OnImageUploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        payment.setImage(imageUrl);
                        paymentViewModel.insert(portfolio.getId(), payment);
                        showToast(getApplicationContext(), "Pembayaran berhasil diajukan.");
                    }
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
                }
            }
        }
    }
}