package com.example.ternavest.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.callback.OnImageUploadCallback;
import com.example.ternavest.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;
import static com.example.ternavest.utils.AppUtils.VERIF_PENDING;
import static com.example.ternavest.utils.ImageUtils.convertUriToByteArray;
import static com.example.ternavest.utils.ImageUtils.getCompressedByteArray;

public class ProfileRepository {
    public static final String FOLDER_KTP = "ktp";
    public static final String FOLDER_PROFILE = "profil";

    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

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
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                profile = new Profile(
                                        firebaseUser.getUid(),
                                        firebaseUser.getDisplayName(),
                                        firebaseUser.getEmail(),
                                        LEVEL_PETERNAK,
                                        VERIF_PENDING,
                                        null
                                );
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
                .set(objectToHashMapForInsert(profile))
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

    // Khusus ganti foto profil
    public void update(String photo){
        reference.document(userId)
                .update("foto", photo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Profile photo was updated");
                        else Log.w(TAG, "Error updating profile photo", task.getException());
                    }
                });
    }

    // Khusus verifikasi KTP
    public void sendVerification(String ktp){
        reference.document(userId)
                .update(
                        "ktp", ktp,
                        "statusVerifikasi", VERIF_PENDING,
                        "alasanDitolakVerifikasi", null
                )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "sendVerification successful");
                        else Log.w(TAG, "Error sendVerification", task.getException());
                    }
                });

        // Tambah ke antrian verifikasi
        database.collection("admin").document("verifikasiKTP")
                .update("pending", FieldValue.arrayUnion(userId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was updated");
                        else Log.w(TAG, "Error updating document", task.getException());
                    }
                });
    }

    public void uploadImage(Context context, Uri uri, String folderName, String fileName, OnImageUploadCallback callback){
        byte[] image = convertUriToByteArray(context, uri);
        image = getCompressedByteArray(image, folderName.equals(FOLDER_PROFILE)); // Jika foto profil, perkecil ukuran

        StorageReference reference = storage.getReference().child(folderName + "/" + fileName);
        UploadTask uploadTask = reference.putBytes(image);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.onSuccess(uri.toString());
                        Log.d(TAG, "Image was uploaded");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error uploading image", e);
            }
        });
    }

    public void deleteImage(String imageUrl){
        storage.getReferenceFromUrl(imageUrl).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Image was deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting image", e);
                    }
                });
    }

    private Map<String, Object> objectToHashMapForInsert(Profile profile){
        Map<String, Object> document = new HashMap<>();
        document.put("id", profile.getId());
        document.put("nama", profile.getName());
        document.put("email", profile.getEmail());
        document.put("level", profile.getLevel());
        document.put("statusVerifikasi", profile.getVerificationStatus());
        document.put("alasanDitolakVerifikasi", profile.getVerificationRejectionNote());
        return document;
    }

    private Map<String, Object> objectToHashMap(Profile profile){
        Map<String, Object> document = new HashMap<>();
        //document.put("id", profile.getId());
        document.put("nama", profile.getName());
        //document.put("email", profile.getEmail());
        //document.put("level", profile.getLevel());
        //document.put("foto", profile.getPhoto());
        //document.put("ktp", profile.getKtp());
        document.put("alamat", profile.getAddress());
        document.put("telepon", profile.getPhone());
        document.put("whatsapp", profile.getWhatsApp());
        document.put("namaBank", profile.getAccountBank());
        document.put("namaRekening", profile.getAccountName());
        document.put("nomorRekening", profile.getAccountNumber());
        //document.put("statusVerifikasi", profile.getVerificationStatus());
        //document.put("alasanDitolakVerifikasi", profile.getVerificationRejectionNote());
        return document;
    }

    private Profile snapshotToObject(DocumentSnapshot document){
        return new Profile(
                document.getString("id"),
                document.getString("nama"),
                document.getString("email"),
                document.getString("level"),
                document.getString("foto"),
                document.getString("ktp"),
                document.getString("alamat"),
                document.getString("telepon"),
                document.getString("whatsapp"),
                document.getString("namaBank"),
                document.getString("namaRekening"),
                document.getString("nomorRekening"),
                document.getString("statusVerifikasi"),
                document.getString("alasanDitolakVerifikasi")
        );
    }
}
