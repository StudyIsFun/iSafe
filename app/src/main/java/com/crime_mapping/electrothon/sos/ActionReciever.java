package com.crime_mapping.electrothon.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("action");
        if(action.equals("action1")){
            Toast.makeText(context,"live cam sharing on",Toast.LENGTH_SHORT).show();

        }
        else if(action.equals("action2")){
            Toast.makeText(context,"live mic sharing on",Toast.LENGTH_SHORT).show();

        }
        else if(action.equals("action3")){
            Toast.makeText(context,"sos sent",Toast.LENGTH_SHORT).show();


        }
        else if(action.equals("action4")){
            Toast.makeText(context,"nearby area around me",Toast.LENGTH_SHORT).show();

        }
        else if(action.equals("action5")){
            Toast.makeText(context,"location sent",Toast.LENGTH_SHORT).show();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }


}
