package com.example.ternavest.adaper.recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PORTFOLIO;
import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
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

        public ViewHolder(@NonNull View view) {
            super(view);
            tvProject = view.findViewById(R.id.tv_project_portfolio);
            tvTotalCost = view.findViewById(R.id.tv_total_cost_portfolio);
            tvCount = view.findViewById(R.id.tv_count_portfolio);
            tvStatus = view.findViewById(R.id.tv_status_portfolio);
        }

        public void bind(Portfolio portfolio) {
            tvCount.setText(String.valueOf(portfolio.getCount()));

            reference.document(portfolio.getProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        project = task.getResult().toObject(Proyek.class);

                        tvProject.setText(project.getNamaProyek());

                        String status = portfolio.getStatus();
                        long totalCost = 0;
                        switch (status) {
                            case PAY_APPROVED:
                                status = "Disetujui";
                                totalCost = portfolio.getTotalCost();
                                break;
                            case PAY_REJECT:
                                status = "Ditolak";
                                totalCost = project.getBiayaHewan()*portfolio.getCount();
                                break;
                            case PAY_PENDING:
                                status = "Pending";
                                totalCost = project.getBiayaHewan()*portfolio.getCount();
                                break;
                        }
                        tvStatus.setText(status);
                        tvTotalCost.setText(String.valueOf(totalCost));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (project == null){
                        showToast(context, "Mohon tunggu...");
                        return;
                    }

                    Intent intent = new Intent(context, DetailPortfolioActivity.class);
                    intent.putExtra(EXTRA_PORTFOLIO, portfolio);
                    intent.putExtra(EXTRA_PROJECT, project);
                    context.startActivity(intent);
                }
            });
        }
    }
}
