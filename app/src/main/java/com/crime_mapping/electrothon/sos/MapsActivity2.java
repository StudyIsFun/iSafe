package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String a1, a2;
    double lat, lon;
    //lat,log,title
    String a, b, t;
    Retrofit retrofit;

    List<LatLng> Pointslist = null;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        Pointslist = new ArrayList<>();
        Intent intentt = getIntent();
        a1 = intentt.getStringExtra("lat");
        a2 = intentt.getStringExtra("lon");
        lat = Double.valueOf(a1);
        lon = Double.valueOf(a2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void FetchCoordinates() {
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://demo9608891.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        SihApi sihApi = retrofit.create(SihApi.class);
        Call<List<LatLong>> call = sihApi.getCoordinates();
        call.enqueue(new Callback<List<LatLong>>() {
            @Override
            public void onResponse(Call<List<LatLong>> call, Response<List<LatLong>> response) {
                Log.e("response", "" + response.code());
                if (response.isSuccessful()) {
                    List<LatLong> list = response.body();
                    for (LatLong latLong : list) {
                        Pointslist.add(new LatLng(latLong.getLat(), latLong.getLng()));
                        Log.e("list", "" + latLong.getLat());
                    }
                    addHeatMap();
                }
            }

            @Override
            public void onFailure(Call<List<LatLong>> call, Throwable t) {
                Log.e("failed", "" + t.getMessage());
            }
        });

    }

    private void addHeatMap() {
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };
        float[] startPoints = {
                0.2f, 1f
        };
        Gradient gradient = new Gradient(colors, startPoints);
        mProvider = new HeatmapTileProvider.Builder()
                .data(Pointslist)
                .gradient(gradient)
                .radius(20)
                .build();

        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));


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
        /*
        DatabaseReference d1 = FirebaseDatabase.getInstance().getReference().child("spots");
        d1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    a = dsp.child("lat").getValue().toString();
                    b = dsp.child("lon").getValue().toString();
                    t = dsp.child("details").getValue().toString();
                    LatLng points = new LatLng(Double.valueOf(a), Double.valueOf(b));
                    mMap.addMarker(new MarkerOptions().position(points).title(t));
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(points)
                            .radius(35)
                            .strokeColor(Color.BLUE)
                            .strokeWidth(2f)
                            .fillColor(Color.argb(50, 255, 0, 0)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */

        // Add a marker in Sydney and move the camera

        FetchCoordinates();
        mMap.setMyLocationEnabled(true);
        LatLng points = new LatLng(31.708535, 76.527377);
        mMap.addMarker(new MarkerOptions().position(points).title("NIT Hamirpur"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points, 16);
        mMap.animateCamera(cameraUpdate);
    }
}
