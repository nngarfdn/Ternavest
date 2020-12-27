package com.example.ternavest.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ternavest.response.Districts;
import com.example.ternavest.response.Provinces;
import com.example.ternavest.response.Regencies;
import com.example.ternavest.rest.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationViewModel extends ViewModel {
    private final String TAG = getClass().getSimpleName();
    private final ApiClient client = new ApiClient();

    private final MutableLiveData<Provinces> resultProvinces = new MutableLiveData<>();
    private final MutableLiveData<Regencies> resultRegencies = new MutableLiveData<>();
    private final MutableLiveData<Districts> resultDistricts = new MutableLiveData<>();

    public LiveData<Provinces> getProvinces(){
        return resultProvinces;
    }
    public LiveData<Regencies> getRegencies(){
        return resultRegencies;
    }
    public LiveData<Districts> getDistricts(){
        return resultDistricts;
    }

    public void loadProvinces(){
        final Provinces[] itemFound = {new Provinces()};
        client.getService().getProvinces().enqueue(new Callback<Provinces>() {
            @Override
            public void onResponse(@NonNull Call<Provinces> call, @NonNull Response<Provinces> response) {
                try {
                    if (response.isSuccessful()) itemFound[0] = response.body();
                    resultProvinces.postValue(itemFound[0]);
                } catch (Exception e){
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Provinces> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure: " + t.getMessage());
            }
        });
    }

    public void loadRegencies(int idProvince){
        final Regencies[] itemFound = {new Regencies()};
        client.getService().getRegencies(idProvince).enqueue(new Callback<Regencies>() {
            @Override
            public void onResponse(@NonNull Call<Regencies> call, @NonNull Response<Regencies> response) {
                try {
                    if (response.isSuccessful()) itemFound[0] = response.body();
                    resultRegencies.postValue(itemFound[0]);
                } catch (Exception e){
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Regencies> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure: " + t.getMessage());
            }
        });
    }

    public void loadDistricts(int idRegency){
        final Districts[] itemFound = {new Districts()};
        client.getService().getDistricts(idRegency).enqueue(new Callback<Districts>() {
            @Override
            public void onResponse(@NonNull Call<Districts> call, @NonNull Response<Districts> response) {
                try {
                    if (response.isSuccessful()) itemFound[0] = response.body();
                    resultDistricts.postValue(itemFound[0]);
                } catch (Exception e){
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Districts> call, @NonNull Throwable t) {
                Log.d(TAG, "Failure: " + t.getMessage());
            }
        });
    }
}
