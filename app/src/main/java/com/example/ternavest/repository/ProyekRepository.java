package com.example.ternavest.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Proyek;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProyekRepository {

    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final MutableLiveData<Proyek> resultData = new MutableLiveData<>();
    public LiveData<Proyek> getData(){
        return resultData;
    }

    public void insert(String userId, Proyek proyek){
        database.collection("proyek").document(userId)
                .set(hashMapProfile(proyek))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Document was added");
                    else Log.w(TAG, "Error adding document", task.getException());
                });
    }

    private Map<String, Object> hashMapProfile(Proyek proyek){
        Map<String, Object> document = new HashMap<>();
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
