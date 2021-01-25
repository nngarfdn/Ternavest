package com.example.ternavest.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Filter;
import com.example.ternavest.model.Proyek;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.ternavest.utils.DateUtils.getCurrentDate;

public class SearchRepository {
    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final MutableLiveData<ArrayList<Proyek>> resultData = new MutableLiveData<>();
    public LiveData<ArrayList<Proyek>> getData() {
        return resultData;
    }

    public void query(Filter filter) {
        Query query = database.collection("proyek").whereGreaterThan("waktuMulai", getCurrentDate());

        // Filter lokasi
        if (filter.isProvinsi()) query = query.whereEqualTo("provinsi", filter.getNamaProvinsi());
        if (filter.isKabupaten()) query = query.whereEqualTo("kabupaten", filter.getNamaKabupaten());
        if (filter.isKecamatan()) query = query.whereEqualTo("kecamatan", filter.getNamaKecamatan());

        query.orderBy("waktuMulai", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Proyek> listProduct = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        for (DocumentSnapshot snapshot : querySnapshot) {
                            Proyek project = snapshot.toObject(Proyek.class);
                            if (project.getNamaProyek().toLowerCase().contains(filter.getKataKunci().toLowerCase()) ||
                                    project.getJenisHewan().toLowerCase().contains(filter.getKataKunci().toLowerCase())){
                                listProduct.add(project);
                            }
                            Log.d(TAG, "Nama item: " + project.getNamaProyek());
                        }

                        resultData.postValue(listProduct);
                        Log.d(TAG, "Document was queried");
                    } else Log.w(TAG, "Error querying document", task.getException());
                });
    }
}
