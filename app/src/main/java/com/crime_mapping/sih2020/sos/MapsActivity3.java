package com.crime_mapping.sih2020.sos;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.crime_mapping.sih2020.sos.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity3 extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final String TAG = "MapsActivity3";
    private GoogleMap mMap;
    Circle circle;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Button getDirection;
    LatLng xx, yy;
    private Polyline currentPolyline;
    private MarkerOptions place1, place2;
    ArrayList<LatLng> listpoints;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Retrofit retrofit;
    List<LatLng> Pointslist = null;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        Pointslist = new ArrayList<>();
        preferences = getSharedPreferences("App", MODE_PRIVATE);
        editor = preferences.edit();

        if (ContextCompat.checkSelfPermission(MapsActivity3.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity3.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity3.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MapsActivity3.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
            listpoints = new ArrayList<LatLng>();
            getDirection = findViewById(R.id.btnGetDirection);
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

//    getDirection.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//        }
//    });

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MapsActivity3.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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
        Toast.makeText(this, "map ready", LENGTH_LONG).show();
        // Add a marker in Sydney and move the camera
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (listpoints.size() == 2) {
                    listpoints.clear();
                    mMap.clear();
                }
                listpoints.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listpoints.size() == 1) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    place1 = new MarkerOptions().position(latLng).title("Location 1");
                    xx = latLng;
                    Toast.makeText(MapsActivity3.this, String.valueOf(listpoints.get(0)), LENGTH_LONG).show();

                } else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    place2 = new MarkerOptions().position(latLng).title("Location 2");
                    yy = latLng;
                    Toast.makeText(MapsActivity3.this, String.valueOf(listpoints.get(1)), LENGTH_LONG).show();
                }
                mMap.addMarker(markerOptions);
            }


        });

//        mMap.addCircle(new CircleOptions().radius(20).strokeColor(Color.argb(30,200,0,0)).strokeWidth(2f).fillColor(0x30ff0000));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destinations=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=AIzaSyCWEKwOS-WbdQ3QpspGBFGObIl0WI3x9yQ";
//        String url = "//https://api.distancematrix.ai/maps/api/distancematrix/json?origins=31.706746,%2076.528922&destinations=31.701881,%2076.522603&key=Eewaeb9aeRahex7fie3geenguxaep";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;


//        https://api.distancematrix.ai/maps/api/distancematrix/json?origins=31.706746,%2076.528922&destinations=31.701881,%2076.522603&key=Eewaeb9aeRahex7fie3geenguxaep
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    public void hoo(View view) {

        /*
        String lat1 = String.valueOf(xx.latitude);
        String lon1 = String.valueOf(xx.longitude);
        String lat2 = String.valueOf(yy.latitude);
        String lon2 = String.valueOf(yy.longitude);
        editor.putString("lat1", lat1);
        editor.putString("lat2", lat2);
        editor.putString("lon1", lon1);
        editor.putString("lon2", lon2);
        editor.commit();
        FirebaseDatabase.getInstance().getReference().setValue(lat1);
        FirebaseDatabase.getInstance().getReference().child("area").child("2").child("lon").setValue(lon1);
        //        Toast.makeText(this,xx.latitude+" "+xx.longitude,Toast.LENGTH_SHORT).show();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&mode=driving&key=AIzaSyCWEKwOS-WbdQ3QpspGBFGObIl0WI3x9yQ";
        new FetchURL(MapsActivity3.this).execute((url), "driving");

         */


        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://sihapi--psproject.repl.co/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        SihApi sihApi = retrofit.create(SihApi.class);
        Call<RouteResponse> call = sihApi.getRoutes("31.708508", "76.527356", "31.689422", "76.519561");
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful()) {
                    Data[] data = response.body().getData();
                    Log.e(TAG, "" + data);
                    for (Data data1 : data) {
                        Pointslist.add(new LatLng(data1.getX(), data1.getY()));
                        LatLng point = new LatLng(data1.getX(), data1.getY());
//                        Circle circle = mMap.addCircle(new CircleOptions()
//                                .center(point).radius(20)
//                                .strokeColor(Color.BLUE)
//                                .strokeWidth(2f)
//                                .fillColor(Color.argb(50, 255, 0, 0)));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 16);
                        mMap.animateCamera(cameraUpdate);

                    }
                    addHeatMap();
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());
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

}
