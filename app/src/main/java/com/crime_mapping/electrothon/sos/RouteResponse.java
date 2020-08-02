package com.crime_mapping.electrothon.sos;

public class RouteResponse {
    private Data[] data;

    public Data[] getData ()
    {
        return data;
    }

    public void setData (Data[] data)
    {
        this.data = data;
    }

    public RouteResponse(Data[] data) {
        this.data = data;
    }
}
