package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class GpsTracker extends Service implements LocationListener {

    public static final String LOG_TAG = GpsTracker.class.getSimpleName();
    public static final int MIN_UPDATE_DIST = 10;
    public static final int UPDATE_TIME_INTERVAL = 1000 * 60;

    private Context mContext;
    private Location mLocation;
    private LocationManager mLocationManager;

    // Bools
    private boolean canGetLocation = false;
//    private boolean isGpsEnabled = false;
//    private boolean networkConnectivity = false;

    double latitude;
    double longitude;


    public GpsTracker(Context context) {
        this.mContext = context;
        checkAndGetLocation();
    }

    public GpsTracker() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    public void checkAndGetLocation() {
        try {

            //here is the change
//            boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            boolean networkConnectivity = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkConnectivity = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGpsEnabled && !networkConnectivity) {
            } else {
                this.canGetLocation = true;
                if (networkConnectivity) {
                    if (!checkPermission())
                        return;

                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME_INTERVAL, MIN_UPDATE_DIST, this);
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            latitude = mLocation.getLatitude();
                            longitude = mLocation.getLongitude();
                        }
                    }
                }
                if (isGpsEnabled) {
                    if (mLocation == null) {

                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME_INTERVAL, MIN_UPDATE_DIST, this);
                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                latitude = mLocation.getLatitude();
                                longitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void stopGps(){
        if(checkPermission()){
            mLocationManager.removeUpdates(GpsTracker.this);
        }
    }
    public double getLatitude(){
        if (mLocation!=null)
            latitude = mLocation.getLatitude();
        return latitude;
    }
    public double getLongitude(){
        if (mLocation!=null){
            longitude = mLocation.getLongitude();
        }
        return longitude;
    }

//******************************************GPS Loaction***************************************************************************
public Location getLocation(){
    if (ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
        return null;
    }
    try {
        LocationManager lm = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,10,this);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return loc;
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    return null;
}
//******************************************GPS Location**************************************************************************-

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public boolean checkPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }

    public void showAlertDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("hey");
        dialog.setMessage("hey1");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();
    }
}
