package com.example.ternavest.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.callback.OnImageUploadCallback;
import com.example.ternavest.model.Payment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ternavest.utils.ImageUtils.convertUriToByteArray;
import static com.example.ternavest.utils.ImageUtils.getCompressedByteArray;

public class PaymentRepository {
    public static final String FOLDER_PAYMENT = "pembayaran";

    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public final CollectionReference reference = database.collection("portofolio");

    private final MutableLiveData<ArrayList<Payment>> resultData = new MutableLiveData<>();
    public LiveData<ArrayList<Payment>> getData(){
        return resultData;
    }

    // Dipanggil waktu buka detail portofolio
    public void query(String portfolioId){ // Investor, peternak
        reference.document(portfolioId).collection("pembayaran")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        ArrayList<Payment> paymentList = new ArrayList<>();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            Payment payment = snapshotToObject(snapshot);
                            paymentList.add(payment);
                            Log.d(TAG, "query: " + payment.getId());
                        }
                        resultData.postValue(paymentList);
                        Log.d(TAG, "Document was queried");
                    } else Log.w(TAG, "Error querying document", task.getException());
                });
    }

    public void insert(String portfolioId, Payment payment){ // Investor
        reference.document(portfolioId).collection("pembayaran").document(payment.getId())
                .set(objectToHashMap(payment))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Document was added");
                    else Log.w(TAG, "Error adding document", task.getException());
                });
    }

    // Saat pembayaran sudah diajukan, hanya bisa update status pembayaran, disetujui/ditolak
    public void update(String portfolioId, String paymentId, String status, String rejectionNote){ // Peternak
        reference.document(portfolioId).collection("pembayaran").document(paymentId)
                .update("status", status, "alasanDitolak", rejectionNote)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Document was updated");
                    else Log.w(TAG, "Error updating document", task.getException());
                });
    }

    // Dipanggil ketika menghapus portofolio yang belum bayar
    public void delete(String portfolioId, String paymentId){ // Investor
        reference.document(portfolioId).collection("pembayaran").document(paymentId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Document was deleted");
                    else Log.w(TAG, "Error deleting document", task.getException());
                });
    }

    public void uploadImage(Context context, String portfolioId, Uri uri, String fileName, OnImageUploadCallback callback){ // Investor
        byte[] image = convertUriToByteArray(context, uri);
        image = getCompressedByteArray(image, false);

        StorageReference reference = storage.getReference().child(FOLDER_PAYMENT + "/" + portfolioId + "/" + fileName);
        UploadTask uploadTask = reference.putBytes(image);
        uploadTask.addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            callback.onSuccess(uri1.toString());
            Log.d(TAG, "Image was uploaded");
        })).addOnFailureListener(e -> Log.w(TAG, "Error uploading image", e));
    }

    public void deleteImage(String imageUrl){ // Investor
        storage.getReferenceFromUrl(imageUrl).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Image was deleted"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting image", e));
    }

    private Map<String, Object> objectToHashMap(Payment payment){
        Map<String, Object> document = new HashMap<>();
        document.put("id", payment.getId());
        document.put("tanggal", payment.getDate());
        document.put("waktu", payment.getTime());
        document.put("foto", payment.getImage());
        document.put("status", payment.getStatus());
        document.put("alasanDitolak", payment.getRejectionNote());
        return document;
    }

    private Payment snapshotToObject(DocumentSnapshot document){
        return new Payment(
                document.getString("id"),
                document.getString("tanggal"),
                document.getString("waktu"),
                document.getString("foto"),
                document.getString("status"),
                document.getString("alasanDitolak")
        );
    }
}
