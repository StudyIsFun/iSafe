package com.crime_mapping.sih2020.sos.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crime {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
