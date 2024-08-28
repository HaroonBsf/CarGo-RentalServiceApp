package com.example.fyp.splash;

import static com.example.fyp.network.ConnectionReceiver.isConnected;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.Boarding.OnBoardingOne;
import com.example.fyp.main.MainActivity;
import com.example.fyp.R;
import com.example.fyp.callback.CallbackData;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.ReadWriteUserDetails;
import com.example.fyp.network.ConnectionReceiver;
import com.example.fyp.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    BroadcastReceiver broadcastReceiver;
    public static ArrayList<GetAddModel> testArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        broadcastReceiver = new ConnectionReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (!isConnected(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this, R.style.NetworkAlertDialog);
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.network_error_dialog, null);
            Button btnRetry = view.findViewById(R.id.btnRetry);
            builder.setCancelable(false);
            builder.setView(view);

            Dialog dialog = builder.create();
            dialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
            dialog.show();

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected(SplashScreen.this)) {
                        dialog.dismiss();
                        getAllUserAds();
                    } else {
                        Toast.makeText(SplashScreen.this, "Internet is not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }

        getAllUserAds();

    }

    public void moveNext() {
        if (currentUser != null) {

            Util.getUserDataOnline(new CallbackData() {
                @Override
                public void getUserCallback(ReadWriteUserDetails user) {
                    Constant.userData = user;
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, OnBoardingOne.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        }
    }

    private void getAllUserAds() {
//        Constant.carAdsList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("allUserAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        String key = user.getKey();
                        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userData : snapshot.getChildren()) {
                                    GetAddModel adsData = userData.getValue(GetAddModel.class);
                                    testArray.add(adsData);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        moveNext();
                    }
                } else {
                    moveNext();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}