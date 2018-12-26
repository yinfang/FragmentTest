package com.clubank.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by long on 18-3-6.
 */

public class LocationUtil {

    private static LocationUtil instance;
//    private AMapLocationClient aMapLocationClient = null;
    private Context context;

    private LocationUtil() {
    }

    private LocationUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public static LocationUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (LocationUtil.class) {
                if (instance == null) {
                    instance = new LocationUtil(context);
                }
            }
        }
        return instance;
    }


//    public void startLocation(AMapLocationListener listener) {
//        aMapLocationClient = new AMapLocationClient(context);
//        aMapLocationClient.setLocationListener(listener);
//        aMapLocationClient.setLocationOption(getLocationOption());
//        aMapLocationClient.startLocation();
//    }

    public void stopLocation() {
//        if (aMapLocationClient != null) {
//            aMapLocationClient.stopLocation();
//        }
    }


//    private AMapLocationClientOption getLocationOption() {
//        AMapLocationClientOption option = new AMapLocationClientOption();
//        option.setLocationMode(AMapLocationClientOption.AMapLocationMode
//                .Hight_Accuracy);
//        option.setOnceLocation(true);
//        option.setOnceLocationLatest(false);
//        option.setHttpTimeOut(200000);
//        option.setLocationCacheEnable(false);
//        return option;
//    }


    public static boolean isLocationServiceEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {

            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean passive = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            if (gps || network) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


}
