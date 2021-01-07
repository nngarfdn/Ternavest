package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Portfolio implements Parcelable {
    private String id;
    private String userId;
    private String projectId;
    private List<String> paymentId;
    private int count;
    private long cost;
    private long totalCost;

    public Portfolio() {}

    public Portfolio(String id, String userId, String projectId, List<String> paymentId, int count, long cost, long totalCost) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.paymentId = paymentId;
        this.count = count;
        this.cost = cost;
        this.totalCost = totalCost;
    }

    protected Portfolio(Parcel in) {
        id = in.readString();
        userId = in.readString();
        projectId = in.readString();
        paymentId = in.createStringArrayList();
        count = in.readInt();
        cost = in.readLong();
        totalCost = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(projectId);
        dest.writeStringList(paymentId);
        dest.writeInt(count);
        dest.writeLong(cost);
        dest.writeLong(totalCost);
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<String> getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(List<String> paymentId) {
        this.paymentId = paymentId;
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
}
