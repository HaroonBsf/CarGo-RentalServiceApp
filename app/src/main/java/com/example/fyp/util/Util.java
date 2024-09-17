package com.example.fyp.util;


import androidx.annotation.NonNull;

import com.example.fyp.constant.Constant;
import com.example.fyp.models.ReadWriteUserDetails;
import com.example.fyp.callback.CallbackData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Util {
    public static void getUserDataOnline(CallbackData callbackData){
        String UUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    String cmp = (String) snapshot.child("company").getValue();
                    String email = (String) snapshot.child("email").getValue();
                    String cnic = (String) snapshot.child("cnic").getValue();
                    String phone = (String) snapshot.child("phone").getValue();
                    ReadWriteUserDetails user = new ReadWriteUserDetails(name, cmp, email, cnic, phone, UUID, "offline");
                    callbackData.getUserCallback(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
