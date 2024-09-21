package com.example.fyp.notifications;

import android.content.Context;
import android.util.Log;

import com.example.fyp.chat.APIService;
import com.example.fyp.chat.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMService {

    Context context;
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private APIService fcmApi = retrofit.create(APIService.class);


    public void sendNotification(String recipientToken, String user, int icon, String body, String title, String sented) {
        Sender messageContent = new Sender(new Data(user, icon, body, title, sented),
                recipientToken);

        FCMMessage message = new FCMMessage(messageContent);
        AccessToken accessToken = new AccessToken();

        Call<MyResponse> call = fcmApi.sendNotification(message, "Bearer "+new Thread(new Runnable() {
            @Override
            public void run() {
                accessToken.getAccessToken(context);
            }
        }));
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Notification sent: " + response.body().getName());
                } else {
                    Log.e("FCM", "Failed to send notification: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.e("FCM", "Error: " + t.getMessage());
            }
        });
    }

    /*private void authReceiver() {
        AccessToken accessToken = new AccessToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = accessToken.getAccessToken(context);
                Log.d("AccessToken", "Token: " + token);
            }
        }).start();
    }*/
}

