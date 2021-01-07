package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Payment implements Parcelable {
    private String id;
    private String date;
    private String time;
    private String image;
    private String status;
    private String rejectionNote;

    public Payment() {}

    public Payment(String id, String date, String time, String image, String status, String rejectionNote) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.image = image;
        this.status = status;
        this.rejectionNote = rejectionNote;
    }

    protected Payment(Parcel in) {
        id = in.readString();
        date = in.readString();
        time = in.readString();
        image = in.readString();
        status = in.readString();
        rejectionNote = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(image);
        dest.writeString(status);
        dest.writeString(rejectionNote);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }

    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }
}
