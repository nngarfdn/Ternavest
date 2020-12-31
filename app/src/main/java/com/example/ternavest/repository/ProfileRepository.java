package com.example.ternavest.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {
    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public final CollectionReference reference = database.collection("profil");

    private final MutableLiveData<Profile> resultData = new MutableLiveData<>();
    public LiveData<Profile> getData(){
        return resultData;
    }

    public void query(String userId){
        reference.document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Profile profile = new Profile();
                            profile = snapshotToObject(task.getResult());
                            resultData.postValue(profile);
                            Log.d(TAG, "Document was queried");
                        } else Log.w(TAG, "Error querying document", task.getException());
                    }
                });
    }

    public void query(){
        reference.document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Profile profile;

                            if (!task.getResult().exists()) { // Kalau belum ada profil di Firestore
                                profile = new Profile();
                                profile.setLevel("peternak");
                                profile.setVerificationStatus("pending");
                                insert(profile);
                            } else profile = snapshotToObject(task.getResult());

                            resultData.postValue(profile);
                            Log.d(TAG, "Document was queried");
                        } else Log.w(TAG, "Error querying document", task.getException());
                    }
                });
    }

    public void insert(Profile profile){
        reference.document(userId)
                .set(objectToHashMap(profile))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was added");
                        else Log.w(TAG, "Error adding document", task.getException());
                    }
                });
    }

    public void update(Profile profile){
        reference.document(userId)
                .update(objectToHashMap(profile))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was updated");
                        else Log.w(TAG, "Error updating document", task.getException());
                    }
                });
    }

    private Map<String, Object> objectToHashMap(Profile profile){
        Map<String, Object> document = new HashMap<>();
        document.put("level", profile.getLevel());
        document.put("foto", profile.getPhoto());
        document.put("ktp", profile.getKtp());
        document.put("alamat", profile.getAddress());
        document.put("telepon", profile.getPhone());
        document.put("whatsapp", profile.getWhatsApp());
        document.put("namaBank", profile.getAccountBank());
        document.put("namaRekening", profile.getAccountName());
        document.put("nomorRekening", profile.getAccountNumber());
        document.put("statusVerifikasi", profile.getVerificationStatus());
        return document;
    }

    private Profile snapshotToObject(DocumentSnapshot document){
        return new Profile(
                document.getString("level"),
                document.getString("foto"),
                document.getString("ktp"),
                document.getString("alamat"),
                document.getString("telepon"),
                document.getString("whatsapp"),
                document.getString("namaBank"),
                document.getString("namaRekening"),
                document.getString("nomorRekening"),
                document.getString("statusVerifikasi")
        );
    }
}
