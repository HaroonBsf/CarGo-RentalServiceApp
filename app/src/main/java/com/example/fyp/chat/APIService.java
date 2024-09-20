package com.example.fyp.chat;

import com.example.fyp.notifications.MyResponse;
import com.example.fyp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: Bearer "
            }
    )
    @POST("/v1/projects/fyp-77d6f/messages:send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
