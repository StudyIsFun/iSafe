package com.crime_mapping.electrothon.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class prime_contacts extends AppCompatActivity {
    DatabaseReference db, db1;
    EditText e1, e2, s1, s3;
    TextView s2;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SharedPreferences phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_contact);
        e1 = (EditText) findViewById(R.id.enterPrimeContactEdit);
        s1 = (EditText) findViewById(R.id.self_name);
        s3 = (EditText) findViewById(R.id.self_blood);
        preferences = getSharedPreferences("App", MODE_PRIVATE);
        editor = preferences.edit();

        phone = getSharedPreferences("App", Context.MODE_PRIVATE);
       // s2.setText(phone.getString("PHN", ""));
    }

    public void submit_prime(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Location_Shared").child(e1.getText().toString());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("Contact", phone.getString("PHN", ""));
        db.setValue(childUpdates);
        editor.putString("PrimeContact", e1.getText().toString());
        editor.putBoolean("AllowLocSharing", true);
        editor.commit();
        db1 = FirebaseDatabase.getInstance().getReference().child("user").child(e2.getText().toString());
        db1.child("prime").setValue(e1.getText().toString());
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("who");
        db1.child(e1.getText().toString()).setValue(uId);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void save(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("details").child(e2.getText().toString()).setValue(uId);
    }

    public void live_location(View view) {
        Intent intent = new Intent(this, MapsActivity4.class);
        startActivity(intent);

    }

    public void submit_details(View view) {
        final String phone = preferences.getString("PHN", "");
        db1 = FirebaseDatabase.getInstance().getReference().child("user").child(phone);
        db1.child("name").setValue(s1.getText().toString());
        db1.child("bloodgroup").setValue(s3.getText().toString());
    }
}
