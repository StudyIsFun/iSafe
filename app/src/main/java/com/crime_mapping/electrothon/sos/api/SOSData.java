package com.crime_mapping.electrothon.sos.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SOSData {
    @SerializedName("nearby")
    @Expose
    private List<String> nearby = null;

    public List<String> getNearby() {
        return nearby;
    }

    public void setNearby(List<String> nearby) {
        this.nearby = nearby;
    }
}
