package com.example.ternavest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Regencies {
    @SerializedName("kota_kabupaten")
    @Expose
    private ArrayList<Attributes> regencies;

    public ArrayList<Attributes> getRegencies() {
        return regencies;
    }
}
