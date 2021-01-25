package com.example.ternavest.ui.both.portfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
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
import com.squareup.picasso.Picasso;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PORTFOLIO;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.getRupiahFormat;
import static com.example.ternavest.utils.AppUtils.showToast;
import static com.example.ternavest.utils.EditTextUtils.getFixText;
import static com.example.ternavest.utils.EditTextUtils.isNull;

public class AddUpdatePortfolioActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RC_UPDATE_PORTFOLIO = 100;

    private FirebaseUser firebaseUser;
    private Portfolio portfolio;
    private PortfolioViewModel portfolioViewModel;
    private Proyek project;

    private Button btnPayment;
    private CardView cvProject;
    private EditText edtCount;
    private ImageView imgProject;
    private TextView tvProjectName, tvProjectLivestock, tvProjectROI, tvCost, tvTotalCost, tvHelper;

    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_portfolio);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar); //No Problerm
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imgProject = findViewById(R.id.imgProyek);
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

            Picasso.get()
                    .load(project.getPhotoProyek())
                    .resize(100, 100) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .into(imgProject);

            tvProjectName.setText(project.getNamaProyek());
            tvProjectLivestock.setText(project.getJenisHewan());
            tvProjectROI.setText(project.getRoi() + "%");
            tvCost.setText(getRupiahFormat(project.getBiayaHewan()));
            tvHelper.setText(getRupiahFormat(project.getBiayaHewan()) + " adalah biaya satu ekor hewan selama satu periode proyek. Silakan masukkan berapa jumlah hewan yang ingin Anda investasikan.");

            isUpdate = intent.hasExtra(EXTRA_PORTFOLIO);
            if (isUpdate){
                getSupportActionBar().setTitle("Edit Informasi Investasi");
                portfolio = intent.getParcelableExtra(EXTRA_PORTFOLIO);

                tvTotalCost.setText(getRupiahFormat(
                        portfolio.getCount() * project.getBiayaHewan()));
                edtCount.setText(String.valueOf(portfolio.getCount()));
                btnPayment.setText("Simpan");
            } else {
                getSupportActionBar().setTitle("Mulai Investasi");
                portfolio = new Portfolio();

                tvTotalCost.setText(getRupiahFormat(project.getBiayaHewan()));
                edtCount.setText("1"); // Nilai default
                btnPayment.setText("Lanjut Pembayaran");
            }
        }

        edtCount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)}); // Batasin jumlah digit
        edtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){ // Hitung real-time
                    tvTotalCost.setText(getRupiahFormat(
                            Long.parseLong(getFixText(edtCount)) * project.getBiayaHewan()));
                    btnPayment.setEnabled(true);
                } else tvTotalCost.setText("Rp0");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String count = getFixText(edtCount);
                if (isNull(count) || Long.parseLong(count) <= 0){
                    edtCount.setError("Harap isi berapa ekor yang akan diinvestasikan");
                    btnPayment.setEnabled(false);
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
                    showToast(this, "Proyek telah ditambah ke portofolio");

                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra(EXTRA_PORTFOLIO, portfolio);
                    intent.putExtra(EXTRA_PROJECT, project);
                    startActivity(intent);
                }

                finish();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}