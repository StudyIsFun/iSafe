package com.crime_mapping.electrothon.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class All_Images extends AppCompatActivity {

    TextView cont;
    Button btn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    double lat, lon;
    String contact;
    private RecyclerView recyclerView;
    private List<ContactModel> contactModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        recyclerView = findViewById(R.id.contactRecylcer);
        contactModels = new ArrayList<>();
        preferences = getSharedPreferences("App", Context.MODE_PRIVATE);
        editor = preferences.edit();
        final String no = preferences.getString("PHN", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Location_Shared").child(no);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("responsess",String.valueOf(snapshot.getKey()));
                        contactModels.add(new ContactModel(snapshot.child("Name").getValue().toString(),String.valueOf(snapshot.getKey()),snapshot.child("Latitude").getValue().toString(),snapshot.child("Longitude").getValue().toString()));
//                        for(DataSnapshot shot: snapshot.getChildren())  {
//                         Log.e("contact",""+shot.getKey());
//
//                         contactModels.add(new ContactModel(""+dataSnapshot.child(no).child(shot.getKey()).child("Name").getValue().toString(),""+shot.getKey()));
//                     }

                        //  Log.e("name",""+name);
                        //
                        //   contactModels.add(new ContactModel(dataSnapshot.child(no).child(contact).child("Name").getValue().toString(), contact));
                    }
//                    Log.e("path", "" + dataSnapshot.child(no).child("9988776655").child("Latitude").getValue().toString());
                    //lat = Double.valueOf(dataSnapshot.child(contact).child("Latitude").getValue().toString());
                    // lon = Double.valueOf(dataSnapshot.child(contact).child("Longitude").getValue().toString());
                    //  GoToMap();

                    ContactAdapter adapter = new ContactAdapter(contactModels, All_Images.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(All_Images.this));
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(All_Images.this, "No user have shared location with you.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void GoToMap() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(All_Images.this, MapsActivity.class);
                intent.putExtra("Latitude", lat);
                intent.putExtra("Longitude", lon);
                startActivity(intent);
            }
        });
    }
}
