package com.crime_mapping.electrothon.sos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Sinch_MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mMainMyid;
    private Button mMainCallbtn;
//    private EditText mMainTargetid;
    private String mMainTargetid;
    private TextView mMainStatus;

    private int RECORD_AUDIO = 1;
    private int CALL_PHONE = 1;

    private  String phone_no;
//    DataSnapshot dataSnapshot;
//    DatabaseReference firebaseDatabase;

//    FirebaseApp.initializeApp(this);
    DatabaseReference mRootReference = FirebaseDatabase.getInstance("https://crime1.firebaseio.com/").getReference();
//    DatabaseReference mContactReference = mRootReference.child("Contact/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinch_activity_main);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainMyid.setText(Sinch_Apps.USER_ID);
        mMainCallbtn.setEnabled(Sinch_Apps.sinchClient.isStarted());
        startService(new Intent(this, SinchService.class));
        if(Sinch_Apps.callClient!=null&& Sinch_Apps.sinchClient.isStarted()){
            mMainStatus.setText("Client Connected, ready to use!");
        }


//        try {
//            mContactReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    mMainTargetid = dataSnapshot.child("phone_no").getValue().toString();
//                    System.out.println(mMainTargetid);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mMainTargetid  = dataSnapshot.getValue(String.class);
//                System.out.println(mMainTargetid);
////                Toast.makeText(MainActivity.this, "call id value", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });



        // Run time permission stuff
        if (ContextCompat.checkSelfPermission(Sinch_MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Sinch_MainActivity.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
        }else{
            requestRecordAudioPermission();
        }

        if (ContextCompat.checkSelfPermission(Sinch_MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Sinch_MainActivity.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
        }else{
            requestCallPhonePermission();
        }

    }

    public void requestRecordAudioPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("This permission is needed to record audio for phone call")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(Sinch_MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();

        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
    }

    public  void requestCallPhonePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("This permission is needed to make a phone call")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(Sinch_MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();

        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
    }

    // Run time permission stuff ended

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RECORD_AUDIO){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "RECORD AUDIO Permission GRANTED", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "RECORD AUDIO Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==CALL_PHONE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "CALL PHONE Permission GRANTED", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "CALL PHONE Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMainMyid = findViewById(R.id.main_myid);
        mMainCallbtn = findViewById(R.id.main_callbtn);
        mMainCallbtn.setOnClickListener(this);
//        mMainTargetid = findViewById(R.id.main_targetid);
        mMainStatus = findViewById(R.id.main_status);
    }

    @Override
    public void onClick(View v) {
        Intent newintent = new Intent(this,MyCamera.class);
        startActivity(newintent);
        switch (v.getId()) {
            default:
                break;
            case R.id.main_callbtn:
//                if(mMainTargetid.length()<8){
//                    mMainTargetid.setError("Enter the correct ID");
//                    break;
//                }else mMainTargetid.setError(null);
                if(Sinch_Apps.callClient==null){
                    Toast.makeText(this, "Sinch Client not connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call currentcall = Sinch_Apps.callClient.callUser(mMainTargetid);

                Intent callscreen = new Intent(this, IncommingCallActivity.class);
                callscreen.putExtra("callid", currentcall.getCallId());
                callscreen.putExtra("incomming", false);
                callscreen.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(callscreen);
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchConnected(SinchStatus.SinchConnected sinchConnected){
        mMainStatus.append(String.format("* CONNECTED :)\n---------------------------\n"));
        mMainCallbtn.setEnabled(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchDisconnected(SinchStatus.SinchDisconnected sinchDisconnected){
        mMainStatus.append(String.format("* DISCONNECTED\n---------------------------\n"));
        mMainCallbtn.setEnabled(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchFailed(SinchStatus.SinchFailed sinchFailed){
        mMainStatus.append(String.format("* CONNECTION FAILED: %s\n---------------------------\n", sinchFailed.error.getMessage()));
        mMainCallbtn.setEnabled(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSinchLogging(SinchStatus.SinchLogger sinchLogger){
        mMainStatus.append(String.format("* %s ** %s ** %s\n---------------------------\n", sinchLogger.message, sinchLogger.area, sinchLogger.level));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
