package com.example.ternavest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.model.Proyek;
import com.example.ternavest.repository.ProyekRepository;

public class ProyekViewModel extends ViewModel {

    private final ProyekRepository repository = new ProyekRepository();

    public LiveData<Proyek> getData() {
        return  repository.getData();
    }

    public void insert(String userId, Proyek proyek){
        repository.insert(userId, proyek);
    }

}
