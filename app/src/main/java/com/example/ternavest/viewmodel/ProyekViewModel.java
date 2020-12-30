package com.example.ternavest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.model.Proyek;
import com.example.ternavest.repository.ProyekRepository;

import java.util.List;

public class ProyekViewModel extends ViewModel {

    private final ProyekRepository repository = new ProyekRepository();

    public LiveData<List<String>> getData() {
        return  repository.getData();
    }

    public void loadData(){
        repository.query();
    }

    public void insert(Proyek proyek){
        repository.insert(proyek);
    }

}
