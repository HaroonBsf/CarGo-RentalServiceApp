package com.example.fyp.chat;

import android.content.Context;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken(Context context) {
        try {
            InputStream serviceAccountStream = context.getAssets().open("fyp-77d6f-firebase-adminsdk-lqn96-d5effb75fc.json");
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}

