package com.crime_mapping.sih2020.sos.api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {



    @GET("crime")
    Call<Crime> pushcrime(

            @Query("lat") String lat,
            @Query("long") String longg,
            @Query("crime") String crime

    );

    @GET("sos")
    Call<Crime> send_sos(
            @Query("lat") String lat,
            @Query("long") String longg,
            @Query("phone") String phone
    );

}
