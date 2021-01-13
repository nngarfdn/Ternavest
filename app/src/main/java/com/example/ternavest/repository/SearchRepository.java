package com.example.ternavest.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Proyek;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchRepository {
    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final MutableLiveData<ArrayList<Proyek>> resultData = new MutableLiveData<>();

    public LiveData<ArrayList<Proyek>> getData() {
        return resultData;
    }

    public void query(String filter) {
        Query query = database.collection("proyek");


        query.whereEqualTo("namaProyek", filter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Proyek> listProduct = new ArrayList<>();
                            QuerySnapshot querySnapshot = task.getResult();

                            for (DocumentSnapshot snapshot : querySnapshot) {
                                Proyek produk = snapshot.toObject(Proyek.class);
                                Log.d(TAG, "Nama item: " + produk.getNamaProyek());
                                listProduct.add(produk);
                                continue;
                            }

                            resultData.postValue(listProduct);
                            Log.d(TAG, "Document was queried");
                        } else Log.w(TAG, "Error querying document", task.getException());
                    }
                });
    }
}
