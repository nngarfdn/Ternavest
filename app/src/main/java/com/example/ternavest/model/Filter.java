package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {
    private String kataKunci;
    private boolean provinsi;
    private boolean kabupaten;
    private boolean kecamatan;
    private String namaProvinsi;
    private String namaKabupaten;
    private String namaKecamatan;

    public Filter() {
        this.kataKunci = "";
        this.provinsi = false;
        this.kabupaten = false;
        this.kecamatan = false;
        this.namaProvinsi = "";
        this.namaKabupaten = "";
        this.namaKecamatan = "";
    }

    protected Filter(Parcel in) {
        kataKunci = in.readString();
        provinsi = in.readByte() != 0;
        kabupaten = in.readByte() != 0;
        kecamatan = in.readByte() != 0;
        namaProvinsi = in.readString();
        namaKabupaten = in.readString();
        namaKecamatan = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kataKunci);
        dest.writeByte((byte) (provinsi ? 1 : 0));
        dest.writeByte((byte) (kabupaten ? 1 : 0));
        dest.writeByte((byte) (kecamatan ? 1 : 0));
        dest.writeString(namaProvinsi);
        dest.writeString(namaKabupaten);
        dest.writeString(namaKecamatan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    public String getKataKunci() {
        return kataKunci;
    }

    public void setKataKunci(String kataKunci) {
        this.kataKunci = kataKunci;
    }

    public boolean isProvinsi() {
        return provinsi;
    }

    public void setProvinsi(boolean provinsi) {
        this.provinsi = provinsi;
    }

    public boolean isKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(boolean kabupaten) {
        this.kabupaten = kabupaten;
    }

    public boolean isKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(boolean kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getNamaProvinsi() {
        return namaProvinsi;
    }

    public void setNamaProvinsi(String namaProvinsi) {
        this.namaProvinsi = namaProvinsi;
    }

    public String getNamaKabupaten() {
        return namaKabupaten;
    }

    public void setNamaKabupaten(String namaKabupaten) {
        this.namaKabupaten = namaKabupaten;
    }

    public String getNamaKecamatan() {
        return namaKecamatan;
    }

    public void setNamaKecamatan(String namaKecamatan) {
        this.namaKecamatan = namaKecamatan;
    }
}
