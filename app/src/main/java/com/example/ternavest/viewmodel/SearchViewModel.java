package com.example.ternavest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.model.Filter;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.repository.SearchRepository;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {
    private final SearchRepository repository = new SearchRepository();

    public LiveData<ArrayList<Proyek>> getData(){
        return repository.getData();
    }

    public void loadData(Filter filter){
        repository.query(filter);
    }
}
