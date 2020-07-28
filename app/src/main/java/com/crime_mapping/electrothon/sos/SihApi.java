package com.crime_mapping.electrothon.sos;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SihApi {

    String API_ROUTE = "crime";
    @Headers({

            "Content-type: application/json"

    })
    @POST(API_ROUTE)
    Call<Post> sendPosts(@Body Post post);
}
