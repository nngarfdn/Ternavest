package com.example.ternavest.adapter.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PORTFOLIO;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.AppUtils.getRupiahFormat;
import static com.example.ternavest.utils.AppUtils.showToast;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    private final ArrayList<Portfolio> portfolioList = new ArrayList<>();
    private final CollectionReference reference;

    public PortfolioAdapter(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        reference = database.collection("proyek");
    }

    public void setData(ArrayList<Portfolio> portfolioList){
        this.portfolioList.clear();
        this.portfolioList.addAll(portfolioList);
        notifyDataSetChanged();
    }

    public ArrayList<Portfolio> getData(){
        return portfolioList;
    }

    @NonNull
    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioAdapter.ViewHolder holder, int position) {
        Portfolio portfolio = portfolioList.get(position);
        holder.bind(portfolio);
    }

    @Override
    public int getItemCount() {
        return portfolioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context = itemView.getContext();
        private Proyek project;
        private final TextView tvProject, tvTotalCost, tvCount, tvStatus;
        private final CardView cvStatus;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvProject = view.findViewById(R.id.tv_project_portfolio);
            tvTotalCost = view.findViewById(R.id.tv_total_cost_portfolio);
            tvCount = view.findViewById(R.id.tv_count_portfolio);
            tvStatus = view.findViewById(R.id.tv_status_portfolio);
            cvStatus = view.findViewById(R.id.cv_status_portfolio);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Portfolio portfolio) {
            tvCount.setText("/ " + (portfolio.getCount()) + " ekor");

            reference.document(portfolio.getProjectId()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    project = Objects.requireNonNull(task.getResult()).toObject(Proyek.class);

                    if (project != null) {
                        tvProject.setText(project.getNamaProyek());
                    }

                    String status = portfolio.getStatus();
                    long totalCost = 0;
                    switch (status) {
                        case PAY_APPROVED:
                            status = "Disetujui";
                            totalCost = portfolio.getTotalCost();
                            cvStatus.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
                            break;
                        case PAY_REJECT:
                            status = "Ditolak";
                            totalCost = project.getBiayaHewan()*portfolio.getCount();
                            cvStatus.setCardBackgroundColor(context.getResources().getColor(R.color.red));
                            break;
                        case PAY_PENDING:
                            status = "Pending";
                            if (portfolio.getTotalCost() != 0 ) totalCost = portfolio.getTotalCost(); // Ada pembayaran pending
                            else totalCost = project.getBiayaHewan()*portfolio.getCount(); // Tidak ada
                            cvStatus.setCardBackgroundColor(context.getResources().getColor(R.color.orange));
                            break;
                    }
                    tvStatus.setText(status);
                    tvTotalCost.setText(getRupiahFormat(totalCost));
                }
            });

            itemView.setOnClickListener(view -> {
                if (project == null){
                    showToast(context, "Mohon tunggu...");
                    return;
                }

                Intent intent = new Intent(context, DetailPortfolioActivity.class);
                intent.putExtra(EXTRA_PORTFOLIO, portfolio);
                intent.putExtra(EXTRA_PROJECT, project);
                context.startActivity(intent);
            });
        }
    }
}
