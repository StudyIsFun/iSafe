package com.crime_mapping.electrothon.sos;

import android.app.Application;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;

public class Sinch_Apps extends Application {
    public static String USER_ID;
    public static SinchClient sinchClient;
    public static CallClient callClient;

    DatabaseReference firebaseDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        USER_ID=(""+(Build.FINGERPRINT+Build.MODEL).hashCode()).replace("-","");

        firebaseDatabase = FirebaseDatabase.getInstance("https://crime1.firebaseio.com/").getReference();
        firebaseDatabase.child("Contact").setValue(USER_ID);



        sinchClient = Sinch.getSinchClientBuilder().context(this)
                .applicationKey("aad676d0-4e30-488f-9f22-21f8b4efea55")
                .applicationSecret("U53Q18n0GkKl1VyvPa7NGg==")
                .environmentHost("clientapi.sinch.com")
                .userId(USER_ID)
                .build();
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportCalling(true);
    }
}
