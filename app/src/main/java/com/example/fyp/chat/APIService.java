package com.example.fyp.chat;

import com.example.fyp.notifications.MyResponse;
import com.example.fyp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlw9yy4k:APA91bFQzUpLg6y-xjQS5w_GOSGlpQgvJFoPhidBDYBIiNFLUpKMK82Gdf_Ovx0vvIzTggs0FN1ZDFZJJQmSuLYdNohI1OelTtqV05ffw0oziCj5zLlT3sVx7xkiet8Od0fqFfyc716i"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
