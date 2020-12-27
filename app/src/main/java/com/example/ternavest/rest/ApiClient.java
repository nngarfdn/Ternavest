package com.example.ternavest.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://dev.farizdotid.com/api/daerahindonesia/";
    private static ApiService apiService;

    public ApiClient(){ // Buat instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getService(){
        return apiService;
    }
}