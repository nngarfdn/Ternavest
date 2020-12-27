package com.example.ternavest.rest;

import com.example.ternavest.response.Districts;
import com.example.ternavest.response.Provinces;
import com.example.ternavest.response.Regencies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("provinsi")
        // Daftar 34 provinsi
    Call<Provinces> getProvinces();

    @GET("kota")
        // Daftar kabupaten/kota sesuai provinsi
    Call<Regencies> getRegencies(
            @Query("id_provinsi") int ID_PROVINCE
    );

    @GET("kecamatan")
        // Daftar kecamatan sesuai kabupaten/kota
    Call<Districts> getDistricts(
            @Query("id_kota") int ID_REGENCY
    );
}

