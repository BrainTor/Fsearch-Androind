package com.example.fsearch.tools.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FireService {
    @POST("/fire/set_")
    Call<Boolean> sendFire(@Body Fire fire,@Query("hashName") String hashName);
}
