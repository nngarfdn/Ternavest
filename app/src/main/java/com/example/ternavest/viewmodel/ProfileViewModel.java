package com.example.ternavest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.model.Profile;
import com.example.ternavest.repository.ProfileRepository;
import com.google.firebase.firestore.CollectionReference;

public class ProfileViewModel extends ViewModel {
    private final ProfileRepository repository = new ProfileRepository();

    public LiveData<Profile> getData(){
        return repository.getData();
    }

    public void loadData(){
        repository.query();
    }

    public void loadData(String userId){
        repository.query(userId);
    }

    public void insert(Profile profile){
        repository.insert(profile);
    }

    public void update(Profile profile){
        repository.insert(profile);
    }

    public CollectionReference getReference(){
        return repository.reference;
    }
}
