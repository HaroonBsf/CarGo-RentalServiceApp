package com.example.fyp.chat;

import com.example.fyp.notifications.FCMMessage;
import com.example.fyp.notifications.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers("Content-Type: application/json")
    @POST("/v1/projects/fyp-77d6f/messages:send")
    Call<MyResponse> sendNotification(@Body FCMMessage message, @Header("Authorization") String authHeader);


}
