package com.example.ternavest.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Proyek;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProyekRepository {

    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<String>> resultData = new MutableLiveData<>();

    public LiveData<List<String>> getData() {
        return resultData;
    }

    public void query() {
        database.collection("proyek").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    resultData.postValue(list);
                    Log.d(TAG, list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void insert(Proyek proyek) {
        DocumentReference ref = database.collection("proyek").document();
        proyek.setId(ref.getId());
        ref.set(hashMapProfile(proyek))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Document was added");
                    else Log.w(TAG, "Error adding document", task.getException());
                });
    }

    private Map<String, Object> hashMapProfile(Proyek proyek) {
        Map<String, Object> document = new HashMap<>();
        document.put("id", proyek.getId());
        document.put("uuid", proyek.getUuid());
        document.put("namaProyek", proyek.getNamaProyek());
        document.put("deskripsiProyek", proyek.getDeskripsiProyek());
        document.put("jenisHewan", proyek.getJenisHewan());
        document.put("waktuMulai", proyek.getWaktuMulai());
        document.put("waktuSelesai", proyek.getWaktuSelesai());
        document.put("biayaHewan", proyek.getBiayaHewan());
        document.put("provinsi", proyek.getProvinsi());
        document.put("kabupaten", proyek.getKabupaten());
        document.put("kecamatan", proyek.getKecamatan());
        document.put("alamatLengkap", proyek.getAlamatLengkap());
        document.put("photoProyek", proyek.getPhotoProyek());
        return document;
    }

}
