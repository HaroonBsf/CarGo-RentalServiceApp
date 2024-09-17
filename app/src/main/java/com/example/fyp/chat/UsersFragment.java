package com.example.fyp.chat;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fyp.R;
import com.example.fyp.adapters.MessagesAdapter;
import com.example.fyp.models.ChatModel;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UsersFragment extends Fragment {

    TextView tvChats, tvUsers;
    View chatsBG, usersBG;
    RecyclerView rvMessages;
    MessagesAdapter messagesAdapter;
    List<UsersModel> usersList;
    List<String> chatsList;
    DatabaseReference reference;
    FirebaseUser fuser;
    LinearLayout llNoMessages;
    boolean isOnCreateRun;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llNoMessages = view.findViewById(R.id.llNoMessages);
        rvMessages = view.findViewById(R.id.rvMessages);

        chatsBG = view.findViewById(R.id.selectedAllAdsBG);
        tvChats = view.findViewById(R.id.tvAllVehicles);

        usersBG = view.findViewById(R.id.selectedNearestBG);
        tvUsers = view.findViewById(R.id.tvNearest);

        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();
        chatsList = new ArrayList<>();

        if (isOnCreateRun){
            chatsList();
        }
        tvChats.setOnClickListener(v -> {
            chatsList();
        });
        tvUsers.setOnClickListener(v -> {
            usersList();
        });

    }

    private void chatsList() {
        usersBG.setVisibility(View.GONE);
        chatsBG.setVisibility(View.VISIBLE);
        tvUsers.setTextColor(Color.BLACK);
        tvChats.setTextColor(Color.WHITE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    if (chat.getSender().equals(fuser.getUid())) {
                        chatsList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        chatsList.add(chat.getSender());
                    }
                }
                readChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readChats() {
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                HashSet<String> uniqueUserIds = new HashSet<>();
                List<UsersModel> tempList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersModel user = dataSnapshot.getValue(UsersModel.class);

                    for (String id : chatsList) {
                        if (user.getId().equals(id) && !uniqueUserIds.contains(user.getId())) {
                            tempList.add(user);
                            uniqueUserIds.add(user.getId());
                        }
                    }

                }
                usersList.addAll(tempList);

                if (usersList.isEmpty()) {
                    llNoMessages.setVisibility(View.VISIBLE);
                } else {
                    llNoMessages.setVisibility(View.GONE);
                }
                messagesAdapter = new MessagesAdapter(getContext(), usersList, true);
                rvMessages.setAdapter(messagesAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void usersList() {
//        llSearchNotFound.setVisibility(View.GONE);
        usersBG.setVisibility(View.VISIBLE);
        chatsBG.setVisibility(View.GONE);
        tvUsers.setTextColor(Color.WHITE);
        tvChats.setTextColor(Color.BLACK);
        llNoMessages.setVisibility(View.GONE);


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                chatsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    UsersModel usersModel = dataSnapshot.getValue(UsersModel.class);
                    if (!key.equals(fuser.getUid())) {
                        usersList.add(usersModel);
                    }
                }
                messagesAdapter = new MessagesAdapter(getContext(), usersList, false);
                rvMessages.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isOnCreateRun) {
            chatsList();
        }
    }
}