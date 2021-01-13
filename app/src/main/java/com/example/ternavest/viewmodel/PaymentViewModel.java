package com.example.ternavest.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.callback.OnImageUploadCallback;
import com.example.ternavest.model.Payment;
import com.example.ternavest.repository.PaymentRepository;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class PaymentViewModel extends ViewModel {
    private final PaymentRepository repository = new PaymentRepository();

    public LiveData<ArrayList<Payment>> getData(){
        return repository.getData();
    }

    public void loadData(String portfolioId){
        repository.query(portfolioId);
    }

    public void insert(String portfolioId, Payment payment){
        repository.insert(portfolioId, payment);
    }

    public void update(String portfolioId, String paymentId, String status, String rejectionNote){
        repository.update(portfolioId, paymentId, status, rejectionNote);
    }

    public void delete(String portfolioId, String paymentId){
        repository.delete(portfolioId, paymentId);
    }

    public CollectionReference getReference(){
        return repository.reference;
    }

    public void uploadImage(Context context, String portfolioId, Uri uri, String fileName, OnImageUploadCallback callback){
        repository.uploadImage(context, portfolioId, uri, fileName, callback);
    }

    public void deleteImage(String imageUrl){
        repository.deleteImage(imageUrl);
    }
}
