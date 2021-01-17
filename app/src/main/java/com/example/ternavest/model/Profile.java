package com.example.ternavest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Profile implements Parcelable {
    private String id;
    private String name;
    private String email;
    private String level;
    private String photo;
    private String ktp;
    private String address;
    private String phone;
    private String whatsApp;
    private String accountBank;
    private String accountName;
    private String accountNumber;
    private String verificationStatus;
    private String verificationRejectionNote;

    public Profile() {}

    public Profile(String id, String name, String email, String level, String photo, String ktp, String address, String phone, String whatsApp, String accountBank, String accountName, String accountNumber, String verificationStatus, String verificationRejectionNote) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.level = level;
        this.photo = photo;
        this.ktp = ktp;
        this.address = address;
        this.phone = phone;
        this.whatsApp = whatsApp;
        this.accountBank = accountBank;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.verificationStatus = verificationStatus;
        this.verificationRejectionNote = verificationRejectionNote;
    }

    // Dipakai saat insert profile (satu kali)
    public Profile(String id, String name, String email, String level, String verificationStatus, String verificationRejectionNote) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.level = level;
        this.verificationStatus = verificationStatus;
        this.verificationRejectionNote = verificationRejectionNote;
    }

    protected Profile(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        level = in.readString();
        photo = in.readString();
        ktp = in.readString();
        address = in.readString();
        phone = in.readString();
        whatsApp = in.readString();
        accountBank = in.readString();
        accountName = in.readString();
        accountNumber = in.readString();
        verificationStatus = in.readString();
        verificationRejectionNote = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(level);
        dest.writeString(photo);
        dest.writeString(ktp);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(whatsApp);
        dest.writeString(accountBank);
        dest.writeString(accountName);
        dest.writeString(accountNumber);
        dest.writeString(verificationStatus);
        dest.writeString(verificationRejectionNote);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getLevel() {
        return level;
    }

    public String getPhoto() {
        return photo;
    }

    public String getKtp() {
        return ktp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getVerificationRejectionNote() {
        return verificationRejectionNote;
    }

    public static Creator<Profile> getCREATOR() {
        return CREATOR;
    }
}
