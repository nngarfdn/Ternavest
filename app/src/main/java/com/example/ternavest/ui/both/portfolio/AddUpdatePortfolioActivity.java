package com.example.ternavest.ui.both.portfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.EditTextUtils.getFixText;
import static com.example.ternavest.utils.EditTextUtils.isNull;

public class AddUpdatePortfolioActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PROJECT = "extra_project";
    public static final String EXTRA_PORTFOLIO = "extra_portfolio";

    private FirebaseUser firebaseUser;
    private Proyek project;
    private Portfolio portfolio;
    private PortfolioViewModel portfolioViewModel;

    private TextView tvTitle, tvProjectName, tvProjectLivestock, tvProjectROI, tvCost, tvTotalCost, tvHelper;
    private ImageView imgProject;
    private Button btnPayment;
    private EditText edtCount;

    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_portfolio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imgProject = findViewById(R.id.imgProyek);
        tvTitle = findViewById(R.id.tv_title_aup);
        tvProjectName = findViewById(R.id.txtNamaProyek);
        tvProjectLivestock = findViewById(R.id.txtJenisHewan);
        tvProjectROI = findViewById(R.id.txtROI);
        tvCost = findViewById(R.id.tv_cost_aup);
        tvTotalCost = findViewById(R.id.tv_total_cost_payment);
        tvHelper = findViewById(R.id.tv_helper_aup);
        edtCount = findViewById(R.id.edt_count_aup);

        btnPayment = findViewById(R.id.btn_payment_aup);
        btnPayment.setOnClickListener(this);

        Intent intentProject = getIntent();
        if (intentProject.hasExtra(EXTRA_PROJECT)){
            project = intentProject.getParcelableExtra(EXTRA_PROJECT);

            loadImageFromUrl(imgProject, project.getPhotoProyek());
            tvProjectName.setText(project.getNamaProyek());
            tvProjectLivestock.setText(project.getJenisHewan());
            tvProjectROI.setText(project.getRoi() + "%");
            tvCost.setText("Rp" + project.getBiayaHewan());
            tvHelper.setText("Rp" + project.getBiayaHewan() + " adalah biaya satu ekor hewan selama satu periode proyek. Silakan masukkan berapa jumlah hewan yang ingin Anda investasikan.");

            Intent intentPortfolio = getIntent();
            isUpdate = intentPortfolio.hasExtra(EXTRA_PORTFOLIO);
            if (isUpdate){
                portfolio = intentPortfolio.getParcelableExtra(EXTRA_PORTFOLIO);

                tvTitle.setText("Edit Informasi Investasi");
                tvTotalCost.setText("Rp" + (
                        portfolio.getCount() * project.getBiayaHewan()
                ));
                edtCount.setText(String.valueOf(portfolio.getCount()));
                btnPayment.setText("Lanjut Pembayaran");
            } else {
                portfolio = new Portfolio();

                tvTitle.setText("Mulai Investasi");
                tvTotalCost.setText("Rp" + (1 * project.getBiayaHewan()));
                edtCount.setText("1");
                btnPayment.setText("Simpan");
            }
        }

        edtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvTotalCost.setText("Rp" + (
                        Integer.parseInt(getFixText(edtCount)) * project.getBiayaHewan()
                ));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_payment_aup){
            String count = getFixText(edtCount);

            if (isNull(count)){
                showToast(this, "Harap isi berapa ekor yang akan diinvestasikan");
                return;
            }

            portfolio.setCount(Integer.parseInt(count));

            if (isUpdate){
                portfolioViewModel.update(portfolio.getId(), portfolio.getCount());
                showToast(this, "Informasi berhasil diedit");
            } else {
                portfolio.setId(project.getId() + "-" + firebaseUser.getUid());
                portfolio.setProjectId(project.getId());
                portfolio.setInvestorId(firebaseUser.getUid());
                portfolio.setBreederId(project.getUuid());
                portfolio.setPaymentId(new ArrayList<>());
                portfolio.setCost(0);
                portfolio.setTotalCost(0);
                portfolio.setStatus(PAY_PENDING);
                showToast(this, "Proyek telah ditambah ke portfolio");
            }

            onBackPressed();
        }
    }
}