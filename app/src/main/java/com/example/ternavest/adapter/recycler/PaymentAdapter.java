package com.example.ternavest.adapter.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.model.Payment;
import com.example.ternavest.ui.both.portfolio.DetailPaymentFragment;
import com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity;

import java.util.ArrayList;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PAYMENT;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.DateUtils.getFullDate;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private final ArrayList<Payment> paymentList = new ArrayList<>();

    public PaymentAdapter(){}

    public void setData(ArrayList<Payment> paymentList){
        this.paymentList.clear();
        this.paymentList.addAll(paymentList);
        notifyDataSetChanged();
    }

    public ArrayList<Payment> getData(){
        return paymentList;
    }

    public void setLastPaymentStatus(String newStatus, String rejectionNote){
        this.paymentList.get(getItemCount()-1).setStatus(newStatus);
        this.paymentList.get(getItemCount()-1).setRejectionNote(rejectionNote);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context = itemView.getContext();
        private final TextView tvDate, tvStatus;
        
        public ViewHolder(@NonNull View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date_payment);
            tvStatus = view.findViewById(R.id.tv_status_payment);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Payment payment) {
            tvDate.setText(getFullDate(payment.getDate(), false) + ", " + payment.getTime());

            String status = payment.getStatus();
            switch (status) {
                case PAY_APPROVED:
                    status = "Disetujui";
                    tvStatus.setTextColor(context.getResources().getColor(R.color.blue));
                    break;
                case PAY_REJECT:
                    status = "Ditolak";
                    tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case PAY_PENDING:
                    status = "Pending";
                    tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
                    break;
            }
            tvStatus.setText(status);

            itemView.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_PAYMENT, payment);
                DetailPaymentFragment bottomSheet = new DetailPaymentFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(((DetailPortfolioActivity) context)
                        .getSupportFragmentManager(), bottomSheet.getTag());
            });
        }
    }
}
