package com.example.ternavest.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ternavest.R;
import com.example.ternavest.model.Payment;

import java.util.ArrayList;

import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context = itemView.getContext();
        private TextView tvDate, tvStatus;
        
        public ViewHolder(@NonNull View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date_payment);
            tvStatus = view.findViewById(R.id.tv_status_payment);
        }

        public void bind(Payment payment) {
            tvDate.setText(payment.getDate() + ", " + payment.getTime());

            String status = payment.getStatus();
            switch (status) {
                case PAY_APPROVED:
                    status = "Disetujui";
                    break;
                case PAY_REJECT:
                    status = "Ditolak";
                    break;
                case PAY_PENDING:
                    status = "Pending";
                    break;
            }
            tvStatus.setText(status);
        }
    }
}
