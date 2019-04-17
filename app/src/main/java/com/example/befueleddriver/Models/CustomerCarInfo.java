package com.example.befueleddriver.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomerCarInfo implements Parcelable {
    private String carmodel;
    private String carmake;
    private String caryear;
    private String carlicenseplate;
    private String caraddnote;
    private String carcolor;
    private String userid;
    private String geokey;
    private boolean isorderplaced;

    public CustomerCarInfo(String carmodel, String carmake, String caryear, String carlicenseplate, String caraddnote, String carcolor, String userid, String geokey, boolean isorderplaced) {
        this.carmodel = carmodel;
        this.carmake = carmake;
        this.caryear = caryear;
        this.carlicenseplate = carlicenseplate;
        this.caraddnote = caraddnote;
        this.carcolor = carcolor;
        this.userid = userid;
        this.geokey = geokey;
        this.isorderplaced = isorderplaced;
    }

    public CustomerCarInfo() {
    }

    protected CustomerCarInfo(Parcel in) {
        carmodel = in.readString();
        carmake = in.readString();
        caryear = in.readString();
        carlicenseplate = in.readString();
        caraddnote = in.readString();
        carcolor = in.readString();
        userid = in.readString();
        geokey = in.readString();
        isorderplaced = in.readByte() != 0;
    }

    public static final Creator<CustomerCarInfo> CREATOR = new Creator<CustomerCarInfo>() {
        @Override
        public CustomerCarInfo createFromParcel(Parcel in) {
            return new CustomerCarInfo(in);
        }

        @Override
        public CustomerCarInfo[] newArray(int size) {
            return new CustomerCarInfo[size];
        }
    };

    public String getGeokey() {
        return geokey;
    }

    public void setGeokey(String geokey) {
        this.geokey = geokey;
    }

    public boolean isIsorderplaced() {
        return isorderplaced;
    }

    public void setIsorderplaced(boolean isorderplaced) {
        this.isorderplaced = isorderplaced;
    }


    public String getCarmodel() {
        return carmodel;
    }

    public void setCarmodel(String carmodel) {
        this.carmodel = carmodel;
    }

    public String getCarmake() {
        return carmake;
    }

    public void setCarmake(String carmake) {
        this.carmake = carmake;
    }

    public String getCaryear() {
        return caryear;
    }

    public void setCaryear(String caryear) {
        this.caryear = caryear;
    }

    public String getCarlicenseplate() {
        return carlicenseplate;
    }

    public void setCarlicenseplate(String carlicenseplate) {
        this.carlicenseplate = carlicenseplate;
    }

    public String getCaraddnote() {
        return caraddnote;
    }

    public void setCaraddnote(String caraddnote) {
        this.caraddnote = caraddnote;
    }

    public String getCarcolor() {
        return carcolor;
    }

    public void setCarcolor(String carcolor) {
        this.carcolor = carcolor;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carmodel);
        dest.writeString(carmake);
        dest.writeString(caryear);
        dest.writeString(carlicenseplate);
        dest.writeString(caraddnote);
        dest.writeString(carcolor);
        dest.writeString(userid);
        dest.writeString(geokey);
        dest.writeByte((byte) (isorderplaced ? 1 : 0));
    }
}

