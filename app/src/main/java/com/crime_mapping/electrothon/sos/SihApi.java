package com.crime_mapping.electrothon.sos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SihApi {

    @GET("crime")
    Call<Post> sendPosts(@Header("Content-type") String header, @Query("lat") String lat, @Query("long") String lon, @Query("crime") String crime);

    @GET
    Call<String> getUnsafeAreas(@Url String url);

    @GET("heatmap")
    Call<List<LatLong>> getCoordinates();

    @GET("routes")
    Call<RouteResponse> getRoutes( @Query("lat1") String lat1, @Query("long1") String lon1, @Query("lat2") String lat2, @Query("long2") String lon2);
}
