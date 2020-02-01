package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class update_spot extends Activity implements LocationListener {
    private EditText e1, e2,e3;
    private Button b1;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    int no_user=0;
    String lat;
    DatabaseReference user_no;
    String provider;
    String latti,longgi;
//    double latti=0,longi=0;
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
    }


    public void submit(View view) {
        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();
        if(s1.equals("risi") && s2.equals("risi"))
        {


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
        }
    }



    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
