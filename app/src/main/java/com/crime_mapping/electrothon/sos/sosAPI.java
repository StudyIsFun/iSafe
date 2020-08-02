package com.crime_mapping.electrothon.sos;

import com.crime_mapping.electrothon.sos.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface sosAPI {
    @POST("sos")
    Call<Post> createPost(@Body Post sos);

}
