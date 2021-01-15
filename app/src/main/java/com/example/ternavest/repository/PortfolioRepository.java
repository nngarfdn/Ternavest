package com.example.ternavest.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ternavest.model.Portfolio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class PortfolioRepository {
    private final String TAG = getClass().getSimpleName();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public final CollectionReference reference = database.collection("portofolio");

    private final MutableLiveData<ArrayList<Portfolio>> resultData = new MutableLiveData<>();
    public LiveData<ArrayList<Portfolio>> getData(){
        return resultData;
    }

    public void query(String userLevel){ // Investor, peternak
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = reference;
        if (userLevel.equals(LEVEL_PETERNAK)) query = query.whereEqualTo("idPeternak", userId);
        else if (userLevel.equals(LEVEL_INVESTOR)) query = query.whereEqualTo("idInvestor", userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<Portfolio> portfolioList = new ArrayList<>();

                    for (DocumentSnapshot snapshot : task.getResult()){
                        Portfolio portfolio = new Portfolio();
                        portfolio = snapshotToObject(snapshot);
                        portfolioList.add(portfolio);
                    }

                    resultData.postValue(portfolioList);
                    Log.d(TAG, "Document was queried");
                } else Log.w(TAG, "Error querying document", task.getException());
            }
        });
    }

    public void query(String userId, String userLevel){ // Investor
        Query query = reference;
        if (userLevel.equals(LEVEL_PETERNAK)) query = query.whereEqualTo("idPeternak", userId);
        else if (userLevel.equals(LEVEL_INVESTOR)) query = query.whereEqualTo("idInvestor", userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<Portfolio> portfolioList = new ArrayList<>();

                    for (DocumentSnapshot snapshot : task.getResult()){
                        Portfolio portfolio = new Portfolio();
                        portfolio = snapshotToObject(snapshot);
                        portfolioList.add(portfolio);
                    }

                    resultData.postValue(portfolioList);
                    Log.d(TAG, "Document was queried");
                } else Log.w(TAG, "Error querying document", task.getException());
            }
        });
    }

    // Jangan simpan biaya dulu karena ada kemungkinan biaya diedit oleh peternak, hanya lakukan pembayaran dengan biaya ter-update
    public void insert(Portfolio portfolio){  // Investor
        portfolio.setId(reference.document().getId()); // Id otomatis
        reference.document(portfolio.getId())
                .set(objectToHashMap(portfolio))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was added");
                        else Log.w(TAG, "Error adding document", task.getException());
                    }
                });
    }

    // Update ketika belum bayar/setelah pembayaran ditolak -> hanya bisa edit jumlah ekor
    public void update(String portfolioId, int count){  // Peternak
        reference.document(portfolioId)
                .update("jumlahEkor", count)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was updated");
                        else Log.w(TAG, "Error updating document", task.getException());
                    }
                });
    }

    // Update ketika pembayaran disetujui -> ganti status dan simpan biaya karena ada kemungkinan biaya per ekor diedit
    // Jika pembayaran terakhir ditolak, status tetap pending (tidak melakukan update)
    public void update(String portfolioId, long cost, long totalCost, String status){  // Investor
        reference.document(portfolioId)
                .update("biaya", cost, "totalBiaya", totalCost, "status", status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was updated");
                        else Log.w(TAG, "Error updating document", task.getException());
                    }
                });
    }

    public void delete(Portfolio portfolio){ // Investor
        reference.document(portfolio.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.d(TAG, "Document was deleted");
                        else Log.w(TAG, "Error deleting document", task.getException());
                    }
                });
    }

    private Map<String, Object> objectToHashMap(Portfolio portfolio){
        Map<String, Object> document = new HashMap<>();
        document.put("id", portfolio.getId());
        document.put("idInvestor", portfolio.getInvestorId());
        document.put("idPeternak", portfolio.getBreederId());
        document.put("idProyek", portfolio.getProjectId());
        document.put("jumlahEkor", portfolio.getCount());
        document.put("biaya", portfolio.getCost());
        document.put("totalBiaya", portfolio.getTotalCost());
        document.put("status", portfolio.getStatus());
        return document;
    }

    private Portfolio snapshotToObject(DocumentSnapshot document){
        return new Portfolio(
                document.getString("id"),
                document.getString("idInvestor"),
                document.getString("idPeternak"),
                document.getString("idProyek"),
                document.getLong("jumlahEkor").intValue(),
                document.getLong("biaya"),
                document.getLong("totalBiaya"),
                document.getString("status")
        );
    }
}
