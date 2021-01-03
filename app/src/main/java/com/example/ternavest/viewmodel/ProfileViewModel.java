package com.example.ternavest.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ternavest.callback.OnImageUploadCallback;
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
        repository.update(profile);
    }

    public void update(String photo){
        repository.update(photo);
    }

    public void sendVerification(String ktp){
        repository.sendVerification(ktp);
    }

    public CollectionReference getReference(){
        return repository.reference;
    }

    public void uploadImage(Context context, Uri uri, String folderName, String fileName, OnImageUploadCallback callback){
        repository.uploadImage(context, uri, folderName, fileName, callback);
    }

    public void deleteImage(String imageUrl){
        repository.deleteImage(imageUrl);
    }
}
