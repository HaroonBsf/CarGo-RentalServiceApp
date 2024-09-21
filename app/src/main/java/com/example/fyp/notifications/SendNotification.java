package com.example.fyp.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp.chat.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class SendNotification {
    private String userFcmToken;
    private String title;
    private String body;
    private Context context;
    private String postUrl = "https://fcm.googleapis.com/v1/projects/fyp-77d6f/messages:send";

    public SendNotification(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    @SuppressLint("NotConstructor")
    public void SendNotification(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageobject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            messageobject.put("token", userFcmToken);
            messageobject.put("notification", notificationObject);
            mainObj.put("message", messageobject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {

            }, volleyError -> {

            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    final Map<String, String> headers = new HashMap<>();

                    AccessToken accessToken = new AccessToken();
                    String token = accessToken.getAccessToken(context);

                    headers.put("content-type", "application/json");
                    headers.put("authorization", "Bearer " + token);

                    return headers;
                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
