package com.example.ternavest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Provinces {
    @SerializedName("provinsi")
    @Expose
    private ArrayList<Attributes> provinces;

    public ArrayList<Attributes> getProvinces() {
        return provinces;
    }
}
