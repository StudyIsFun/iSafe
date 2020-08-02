package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, View.OnClickListener, SensorEventListener, EasyPermissions.PermissionCallbacks {
    private GoogleMap mMap;
    private String prime_id = null;
    private String phone_no;
    //    DataSnapshot dataSnapshot;
//    DatabaseReference firebaseDatabase;
    private String mMainTargetid;
    private SharedPreferences preferences;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String ANONYMOUS = "anonymous";
    GoogleApiClient gac;
    String prime = "";
    LocationRequest locationRequest;
    private static final int UPDATE_INTERVAL = 15 * 1000;
    private static final int FASTEST_UPDATE_INTERVAL = 2 * 1000;
    private static final int notification_request_code = 100;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location lastLocation, lastLocationGpsProvider;
    private DatabaseReference mRootReference;

    private static final String BACKGROUND_SERVICE_STATUS = "bgServiceStatus";
    SharedPreferences sharedpreferences;
    private String MyPREFERENCES = "SOS_DATA";
    private boolean isServiceBackground;

    TextView tv;
    Button b;

    private int cnt = 5000;
    //Firebase Variables
//    static {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//    }
    private Switch togglebutton1, togglebutton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseuser;
    FirebaseDatabase database;
    DatabaseReference myRef, myUserRef;
    private String mUsername;
    private String mPhotoUrl;
    private ChildEventListener childEventListener;
    private String mEmail;
    private String mUid;
    public String a1, a2;
    Double l1 = 0.00, l2 = 0.00, ll1, ll2;
    //Notification
    NotificationManager nm;
    String CHANNEL_ID = "my_sos_channel";// The id of the channel.
    Notification n;
    Notification.Builder nb;
    private int RECORD_AUDIO = 1;
    private int CALL_PHONE = 1;
    SharedPreferences preferencess;
    SharedPreferences.Editor editor;
    private Button mMainCallbtn;
    private static final String TAG = "MainActivity";
    private Boolean ToggleButtonState;


    //camera variable
    private String no;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Camera mCamera;
    String primeContact;
    //to store path of each image
    String file_path;
    private CameraPreview mCameraPreview;
    private StorageReference mStorageRef;
    Uri file;
    private StorageTask mUploadTask;
    public static final int PERMISSION_CODE = 123;

    private DatabaseReference mDatabaseRef;


    //mic variable
    private String number;
    private TextView mCallingStatus;
    private TextView mCallingName;
    private LinearLayout mCallingNotify;
    private Button mCallingAnswer;
    private Button mCallingReject;
    private LinearLayout mCallingActionButton;
    private Call call;
    private Ringtone r;
    private boolean isIncomming;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private View mCallingBlacksreen;
    boolean flag = false;


    @Override
    protected void onStart() {
        gac.connect();
        if (childEventListener != null) {
            myRef.addChildEventListener(childEventListener);
            Log.d(TAG, "onStart: ChildEventListener Attached");
        }
        EventBus.getDefault().register(this);
        stopService(new Intent(this, MyService.class));
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
//        if (childEventListener != null) {
//            myRef.removeEventListener(childEventListener);
//            Log.d(TAG, "onStop: ChildEventListener Removed");
//        }

        if (isServiceBackground && FirebaseAuth.getInstance().getCurrentUser() != null) {
            startService(new Intent(this, MyService.class));
            Log.d(TAG, "onStop: starting service");
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        b = findViewById(R.id.btnService);
        startService(new Intent(this, SinchService.class));
        togglebutton = (Switch) findViewById(R.id.switch1);
        togglebutton1 = (Switch) findViewById(R.id.switch2);

        preferences = getSharedPreferences("App", Context.MODE_PRIVATE);
        editor = preferences.edit();
        no = preferences.getString("PHN", "");
        primeContact = preferences.getString("PrimeContact", "");


        //for mic section
        preferencess = getSharedPreferences("App", Context.MODE_PRIVATE);
        number = preferencess.getString("PHN", "");
      //  initView();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapp);
        mapFragment.getMapAsync(this);

        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "You have already granted this permission", Toast.LENGTH_SHORT).show();

        } else {

            requestRecordAudioPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

//            Toast.makeText(this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
        } else {
            requestCallPhonePermission();
        }

         */


        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence name = "SOS ALERT";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name,
                    importance);
            nm.createNotificationChannel(channel);
        }


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isServiceBackground = sharedpreferences.getBoolean(BACKGROUND_SERVICE_STATUS, true);
        if (isServiceBackground)
            b.setText("Stop background Notification");
        else
            b.setText("Start background Notification");




        /* firebase initialization */
        mUsername = ANONYMOUS;
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseuser = mFirebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Locations");
        myUserRef = database.getReference("Users");
        myRef.keepSynced(true);

        if (mFirebaseuser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

//                FirebaseLocationData fld = dataSnapshot.getValue(FirebaseLocationData.class);
//                Toast.makeText(MainActivity.this, fld.getEmail()+" added", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());


                final FirebaseLocationData fld = dataSnapshot.getValue(FirebaseLocationData.class);
                Log.d(TAG, "onChildChanged: Creating Notification");
                myUserRef.child(fld.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sos_name = dataSnapshot.getValue(String.class);
                        nb = new Notification.Builder(MainActivity.this);
                        nb.setContentTitle("Emergency");
                        nb.setContentText("SOS broadcasted from " + sos_name);
                        nb.setSmallIcon(android.R.drawable.ic_dialog_alert);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            nb.setChannelId(CHANNEL_ID);
                        }
                        nb.setDefaults(Notification.DEFAULT_ALL);
                        Intent i = new Intent(MainActivity.this, MapsActivity.class);
                        Bundle b = new Bundle();
                        b.putDouble("lat", fld.getLatitude());
                        b.putDouble("long", fld.getLongitude());
                        b.putString("name", sos_name);
                        b.putString("time", fld.getSos_time());
                        i.putExtras(b);
                        ;
                        nb.setAutoCancel(false);
                        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, notification_request_code, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        nb.setContentIntent(pi);
                        n = nb.build();
                        nm.notify(notification_request_code, n);
                        Log.d(TAG, "onDataChange: NOTIFICATION CREATED");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                Toast.makeText(MainActivity.this,fld.getEmail()+" changed", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

//                String locKey = dataSnapshot.getKey();


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                FirebaseLocationData fld = dataSnapshot.getValue(FirebaseLocationData.class);
                String locKey = dataSnapshot.getKey();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, ":onCancelled", databaseError.toException());
            }
        };
        if (childEventListener != null)
            Log.d(TAG, "onCreate: childEventListenerCreated");


        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);


        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        gac = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //for camera things
        /*
        if (checkPermission()) {
//            setContentView(R.layout.activity_main);
            mCamera = getCameraInstance();
            mCameraPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);

//            Button captureButton = (Button) findViewById(R.id.button_capture);
//            captureButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Timer timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mCamera.startPreview();
//                            mCamera.takePicture(null, null, mPicture);
//                        }
//                    }, 0, 5000);
//                }
//            });
        } else {
            requestPermission();
        }

         */

        RequestPermission();
    }

    @AfterPermissionGranted(PERMISSION_CODE)
    private void RequestPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //  Toast.makeText(this, "Opening camera", Toast.LENGTH_SHORT).show();
            CameraPrev();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions because this and that",
                    PERMISSION_CODE, perms);
        }
    }

    private void CameraPrev() {
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
    }


    //camera section begins here
    private void initView() {
        mCallingStatus = findViewById(R.id.calling_status);
        mCallingName = findViewById(R.id.calling_name);
        mCallingNotify = findViewById(R.id.calling_notify);
        mCallingAnswer = findViewById(R.id.calling_answer);
        mCallingAnswer.setOnClickListener(this);
        mCallingReject = findViewById(R.id.calling_reject);
        mCallingReject.setOnClickListener(this);
        mCallingActionButton = findViewById(R.id.calling_action_button);
        mCallingBlacksreen = findViewById(R.id.calling_blackscreen);
    }


    //camera section ends here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.firebase_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                return true;
            case R.id.sign_out:
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.view_location:
                startActivity(new Intent(MainActivity.this, LocationListActivity.class));
                return true;
            case R.id.info:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startSos(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
//        mFusedLocationProviderClient.getLastLocation()
//                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            lastLocation = task.getResult();
//
////                            txtLatitude.setText(String.valueOf(lastLocation.getLatitude()));
////                            txtLongitude.setText(String.valueOf(lastLocation.getLongitude()));
//
//                        } else {
//                            Log.w(TAG, "getLastLocation:exception", task.getException());
////                            showSnackbar(getString(R.string.no_location_detected));
//                        }
//                    }
//                });

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lastLocationGpsProvider = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        //write data to firebase

        if (lastLocationGpsProvider != null)
            updateUserLocationToFirebase(lastLocationGpsProvider);
        else {
            lastLocationGpsProvider = LocationServices.FusedLocationApi.getLastLocation(gac);
            if (lastLocationGpsProvider != null)
                updateUserLocationToFirebase(lastLocationGpsProvider);
        }


//        Toast.makeText(this, "GPS location without google client\n"+lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        } else {
//            Toast.makeText(this, "Permission is given", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Permission is already given");
        }

        // TODO: use fusedlocaionproviderclient
        /* mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.

                        if (location != null) {
                            // Logic to handle location object
                            lastLocation=location;
                        }
                    }
                });
        */
        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);


        Log.d(TAG, "LastLocation from Deprecated: " + (ll == null ? "NO LastLocation" : ll.toString()));
//        tv.setText("LastLocation from Deprecated: " + (ll == null ? "NO LastLocation" : ll.toString()));
//        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : lastLocation.toString()));
        updateUI(ll);

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d(TAG, connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            updateUI(location);
        }

    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        a1 = Double.toString(loc.getLatitude());
        l1 = loc.getLatitude();
        l2 = loc.getLongitude();
        a2 = Double.toString(loc.getLongitude());
        tv.setText(Double.toString(loc.getLatitude()) + '\n' + Double.toString(loc.getLongitude()) + '\n' + DateFormat.getTimeInstance().format(loc.getTime()));
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private void updateUserLocationToFirebase(Location location) {
        DatabaseReference d1 = FirebaseDatabase.getInstance().getReference().child("spots");
        d1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String a = dsp.child("lat").getValue().toString();

                    String b = dsp.child("lon").getValue().toString();
                    String t = dsp.child("details").getValue().toString();
//                    LatLng points = new LatLng(Double.valueOf(a),Double.valueOf(b));
//                    ll1 = Double.valueOf(a);
//                    ll2 = Double.valueOf(b);
//                    if(abs((ll1-l1))<=0.0002 && abs(ll2-l2)<0.0002)
//                    {p
                    Toast.makeText(MainActivity.this, t, Toast.LENGTH_SHORT).show();
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseLocationData fld = new FirebaseLocationData(mEmail, location.getLatitude(), location.getLongitude(), DateFormat.getTimeInstance().format(location.getTime()), DateFormat.getTimeInstance().format(new Date()));
        fld.setUid(mUid);
        myUserRef.child(mUid).child("name").setValue(mUsername);
        myRef.child(mUid).setValue(fld);
    }


    public void changeServiceState(View view) { //

        isServiceBackground = !isServiceBackground;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(BACKGROUND_SERVICE_STATUS, isServiceBackground);
        editor.apply();
//        tv.setText(String.valueOf(isServiceBackground));
        if (isServiceBackground) {
            b.setText("Stop background Notification");

            Toast.makeText(this, "Background Notification Started\nYou will get SOS notification even if app is closed", Toast.LENGTH_SHORT).show();

        } else {
            b.setText("Start background Notification");

            Toast.makeText(this, "Background Notification Stopped\nYou won't get notification if app is closed\nPlease Turn it back on", Toast.LENGTH_SHORT).show();
        }


    }

    public void Update_loc(View view) {
        //write code for police sections
        Toast.makeText(this, a1 + " " + a2, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, update_spot.class);
        Bundle extras = new Bundle();
        extras.putString("latti", a1);
        extras.putString("longgi", a2);
        intent.putExtras(extras);
        startActivity(intent);

    }

    public void unsafe_nearby(View view) {
        Intent intent = new Intent(this, MapsActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("lat", a1);
        bundle.putString("lon", a2);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void routes(View view) {
        Intent intent = new Intent(this, MapsActivity3.class);
        startActivity(intent);
    }

    public void share_loc(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Locations").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // prime = dataSnapshot.child("prime").getValue().toString();
                Toast.makeText(MainActivity.this, "service started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Toast.makeText(this,"services started...",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    public void enter_prime(View view) {
        Intent intent = new Intent(this, prime_contacts.class);
        startActivity(intent);
    }

    public void location_sharing(View view) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Location_Shared");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                if (dataSnapshot.hasChild(no)) {
                } else {
                    Toast.makeText(MainActivity.this, "No user have shared location with you.", Toast.LENGTH_SHORT).show();
                }
                 */
                if (dataSnapshot.hasChild(no)) {
                    flag = true;
                }
              /*
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", "" + snapshot.getKey());
                }

               */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (flag) {
            Intent intent = new Intent(MainActivity.this, AllContacts.class);
            startActivity(intent);
        }
    }


//    public void share_cam(View view) {
//        startActivity(new Intent(MainActivity.this, MyCamera.class));
//    }

    //    public void share_mic(View view) {
//
//
//
//        preferencess = getSharedPreferences("App", Context.MODE_PRIVATE);
//        String number = preferencess.getString("PHN","");
//        mRootReference = FirebaseDatabase.getInstance("https://crime1.firebaseio.com/").getReference();
//        DatabaseReference mContactReference = mRootReference.child("user").child(number).child("prime");
//
//
//        mContactReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                prime_id = snapshot.getValue().toString();
//
//                //if prime_id is received,searching in contact table
//                DatabaseReference mContactReference1 = mRootReference.child("Contact").child(prime_id);
//                //            mContactReference = mRootReference.child("Contact").child(prime_id);
//                try {
//                    mContactReference1.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            mMainTargetid = dataSnapshot.getValue().toString();
////                                Toast.makeText(Sinch_MainActivity.this,"second " +mMainTargetid,Toast.LENGTH_LONG).show();
//                                Log.d("id_found", String.valueOf(mMainTargetid));
//                            try {
//                                Call currentcall = Sinch_Apps.callClient.callUser(mMainTargetid);
//
//                                Intent callscreen = new Intent(MainActivity.this, IncommingCallActivity.class);
//                                callscreen.putExtra("callid", currentcall.getCallId());
//                                callscreen.putExtra("incomming", false);
//                                callscreen.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(callscreen);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
////        startActivity(new Intent(MainActivity.this, Sinch_MainActivity.class));
//    }
    public void requestRecordAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("This permission is needed to record audio for phone call")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
    }

    public void requestCallPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("This permission is needed to make a phone call")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
    }

    // Run time permission stuff ended

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == RECORD_AUDIO) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "RECORD AUDIO Permission GRANTED", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "RECORD AUDIO Permission DENIED", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == CALL_PHONE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "CALL PHONE Permission GRANTED", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "CALL PHONE Permission DENIED", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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

        LatLng latLng = new LatLng(l1, l2);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);
    }

    public void onToggleClicked(View view) {
        if (togglebutton1.isChecked()) {
            Toast.makeText(this, "Your live camera is being shared", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mCamera.startPreview();
                    mCamera.takePicture(null, null, mPicture);
                }
            }, 0, 5000);
//        startActivity(new Intent(MainActivity.this, MyCamera.class));
        } else {
            mCamera.stopPreview();
            Toast.makeText(this, "Camera sharing off", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleclick(View view) {
        if (togglebutton.isChecked()) {
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mRootReference = FirebaseDatabase.getInstance("https://crime1.firebaseio.com/").getReference();
            DatabaseReference mContactReference = mRootReference.child("user").child(number).child("prime");


            mContactReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    prime_id = snapshot.getValue().toString();

                    //if prime_id is received,searching in contact table
                    DatabaseReference mContactReference1 = mRootReference.child("Contact").child(prime_id);
                    //            mContactReference = mRootReference.child("Contact").child(prime_id);
                    try {
                        mContactReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mMainTargetid = dataSnapshot.getValue().toString();
//                                Toast.makeText(Sinch_MainActivity.this,"second " +mMainTargetid,Toast.LENGTH_LONG).show();
                                    Log.d("id_found", String.valueOf(mMainTargetid));
                                    try {
                                        Call currentcall = Sinch_Apps.callClient.callUser(mMainTargetid);

//                                    Intent callscreen = new Intent(MainActivity.this, IncommingCallActivity.class);
//                                    callscreen.putExtra("callid", currentcall.getCallId());
//                                    callscreen.putExtra("incomming", false);
//                                    callscreen.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(callscreen);


                                        //adding
                                        call = Sinch_Apps.callClient.getCall(currentcall.getCallId());
                                        isIncomming = false;
                                        Sinch_UiUtils.setFullscreen(MainActivity.this, false);
                                        mCallingStatus.setText("MEMANGGIL...");
                                        mCallingAnswer.setVisibility(View.GONE);
                                        mCallingName.setText(call.getRemoteUserId() + "");
                                        mCallingReject.setText("END");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "Oops... Your Prime Contact doesn't has iSafe ):", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
//        startActivity(new Intent(MainActivity.this, Sinch_MainActivity.class));
        } else {
            Toast.makeText(this, "Mic sharing turned off...", Toast.LENGTH_SHORT).show();
            call.hangup();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                recreate();
            }
        }
    }


    //camera data
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                file_path = String.valueOf(pictureFile);
                Log.d("image", String.valueOf(pictureFile));
                String[] arr = file_path.split("/");
                int sz = arr.length;
                Log.d("image_id", arr[sz - 1]);
                //  StorageReference mStorageRef = FirebaseStorage.getInstance("gs://crime1.appspot.com").getReference();
                //   StorageReference riversRef = mStorageRef.child(no + "/" + arr[sz - 1]);
                file = Uri.fromFile(new File(String.valueOf(pictureFile)));
                Log.e("uri", "" + file);

                /*
                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Log.d("successfully_upload", "image uploaded successfully");
                                File fdelete = new File(file_path);
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Log.d("file_deleted", "deletion successfully");
                                    } else {
                                        Log.d("file_deleted", "deletion filed");
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.d("successfully_upload", "failed");
                            }
                        });
                */

                mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(primeContact);
                mStorageRef = FirebaseStorage.getInstance().getReference("uploads").child(primeContact);
                UploadImage();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }


    };

    private void UploadImage() {
        if (file != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(file));
            mUploadTask = fileReference.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            String uploadId = mDatabaseRef.push().getKey();
                                            mDatabaseRef.child(uploadId).setValue(imageUrl);
                                        }
                                    });
                                }
                            }
                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            //   Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            Toast.makeText(MainActivity.this, "Progress" + progress, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri file) {

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(file));

    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "iSafe");
        mediaStorageDir.mkdirs();
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp:", "failed to create directory");
                return null;
            }
            Log.d("MyCameraApp:", "success to create directory");
        } else {
            Log.d("MyCameraApp:", "dir exist");
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


//        Toast.makeText(getContext(),String.valueOf(mediaFile),Toast.LENGTH_LONG).show();
        return mediaFile;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.my_camera, menu);
//        getMenuInflater().inflate(R.menu.firebase_menu, menu);
//        return true;
//    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate();
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // camera_main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
        if (requestCode == RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "RECORD AUDIO Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "RECORD AUDIO Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "CALL PHONE Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "CALL PHONE Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

         */
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (event.values[0] == 0) {
            if (!isIncomming || call.getState() == CallState.ESTABLISHED) {
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
                Sinch_UiUtils.enableDisableViewGroup((ViewGroup) findViewById(R.id.calling_root).getParent(), false);
                Sinch_UiUtils.setFullscreen(this, true);
                mCallingBlacksreen.setVisibility(View.VISIBLE);
            }
        } else {
            params.screenBrightness = -1;
            getWindow().setAttributes(params);
            Sinch_UiUtils.enableDisableViewGroup((ViewGroup) findViewById(R.id.calling_root).getParent(), true);
            Sinch_UiUtils.setFullscreen(this, false);
            mCallingBlacksreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.calling_answer:
                call.answer();
                mCallingAnswer.setVisibility(View.GONE);
                mCallingReject.setText("END");
                mCallingStatus.setText("ACTIVE CALL");
                setBlinking(mCallingNotify, false);
                if (r != null) r.stop();
                Sinch_UiUtils.setFullscreen(this, false);
                break;
            case R.id.calling_reject:
                Toast.makeText(this, "Prime contact denied to access your live mic", Toast.LENGTH_LONG).show();
                call.hangup();
                if (r != null) r.stop();
                finish();
                break;
        }
    }

    private void setBlinking(View object, boolean status) {
        if (!status) {
            object.animate().cancel();
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(object, View.ALPHA, 0.1f, 1.0f);
        anim.setDuration(1000);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchLogging(SinchStatus.SinchLogger sinchLogger) {
        if (sinchLogger.message.contains("terminationCause=DENIED") && !isIncomming) {
            if (r != null) r.stop();
            call.hangup();
            Toast.makeText(this, "USER REJECTED", Toast.LENGTH_SHORT).show();
            finish();
        } else if (sinchLogger.message.contains("onSessionEstablished")) {
            mCallingStatus.setText("ACTIVE CALL");
        } else if (sinchLogger.message.contains("onSessionTerminated")) {
            call.hangup();
            if (r != null) r.stop();
            if (sinchLogger.message.contains("terminationCause=NO_ANSWER")) {
                Toast.makeText(this, "NO ANSWER", Toast.LENGTH_SHORT).show();
            } else if (sinchLogger.message.contains("terminationCause=TIMEOUT")) {
                Toast.makeText(this, "TIMEOUT", Toast.LENGTH_SHORT).show();
            } else if (sinchLogger.message.contains("terminationCause=CANCELED")) {
                Notification n = new NotificationCompat.Builder(this, "calling").setContentTitle("Missed Call").setAutoCancel(true).setContentText("You have missed call from " + call.getRemoteUserId()).setSmallIcon(android.R.drawable.sym_call_missed).build();
                NotificationManagerCompat.from(this).notify(1133, n);
            }
            finish();
        }
    }

    public void Image_Shared(View view) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("uploads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(no)) {

                    Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        if (flag) {
            Intent intent = new Intent(MainActivity.this, AllContacts.class);
            intent.putExtra("ImageIntent", true);
            startActivity(intent);
        }

         */
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
