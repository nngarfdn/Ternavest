package com.example.ternavest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Districts {
    @SerializedName("kecamatan")
    @Expose
    private ArrayList<Attributes> districts;

    public ArrayList<Attributes> getDistricts() {
        return districts;
    }
}
