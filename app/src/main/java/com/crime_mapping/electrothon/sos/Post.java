package com.crime_mapping.electrothon.sos;

public class Post {
    private float lat;
    private float long1;

    public Post(double lat11, double long11) {
        this.lat= (float) lat11;
        this.long1= (float) long11;

    }

    public float getLat() {
        return lat;
    }

    public float getLong1() {
        return long1;
    }
}
