package com.adriansng.projet_7_go4lunch.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.adriansng.projet_7_go4lunch.model.POJO.DetailPlace.Location;

import java.util.Objects;

public class Restaurant implements Parcelable {

    private String name;
    private String placeId;
    private final String photo;
    private final String address;
    private final String phoneNumber;
    private final String website;
    private double mNotation;
    private Location location;
    private String distance;
    private boolean openHours;
    private final int numberWorkmate;
    private final boolean isFavorite;

    // --- CONSTRUCTORS ---

    /**
     * Constructor for Places request
     */
    public Restaurant(String placeId, String name, String photo, String address, String phoneNumber, String website, double mNotation, Location location, boolean openHours, String distance, int numberWorkmate, boolean isFavorite) {
        this.placeId = placeId;
        this.name = name;
        this.photo = photo;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.mNotation = mNotation;
        this.location = location;
        this.openHours= openHours;
        this.distance= distance;
        this.numberWorkmate=numberWorkmate;
        this.isFavorite=isFavorite;
    }


    /**
     * Constructor for Places Detail request
     */

    public Restaurant(String placeIdRes, String name, String photo, String address, String numberPhone, String webSite, int numberWorkmates, boolean isFavorite) {
        this.placeId = placeIdRes;
        this.name = name;
        this.photo = photo;
        this.address = address;
        this.phoneNumber = numberPhone;
        this.website = webSite;
        this.numberWorkmate = numberWorkmates;
        this.isFavorite = isFavorite;
    }


    // --- GETTERS ---


    protected Restaurant(Parcel in) {
        name = in.readString();
        placeId = in.readString();
        photo = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        website = in.readString();
        mNotation = in.readDouble();
        distance = in.readString();
        openHours = in.readByte() != 0;
        numberWorkmate = in.readInt();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPhoto() {
        return photo;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public double getNotation() {
        return mNotation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getOpenHours() {
        return openHours;
    }

    // --- SETTERS ---

    public String getDistance() {
        return distance;
    }

    public int getNumberWorkmate() {
        return numberWorkmate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // --- OVERRIDES ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(placeId, that.placeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeString(photo);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(website);
        dest.writeDouble(mNotation);
        dest.writeByte((byte) (openHours ? 1 : 0));
        dest.writeString(distance);
    }
}