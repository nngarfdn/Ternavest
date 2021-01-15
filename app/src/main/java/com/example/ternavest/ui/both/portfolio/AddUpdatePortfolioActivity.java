package com.example.ternavest.ui.both.portfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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
import com.example.ternavest.ui.investor.portfolio.PaymentActivity;
import com.example.ternavest.ui.peternak.kelola.proyek.DetailFragment;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PORTFOLIO;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.getRupiahFormat;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.EditTextUtils.getFixText;
import static com.example.ternavest.utils.EditTextUtils.isNull;

public class AddUpdatePortfolioActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RC_UPDATE_PORTFOLIO = 100;

    private FirebaseUser firebaseUser;
    private Proyek project;
    private Portfolio portfolio;
    private PortfolioViewModel portfolioViewModel;

    private CardView cvProject;
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

        cvProject = findViewById(R.id.cv_project_project);
        btnPayment = findViewById(R.id.btn_payment_aup);
        cvProject.setOnClickListener(this);
        btnPayment.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PROJECT)){
            project = intent.getParcelableExtra(EXTRA_PROJECT);

            loadImageFromUrl(imgProject, project.getPhotoProyek());
            tvProjectName.setText(project.getNamaProyek());
            tvProjectLivestock.setText(project.getJenisHewan());
            tvProjectROI.setText(project.getRoi() + "%");
            tvCost.setText(getRupiahFormat(project.getBiayaHewan()));
            tvHelper.setText(getRupiahFormat(project.getBiayaHewan()) + " adalah biaya satu ekor hewan selama satu periode proyek. Silakan masukkan berapa jumlah hewan yang ingin Anda investasikan.");

            isUpdate = intent.hasExtra(EXTRA_PORTFOLIO);
            if (isUpdate){
                portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

                tvTitle.setText("Edit Informasi Investasi");
                tvTotalCost.setText(getRupiahFormat(
                        portfolio.getCount() * project.getBiayaHewan()
                ));
                edtCount.setText(String.valueOf(portfolio.getCount()));
                btnPayment.setText("Simpan");
            } else {
                portfolio = new Portfolio();

                tvTitle.setText("Mulai Investasi");
                tvTotalCost.setText(getRupiahFormat(project.getBiayaHewan()));
                edtCount.setText("1");
                btnPayment.setText("Lanjut Pembayaran");
            }
        }

        edtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){ // Masih BUG -> belum fix
                    tvTotalCost.setText(getRupiahFormat(
                            Integer.parseInt(getFixText(edtCount)) * project.getBiayaHewan()
                    ));
                } else {
                    tvTotalCost.setText("Rp0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isNull(getFixText(edtCount))){
                    edtCount.setText("1");
                }
            }
        });

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_project_project:
                Bundle bundle = new Bundle();
                bundle.putParcelable("proyek", project);
                DetailFragment bottomSheet = new DetailFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                break;

            case R.id.btn_payment_aup:
                String count = getFixText(edtCount);

                if (isNull(count)){
                    showToast(this, "Harap isi berapa ekor yang akan diinvestasikan");
                    return;
                }

                portfolio.setCount(Integer.parseInt(count));

                if (isUpdate){
                    portfolioViewModel.update(portfolio.getId(), portfolio.getCount());
                    showToast(this, "Informasi berhasil diedit");

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PORTFOLIO, portfolio);
                    setResult(RC_UPDATE_PORTFOLIO, intent);
                } else {
                    portfolio.setProjectId(project.getId());
                    portfolio.setInvestorId(firebaseUser.getUid());
                    portfolio.setBreederId(project.getUuid());
                    portfolio.setCost(0);
                    portfolio.setTotalCost(0);
                    portfolio.setStatus(PAY_PENDING);

                    portfolioViewModel.insert(portfolio);
                    showToast(this, "Proyek telah ditambah ke portfolio");

                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra(EXTRA_PORTFOLIO, portfolio);
                    intent.putExtra(EXTRA_PROJECT, project);
                    startActivity(intent);
                }

                finish();
                break;
        }
    }
}