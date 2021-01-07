package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable {
    private List<String> portfolioId;

    public User() {}

    public User(List<String> portfolioId) {
        this.portfolioId = portfolioId;
    }

    protected User(Parcel in) {
        portfolioId = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(portfolioId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public List<String> getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(List<String> portfolioId) {
        this.portfolioId = portfolioId;
    }
}
