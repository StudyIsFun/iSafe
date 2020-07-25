package com.crime_mapping.electrothon.sos;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double latitude, longitude;
    String name, time;

    String l1, l2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            latitude = b.getDouble("lat");
            longitude = b.getDouble("long");
            name = b.getString("name");
            time = b.getString("time");
        }

        Intent intent = getIntent();
        l1 = intent.getStringExtra("Latitude");
        l2 = intent.getStringExtra("Longitude");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double lat, lon;
        lat = Double.valueOf(l1);
        lon = Double.valueOf(l2);
        LatLng loc = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions().position(loc).title(name + " at " + time));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f), 4000, null);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
