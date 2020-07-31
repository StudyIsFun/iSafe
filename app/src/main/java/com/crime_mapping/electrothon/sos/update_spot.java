package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
import android.view.MotionEvent;
=======
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

<<<<<<< HEAD
import com.crime_mapping.electrothon.sos.api.ApiClient;
import com.crime_mapping.electrothon.sos.api.ApiInterface;
import com.crime_mapping.electrothon.sos.api.Crime;
=======
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
<<<<<<< HEAD
import com.google.firebase.database.DatabaseReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
=======
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81

public class update_spot extends FragmentActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback{
    private EditText e1, e2,e3;
    private Button b1;
    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    int no_user=0;
    String lat;
    DatabaseReference user_no;
    String provider;
    String latti,longgi;
<<<<<<< HEAD
=======
    //    double latti=0,longi=0;
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_spot);
        e1 = (EditText) findViewById(R.id.id);
        e2 = (EditText) findViewById(R.id.pswd);
        b1 = (Button) findViewById(R.id.submit);
        e3 = (EditText)findViewById(R.id.detail);

        Intent intentt = getIntent();
        latti = intentt.getStringExtra("latti");
        longgi = intentt.getStringExtra("longgi");


        SupportMapFragment mapfrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapfrag.getMapAsync(update_spot.this);
    }

<<<<<<< HEAD
=======

>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
    public void submit(View view) {
        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();
        if(s1.equals("risi") && s2.equals("risi"))
        {
<<<<<<< HEAD
            push();
=======


            user_no = FirebaseDatabase.getInstance().getReference().child("spots");
            user_no.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            no_user++;
                        }
                    }
                    Toast.makeText(update_spot.this ,String.valueOf(no_user) ,Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });

            if(latti!=null && longgi != null)
            {
//                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("spots").child(String.valueOf(no_user/3+1));
                firebaseDatabase.child("lat").setValue(latti);
                firebaseDatabase.child("lon").setValue(longgi);
                firebaseDatabase.child("details").setValue(e3.getText().toString());
                Toast.makeText(update_spot.this,latti+" "+longgi, Toast.LENGTH_LONG).show();

            }
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
        }
    }



    @Override
    public void onLocationChanged(Location location) {


    }



    public void custom_submit(View view) {
<<<<<<< HEAD
//        Toast.makeText(this,"Latti : "+latti+" Longi : "+longgi,Toast.LENGTH_LONG).show();
        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();
        if(s1.equals("risi") && s2.equals("risi"))
        {
            push();
        }
=======
        Toast.makeText(this,"Latti : "+latti+" Longi : "+longgi,Toast.LENGTH_LONG).show();
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera

        LatLng latLng = new LatLng(31.0543, 73.29423);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                latti = String.valueOf(point.latitude);
                longgi = String.valueOf(point.longitude);
                mMap.addMarker(new MarkerOptions().position(point).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
<<<<<<< HEAD

    void push()
    {
        if(latti!=null && longgi != null) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<Crime> calllatest;
            calllatest = apiInterface.pushcrime(String.valueOf(latti), String.valueOf(longgi), e3.getText().toString());
            calllatest.enqueue(new Callback<Crime>() {
                @Override
                public void onResponse(Call<Crime> call, Response<Crime> response) {
                    Log.e("call", call.request().url().toString());
                    Log.e("responnse", String.valueOf(response));

                }

                @Override
                public void onFailure(Call<Crime> call, Throwable t) {

                }
            });
        }
    }
=======
>>>>>>> 014d818825fd94f700009be5c4b7fbb651b44e81
}
