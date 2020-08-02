package com.crime_mapping.electrothon.sos;

public class Upload {
    private String mImageUrl;
    public Upload( String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public Upload(){

    }
    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}