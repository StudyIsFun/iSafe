package com.crime_mapping.electrothon.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class prime_contacts extends AppCompatActivity {
    DatabaseReference db;
    EditText e1, e2;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_contacts);
        e1 = (EditText) findViewById(R.id.mob);
        e2 = (EditText) findViewById(R.id.self_no);
        preferences = getSharedPreferences("App",MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void submit_prime(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Location_Shared").child(e1.getText().toString());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("Contact", e2.getText().toString());
        db.setValue(childUpdates);
        editor.putString("PrimeContact",e1.getText().toString());
        editor.putBoolean("AllowLocSharing",true);
        editor.commit();

        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("who");
        db1.child(e1.getText().toString()).setValue(uId);

    }

    public void save(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("details").child(e2.getText().toString()).setValue(uId);
    }

    public void live_location(View view) {
        Intent intent = new Intent(this, MapsActivity4.class);
        startActivity(intent);

    }
}
