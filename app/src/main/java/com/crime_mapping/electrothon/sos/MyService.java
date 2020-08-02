package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //    static {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//    }
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat, lon;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference locationRef = mRootRef.child("location").child(uId);
    DatabaseReference myRef, myUserRef;

    ChildEventListener childEventListener;
    NotificationManager nm;
    Notification n;
    private static final String TAG = "Service Activity";
    Notification.Builder nb;
    String CHANNEL_ID = "my_sos_channel";
    int notification_request_code = 200;
    String notify_button;
    ImageButton cam;
    ImageButton mic;
    ImageButton sos11;
    ImageButton location;
    ImageButton area;
    //ImageButton showNotification;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference;
    NotificationManager notificationManager;

    public MyService() {
    }

    @Override
    public void onCreate() {
        LayoutInflater inflate =(LayoutInflater) getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        RelativeLayout layoutleft= (RelativeLayout) inflate.inflate(
                R.layout.notification_design,null
        );
        cam=layoutleft.findViewById(R.id.cam);
        mic=layoutleft.findViewById(R.id.mic);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence name = "SOS ALERT";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name,
                    importance);
            nm.createNotificationChannel(channel);
        }

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify_button = "action1";
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify_button = "action2";
            }
        });
        sos11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify_button = "action3";
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify_button = "action4";
            }
        });
        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify_button = "action5";
            }
        });

        Intent intentAction = new Intent(this,ActionReciever.class);

        intentAction.putExtra("action","actionName");
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_design);
        // Intent clickIntent = new Intent(this, NotificationReceiver.class);
        // PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
        //         0, clickIntent, 0);
        //collapsedView.setTextViewText(R.id.text_view_collapsed_1, "Hello World!");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.isafe)
                .setCustomContentView(collapsedView)
                .setOngoing(true)
                .build();

        notificationManager.notify(1, notification);

        myRef = FirebaseDatabase.getInstance().getReference("Locations");
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: from service");
                final FirebaseLocationData fld = dataSnapshot.getValue(FirebaseLocationData.class);

                myUserRef.child(fld.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sos_name = dataSnapshot.getValue(String.class);

                        nb = new Notification.Builder(MyService.this);
                        nb.setContentTitle("Emergency");
                        nb.setContentText("SOS broadcasted from " + sos_name);
                        nb.setSmallIcon(android.R.drawable.ic_dialog_alert);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            nb.setChannelId(CHANNEL_ID);
                        }
                        nb.setDefaults(Notification.DEFAULT_ALL);
                        Intent i = new Intent(MyService.this, MapsActivity.class);
                        Bundle b = new Bundle();
                        b.putDouble("lat", fld.getLatitude());
                        b.putDouble("long", fld.getLongitude());
                        b.putString("name", sos_name);
                        b.putString("time", fld.getSos_time());
                        i.putExtras(b);
                        ;
                        nb.setAutoCancel(false);
                        PendingIntent pi = PendingIntent.getActivity(MyService.this, notification_request_code, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        nb.setContentIntent(pi);
                        n = nb.build();
                        nm.notify(notification_request_code, n);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyService.this, "SOS APP service: database fetch cancelled", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        super.onCreate();

        preferences = getSharedPreferences("App", MODE_PRIVATE);
        editor = preferences.edit();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        buildGoogleApiClient();
        mGoogleApiClient.connect();


        if (childEventListener != null)
            myRef.addChildEventListener(childEventListener);
//        Toast.makeText(this, "SOS background service started", Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (childEventListener != null)
            myRef.removeEventListener(childEventListener);
        mGoogleApiClient.disconnect();
//        Toast.makeText(this, "SOS backgroung service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        updateUI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10); // Update location every second

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MyService.this);


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            Log.d("response", "" + lat + lon);

            if (preferences.getBoolean("AllowLocSharing", false)) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Location_Shared").child(preferences.getString("PrimeContact", ""));
                Map<String,Double> details = new HashMap<>();
                details.put("Latitude", lat);
                details.put("Longitude", lon);
                databaseReference.setValue(details);
            }
        }
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    void updateUI() {

        locationRef.child("lat").setValue(String.valueOf(lat));
        locationRef.child("lon").setValue(String.valueOf(lon));
//        locationRef.child(uId).setValue(new LatLng(Float.valueOf(lat), Float.valueOf(lon)));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
