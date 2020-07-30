package com.crime_mapping.electrothon.sos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Locale;

public class language extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        loadLocale();
    }


    private void setLocale(String lang) {
//        Toast.makeText(this,lang,Toast.LENGTH_LONG).show();
        Log.d("new lang",lang);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        //saving data in shared prefrences
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Langg",lang);

        editor.apply();

    }
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Langg","");
        setLocale(language);
    }

    public void english(View view) {
        setLocale("en");
        startActivity(new Intent(this,prime_contacts.class));
//        recreate();
    }

    public void hindi(View view) {
        setLocale("hi");
        recreate();
        startActivity(new Intent(this,prime_contacts.class));

    }

    public void tamil(View view) {
        setLocale("en");
        recreate();
    }

    public void kannada(View view) {
        setLocale("en");
        recreate();
    }

    public void gujarati(View view) {
        setLocale("en");
        recreate();
    }

    public void malayalam(View view) {
        setLocale("en");
        recreate();
    }

    public void marathi(View view) {
        setLocale("en");
        recreate();
    }

    public void bengali(View view) {
        setLocale("en");
        recreate();
    }

    public void telugu(View view) {
        setLocale("en");
        recreate();
    }

    public void urdu(View view) {
        setLocale("en");
        recreate();
    }

    public void udiya(View view) {
        setLocale("en");
        recreate();
    }

    public void punjabi(View view) {
        setLocale("en");
        recreate();
    }
}