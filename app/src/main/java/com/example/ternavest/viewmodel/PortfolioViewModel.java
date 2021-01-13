package com.example.ternavest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.model.Portfolio;
import com.example.ternavest.repository.PortfolioRepository;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class PortfolioViewModel extends ViewModel {
    private final PortfolioRepository repository = new PortfolioRepository();

    public LiveData<ArrayList<Portfolio>> getData(){
        return repository.getData();
    }

    public void loadData(String userLevel){
        repository.query(userLevel);
    }

    public void loadData(String userId, String userLevel){
        repository.query(userId, userLevel);
    }

    public void insert(Portfolio portfolio){
        repository.insert(portfolio);
    }

    public void update(String portfolioId, int count){
        repository.update(portfolioId, count);
    }

    public void update(String portfolioId, long cost, long totalCost, String status){
        repository.update(portfolioId, cost, totalCost, status);
    }

    public void delete(Portfolio portfolio){
        repository.delete(portfolio);
    }

    public CollectionReference getReference(){
        return repository.reference;
    }
}
