package com.example.fsearch.tools.server;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CoordinateService {
    @POST("/coordinates/set_")
    Call<Boolean>  sendCoordinate(@Body Coordinates coordinate,@Query("hashName") String hashName);
}
