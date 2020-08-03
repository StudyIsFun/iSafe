package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MpasActivity2";
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
                .baseUrl("https://sihapi--psproject.repl.co/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        SihApi sihApi = retrofit.create(SihApi.class);
        Call<RouteResponse> call = sihApi.getUnsafeAreas(a1, a2);
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful()) {
                    Data[] data = response.body().getData();
                    Log.e(TAG, "" + data);
                    for (Data data1 : data) {
                        LatLng ltlng = new LatLng(data1.getX(), data1.getY());
                        mMap.addMarker(new MarkerOptions().position(ltlng).title(data1.getDescription()));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 16);
                        mMap.animateCamera(cameraUpdate);

                    }

                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {

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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
        // Add a marker in Sydney and move the camera

        FetchCoordinates();
        mMap.setMyLocationEnabled(true);
        LatLng points = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(points).title("You"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points, 16);
        mMap.animateCamera(cameraUpdate);
    }
}
