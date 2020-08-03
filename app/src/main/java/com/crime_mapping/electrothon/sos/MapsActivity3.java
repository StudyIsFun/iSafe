package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.crime_mapping.electrothon.sos.directionhelpers.FetchURL;
import com.crime_mapping.electrothon.sos.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    SearchView start, end;
    LatLng latLngStart;
    LatLng latLngEnd;
    private final static int MY_PERMISSIONS_REQUEST = 32;
    double lat1, lon1, lat2, lon2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        start = findViewById(R.id.searchStart);
        end = findViewById(R.id.searchDestination);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
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


//    getDirection.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//        }
//    });

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);


        start.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = start.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity3.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    lat1 = address.getLatitude();
                    lon1 = address.getLongitude();
                    latLngStart = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngStart).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 14));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        end.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = end.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity3.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    lat2 = address.getLatitude();
                    lon2 = address.getLongitude();
                    latLngEnd = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngEnd).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngEnd, 14));
                    new TaskDirectionRequest().execute(buildRequestUrl(latLngStart, latLngEnd));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        /*
        end.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = start.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity3.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    latLngEnd = new LatLng(address.getLatitude(), address.getLongitude());
                    Log.e("endpoint", "" + latLngEnd);
                    mMap.addMarker(new MarkerOptions().position(latLngEnd).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngEnd, 14));

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

         */

    }

    @Override
    public void onBackPressed() {
        if (currentPolyline != null)
            currentPolyline.remove();
        super.onBackPressed();
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
        //
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


    }

    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + APIKEY;
        Log.d("TAG", url);
        return url;
    }

    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }

    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
                ApiCall();
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ApiCall() {
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://sihapi--psproject.repl.co/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        SihApi sihApi = retrofit.create(SihApi.class);
        Call<RouteResponse> call = sihApi.getRoutes(String.valueOf(lat1), String.valueOf(lon1), String.valueOf(lat2), String.valueOf(lon2));
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful()) {
                    Data[] data = response.body().getData();

                    Log.e(TAG, "" + data.length);

                    for (Data data1 : data) {

                        LatLng point = new LatLng(data1.getX(), data1.getY());
                        Log.e("mapsactivity3", "" + data1.getX() + "" + data1.getY());
                        Circle circle = mMap.addCircle(new CircleOptions()
                                .center(point).radius(20)
                                .strokeColor(Color.BLUE)
                                .strokeWidth(2f)
                                .fillColor(Color.argb(50, 255, 0, 0)));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 16);
                        mMap.animateCamera(cameraUpdate);

                    }
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());
            }
        });
    }

    /**
     * Request app permission for API 23/ Android 6.0
     *
     * @param permission
     */
    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST);
        }
    }

}
