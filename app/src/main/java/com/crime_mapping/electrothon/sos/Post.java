package com.crime_mapping.electrothon.sos;

public class Post {
    private  String lat;
    private String longg;
    private String details;

//    public Post(String lat, String longg, String details) {
//        this.lat = lat;
//        this.longg = longg;
//        this.details = details;
//    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLongg(String longg) {
        this.longg = longg;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLat() {
        return lat;
    }

    public String getLongg() {
        return longg;
    }

    public String getDetails() {
        return details;
    }



    @Override
    public String toString() {
        return "Posts{" +
                "lat=" + lat +
                ", longg=" + longg +
                ", details='" + details +
                '}';
    }
}
