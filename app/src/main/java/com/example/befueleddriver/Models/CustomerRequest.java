package com.example.befueleddriver.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomerRequest {

    private String carId;
    private boolean isorderplaced;
    private String userID;

    public CustomerRequest(String carId, boolean isorderplaced, String userID) {
        this.carId = carId;
        this.isorderplaced = isorderplaced;
        this.userID = userID;
    }
    public CustomerRequest() {

    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public boolean isIsorderplaced() {
        return isorderplaced;
    }

    public void setIsorderplaced(boolean isorderplaced) {
        this.isorderplaced = isorderplaced;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
