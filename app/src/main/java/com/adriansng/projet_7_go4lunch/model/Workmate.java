package com.adriansng.projet_7_go4lunch.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Workmate  implements Parcelable {

    private String uid;
    private String nameWorkmate;
    private String avatar;
    private String email;
    private String chooseRestaurant;
    private String nameChooseRestaurant;
    private String addressChooseRestaurant;

    // --- CONSTRUCTORS ---

    public Workmate(String uid, String nameWorkmate, String avatar, String email) {
        this.uid=uid;
        this.nameWorkmate = nameWorkmate;
        this.avatar = avatar;
        this.email = email;
    }

    public Workmate(String uid, String nameWorkmate, String avatar) {
        this.uid=uid;
        this.nameWorkmate = nameWorkmate;
        this.avatar = avatar;
    }

    public Workmate() {
    }

    protected Workmate(Parcel in) {
        uid = in.readString();
        nameWorkmate = in.readString();
        avatar = in.readString();
        email = in.readString();
        chooseRestaurant = in.readString();
        nameChooseRestaurant = in.readString();
        addressChooseRestaurant= in.readString();
    }

    public static final Creator<Workmate> CREATOR = new Creator<Workmate>() {
        @Override
        public Workmate createFromParcel(Parcel in) {
            return new Workmate(in);
        }

        @Override
        public Workmate[] newArray(int size) {
            return new Workmate[size];
        }
    };

    // --- GETTERS ---

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameWorkmate() {
        return nameWorkmate;
    }

    public void setNameWorkmate(String nameWorkmate) {
        this.nameWorkmate = nameWorkmate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // --- SETTERS ---

    public String getChooseRestaurant() {
        return chooseRestaurant;
    }

    public void setChooseRestaurant(String chooseRestaurant) {
        this.chooseRestaurant = chooseRestaurant;
    }

    public String getNameChooseRestaurant() {
        return nameChooseRestaurant;
    }

    public void setNameChooseRestaurant(String nameChooseRestaurant) {
        this.nameChooseRestaurant = nameChooseRestaurant;
    }

    public String getAddressChooseRestaurant() {
        return addressChooseRestaurant;
    }

    public void setAddressChooseRestaurant(String addressChooseRestaurant) {
        this.addressChooseRestaurant = addressChooseRestaurant;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // --- OVERRIDE ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workmate workmate = (Workmate) o;
        return Objects.equals(uid, workmate.uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nameWorkmate);
        dest.writeString(avatar);
        dest.writeString(email);
        dest.writeString(chooseRestaurant);
        dest.writeString(nameChooseRestaurant);
        dest.writeString(addressChooseRestaurant);
    }
}
