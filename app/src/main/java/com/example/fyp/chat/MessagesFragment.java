package com.example.fyp.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.adapters.MessagesAdapter;
import com.example.fyp.main.MainActivity;
import com.example.fyp.map.GoogleMapActivity;
import com.example.fyp.models.ChatModel;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment implements OnMapReadyCallback {

    public static String otp;
    RecyclerView rvMessages;
    MessagesAdapter messagesAdapter;
    List<GetAddModel> mUsers;
    List<String> usersList;
    FirebaseUser fuser;
    List<ChatModel> mChat;
    List<ChatModel> filterChat = new ArrayList<ChatModel>();
    List<ChatModel> filterChatTwo = new ArrayList<ChatModel>();
    DatabaseReference reference;
    LinearLayout llNoMessages;
    TextView msg;

    private static final int LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    private DatabaseReference databaseReference;

    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        msg = view.findViewById(R.id.tvMessages);
        ImageView ivTravell = view.findViewById(R.id.ivTravell);
        llNoMessages = view.findViewById(R.id.llNoMessages);

        SessionManager sessionManager = SessionManager.getInstance();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_main_d);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference("car_rental_location");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (sessionManager.isLoggedIn()) {
                        saveLocationToFirebase(location);
                    }
                }
            }
        };


        if (sessionManager.isLoggedIn()){
            ivTravell.setVisibility(View.VISIBLE);
            llNoMessages.setVisibility(View.GONE);
            msg.setText("Travelling...");
        }else {
            ivTravell.setVisibility(View.GONE);
            llNoMessages.setVisibility(View.VISIBLE);
            msg.setText("Start Travelling Now");
        }

        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText otp = view.findViewById(R.id.etOTP);
                String code = otp.getText().toString();

                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getContext(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                } else if (otp.length() < 4) {
                    Toast.makeText(getContext(), "OTP must have 4 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("car_rental_location").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            sessionManager.setLoggedIn(true);
                            sessionManager.setUserId(otp.getText().toString());

                            ivTravell.setVisibility(View.VISIBLE);
                            llNoMessages.setVisibility(View.GONE);
                            msg.setText("Travelling...");

                        } else {
                            Toast.makeText(getContext(), "Invalid OTP Code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //startActivity(new Intent(getContext(), GoogleMapActivity.class));
            }
        });
        //rvMessages = view.findViewById(R.id.rvMessages);

//        rvMessages.setHasFixedSize(true);
//        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readMessages();

    }

    private void readMessages() {
        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(myid) || chat.getSender().equals(myid)) {
                        mChat.add(chat);
                    }
                }
                seprateTheChats(myid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seprateTheChats(String myid) {

        for (int i = 0; i < mChat.size(); i++) {
            for (int j = 0; j < mChat.size(); j++) {
                if (mChat.get(i).getSender().equals(mChat.get(j).getSender()) && mChat.get(i).getReceiver().equals(mChat.get(j).getReceiver())) {
                    filterChat.add(mChat.get(i));

                    Log.e("trueR", mChat.get(i).getMessage());
                    break;
                }
            }
        }

        seprateUserChat(myid);

    }

    private void seprateUserChat(String myid) {

        for (int i = 0; i < filterChat.size(); i++) {
            for (int j = 0; j < filterChat.size(); j++) {

                if (myid.equals(filterChat.get(i).getSender()) && filterChat.get(i).getReceiver().equals(filterChat.get(j).getSender()) ||
                        myid.equals(filterChat.get(i).getReceiver()) && filterChat.get(i).getSender().equals(filterChat.get(j).getReceiver())) {

                    filterChatTwo.add(filterChat.get(i));

                    Log.e("trueR1", filterChat.get(i).getSenderName());
                    break;
                }
            }
        }


        for (int i = 0; i < filterChat.size(); i++) {
            for (int j = 0; j < filterChat.size(); j++) {
                String currentId = "";
                if (!filterChat.get(i).getSender().equals(myid)) {
                    currentId = myid;
                }

                if (currentId.equals(filterChat.get(i).getSender()) || currentId.equals(filterChat.get(i).getReceiver())) {
                    //filterChatTwo.get(i);
                }

            }
        }

    }

    private void saveLocationToFirebase(Location location) {
        String otp = sessionManager.getUserId();
        FirebaseDatabase.getInstance().getReference().child("car_rental_location").child(otp).
                setValue(location);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

}