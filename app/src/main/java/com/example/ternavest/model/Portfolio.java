package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Portfolio implements Parcelable {
    private String id;
    private String investorId;
    private String breederId;
    private String projectId;
    private int count;
    private long cost;
    private long totalCost;
    private String status;

    public Portfolio() {}

    public Portfolio(String id, String investorId, String breederId, String projectId, int count, long cost, long totalCost, String status) {
        this.id = id;
        this.investorId = investorId;
        this.breederId = breederId;
        this.projectId = projectId;
        this.count = count;
        this.cost = cost;
        this.totalCost = totalCost;
        this.status = status;
    }

    protected Portfolio(Parcel in) {
        id = in.readString();
        investorId = in.readString();
        breederId = in.readString();
        projectId = in.readString();
        count = in.readInt();
        cost = in.readLong();
        totalCost = in.readLong();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(investorId);
        dest.writeString(breederId);
        dest.writeString(projectId);
        dest.writeInt(count);
        dest.writeLong(cost);
        dest.writeLong(totalCost);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Portfolio> CREATOR = new Creator<Portfolio>() {
        @Override
        public Portfolio createFromParcel(Parcel in) {
            return new Portfolio(in);
        }

        @Override
        public Portfolio[] newArray(int size) {
            return new Portfolio[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getBreederId() {
        return breederId;
    }

    public void setBreederId(String breederId) {
        this.breederId = breederId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
