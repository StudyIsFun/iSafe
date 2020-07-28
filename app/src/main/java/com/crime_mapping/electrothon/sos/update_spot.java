package com.crime_mapping.electrothon.sos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class update_spot extends Activity implements LocationListener {
    private EditText e1, e2,e3;
    private Button b1;
    private SihApi sihApi;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    long no_user=0;
    long no_user1=0;
    long temp=0;
    String lat;
    DatabaseReference user_no;
    String provider;
    String details; // type of crime
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
        details = e3.getText().toString();
        Intent intentt = getIntent();
        latti = intentt.getStringExtra("latti");
        longgi = intentt.getStringExtra("longgi");

    }


    public void submit(View view) {
        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();
        if(s1.equals("risi") && s2.equals("risi"))
        {



            try {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://sihapi--psproject.repl.co/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                 sihApi = retrofit.create(SihApi.class);

                sendPost();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }



//            user_no = FirebaseDatabase.getInstance().getReference().child("spots");
//            user_no.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    if(dataSnapshot.exists()){
////                        for(DataSnapshot ds: dataSnapshot.getChildren())
////                        {
////                            no_user++;
////                        }
//                        no_user = dataSnapshot.getChildrenCount();
//                        no_user1 = no_user;
////                        System.out.println("no_user is " +no_user);
//                    }
//                    Toast.makeText(update_spot.this ,String.valueOf(no_user) ,Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//
//
//            });
//
//            if(latti!=null && longgi != null)
//            {
////
//                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("spots").child(String.valueOf(no_user));
//                firebaseDatabase.child("lat").setValue(latti);
//                firebaseDatabase.child("lon").setValue(longgi);
//                firebaseDatabase.child("details").setValue(e3.getText().toString());
//
//                Toast.makeText(update_spot.this,latti+" "+longgi, Toast.LENGTH_LONG).show();
//
//            }
        }
    }
    public void sendPost() {
//        latti = "31.0344";
//        longgi = "73.999";
        System.out.println("latti is "+latti);
        System.out.println("longgi is "+longgi);
//        Post post = new Post(latti, longgi, details);

        Post post = new Post();
        Post.setLat(latti);
        Post.setLongg(longgi);
        Post.setDetails(details);
        
        Call<Post> call = sihApi.sendPosts(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, response.body().getDetails());
                }
                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
            }

//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
//            }


            @Override
            public void onFailure(Call<Post> call, Throwable t){
            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
            });
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
