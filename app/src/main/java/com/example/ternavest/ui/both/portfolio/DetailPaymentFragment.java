package com.example.ternavest.ui.both.portfolio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.ternavest.R;
import com.example.ternavest.model.Payment;
import com.example.ternavest.preference.UserPreference;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PAYMENT;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.PAY_APPROVED;
import static com.example.ternavest.utils.AppUtils.PAY_PENDING;
import static com.example.ternavest.utils.AppUtils.PAY_REJECT;
import static com.example.ternavest.utils.AppUtils.loadImageFromUrl;
import static com.example.ternavest.utils.DateUtils.getFullDate;

public class DetailPaymentFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private CardView cvStatus;
    private TextView tvStatus, tvDate;
    private ImageView imgPayment;
    private Button btnApprove, btnReject;
    private Spinner spRejectionNote;

    private UserPreference userPreference;
    private Payment payment;
    private DetailPaymentListener listener;

    public DetailPaymentFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPreference = new UserPreference(getContext());

        cvStatus = view.findViewById(R.id.cv_status_dp);
        tvStatus = view.findViewById(R.id.tv_status_dp);
        tvDate = view.findViewById(R.id.tv_date_dp);
        imgPayment = view.findViewById(R.id.img_receipt_dp);
        spRejectionNote = view.findViewById(R.id.spin_reject_dp);

        btnApprove = view.findViewById(R.id.btn_approve_dp);
        btnReject = view.findViewById(R.id.btn_reject_dp);
        btnApprove.setOnClickListener(this);
        btnReject.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()){
            payment = bundle.getParcelable(EXTRA_PAYMENT);

            tvDate.setText(getFullDate(payment.getDate(), false) + ", " + payment.getTime());
            loadImageFromUrl(imgPayment, payment.getImage());

            btnApprove.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            String status = payment.getStatus();
            switch (status) {
                case PAY_APPROVED:
                    status = "Disetujui";
                    cvStatus.setCardBackgroundColor(getContext().getResources().getColor(R.color.blue));
                    break;
                case PAY_REJECT:
                    status = "Ditolak";
                    cvStatus.setCardBackgroundColor(getContext().getResources().getColor(R.color.red));
                    break;
                case PAY_PENDING:
                    status = "Pending";
                    cvStatus.setCardBackgroundColor(getContext().getResources().getColor(R.color.orange));

                    if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)){
                        btnApprove.setVisibility(View.VISIBLE);
                        btnReject.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            tvStatus.setText(status);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_approve_dp:
                new AlertDialog.Builder(getContext())
                        .setTitle("Setujui pembayaran")
                        .setMessage("Apakah Anda yakin bukti pembayaran telah valid?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listener.receiveData(payment, PAY_APPROVED, null);
                                dismiss();
                            }
                        }).create().show();
                break;

            case R.id.btn_reject_dp:
                new AlertDialog.Builder(getContext())
                        .setTitle("Tolak pembayaran")
                        .setMessage("Apakah Anda yakin ingin menolak pembayaran ini? Pastikan Anda memilih alasan penolakan dengan tepat.")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listener.receiveData(payment, PAY_REJECT, spRejectionNote.getSelectedItem().toString());
                                dismiss();
                            }
                        }).create().show();
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DetailPaymentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement " + DetailPaymentListener.class.getSimpleName());
        }
    }

    public interface DetailPaymentListener{
        void receiveData(Payment payment, String statusResult, String rejectionNote);
    }
}
