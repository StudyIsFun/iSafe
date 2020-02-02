package com.crime_mapping.electrothon.sos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class prime_contacts extends AppCompatActivity {
    DatabaseReference db;
    EditText e1,e2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_contacts);
        e1 = (EditText)findViewById(R.id.mob);
        e2 = (EditText)findViewById(R.id.self_no);
    }

    public void submit_prime(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Locations").child(uId);
        db.child("prime").setValue(e1.getText().toString());
        db.child("self").setValue(e2.getText().toString());

        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("who");
        db1.child(e1.getText().toString()).setValue(uId);

    }

    public void save(View view) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("details").child(e2.getText().toString()).setValue(uId);
    }

    public void live_location(View view) {
        Intent intent = new Intent(this,MapsActivity4.class);
        startActivity(intent);

    }
}
