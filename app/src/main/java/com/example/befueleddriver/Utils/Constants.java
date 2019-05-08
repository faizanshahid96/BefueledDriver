package com.example.befueleddriver.Utils;

import android.location.Location;



import java.util.Locale;

public class Constants {
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final String baseURL =  "https://maps.googleapis.com";
    public static final String fcmURL =  "https://fcm.googleapis.com/";

//    public static IGoogleAPI getGoogleAPI(){
//        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
//    }
    public static Location mLastLocation = null;


}
