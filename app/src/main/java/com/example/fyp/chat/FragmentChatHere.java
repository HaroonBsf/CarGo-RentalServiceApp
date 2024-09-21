package com.example.fyp.chat;

import static com.example.fyp.adapters.GridAdapter.bottomSheetDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.adapters.ChatAdapter;
import com.example.fyp.callback.ReceiverUserIdCallback;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.ChatModel;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.notifications.FCMService;
import com.example.fyp.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentChatHere extends Fragment {

    TextView tvViewAdInChat, tvInChatOwnerName, tvInChatOwnerCompany, tvInChatAdInfo, tvDisclaimer, tvUserStatus;
    CardView cv_img_on, cv_img_off;
    EditText etMessage;
    ImageView btnSendMsg, ivCall, ivWhatsapp;
    CircleImageView ivProfileImage;
    ChatAdapter messageAdapter;
    List<ChatModel> mChat;
    RecyclerView rvChat;
    ProgressBar chatProgressBar;
    int count = 0;
    String recieverId = "";
    ValueEventListener seenListener;
    DatabaseReference reference;
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean notify = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_here, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvViewAdInChat = view.findViewById(R.id.tvViewAdInChat);
        tvInChatOwnerName = view.findViewById(R.id.tvInChatOwnerName);
        tvInChatOwnerCompany = view.findViewById(R.id.tvInChatOwnerCompany);
        tvInChatAdInfo = view.findViewById(R.id.tvInChatAdInfo);
        etMessage = view.findViewById(R.id.etMessage);
        btnSendMsg = view.findViewById(R.id.ivSendMsg);
        tvDisclaimer = view.findViewById(R.id.tvDisclaimer);
        chatProgressBar = view.findViewById(R.id.chatProgressBar);
        rvChat = view.findViewById(R.id.rvChat);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUserStatus = view.findViewById(R.id.tvUserStatus);
        cv_img_on = view.findViewById(R.id.cv_img_on);
        cv_img_off = view.findViewById(R.id.cv_img_off);
        // Contact phone
        ivCall = view.findViewById(R.id.ivCall);
        ivWhatsapp = view.findViewById(R.id.ivWhatsapp);

        rvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);

        String name;
        if (Constant.itemGetModel != null && Constant.itemGetModel.getOwnerName() != null) {
            name = Constant.itemGetModel.getOwnerName();
            tvInChatOwnerName.setText(name);
            tvInChatOwnerCompany.setText("Company: " + Constant.itemGetModel.getOwnerCompany());
            tvInChatAdInfo.setText(Constant.itemGetModel.getMake() + " " + Constant.itemGetModel.getModel()
                    + " " + Constant.itemGetModel.getVariant() + " " + Constant.itemGetModel.getYear());
        } else {
            tvViewAdInChat.setVisibility(View.GONE);
            tvInChatAdInfo.setVisibility(View.GONE);
            ivProfileImage.setVisibility(View.VISIBLE);
            name = Constant.usersModel.getName();
            tvInChatOwnerName.setText(name);
            if (Constant.usersModel.getProfile() != null) {
                Picasso.get()
                        .load(Constant.usersModel.getProfile())
                        .placeholder(R.drawable.ic_guest)
                        .into(ivProfileImage);
            } else {
                ivProfileImage.setImageResource(R.drawable.ic_guest);
            }
            if (Constant.usersModel.getCompany() != null && !Constant.usersModel.getCompany().isEmpty()) {
                tvInChatOwnerCompany.setText("Company: " + Constant.usersModel.getCompany());
            } else {
                tvInChatOwnerCompany.setVisibility(View.GONE);
            }

        }

        tvViewAdInChat.setOnClickListener(v -> bottomSheetDialog.show());

        String senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String uniqueOwnerPhone;
        if (Constant.itemGetModel != null && Constant.itemGetModel.getOwnerPhone() != null) {
            uniqueOwnerPhone = Constant.itemGetModel.getOwnerPhone();
        } else {
            uniqueOwnerPhone = Constant.usersModel.getPhone();
        }

        ivWhatsapp.setOnClickListener(v -> whatsAppChat(uniqueOwnerPhone, name));
        ivCall.setOnClickListener(v -> dialPadCall(uniqueOwnerPhone, name));

        chatProgressBar.setVisibility(View.VISIBLE);
        fetchReceiverUserId(getContext(), uniqueOwnerPhone, new ReceiverUserIdCallback() {
            @Override
            public void onReceiverUserIdReceived(String receiverUserId) {
                if (receiverUserId != null) {
                    readMessages(getContext(), senderUserId, receiverUserId);
                    chatProgressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Receiver user ID not found", Toast.LENGTH_SHORT).show();
                    chatProgressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                String msg = etMessage.getText().toString().trim();
                if (!msg.equals("")) {
                    fetchReceiverUserId(getContext(), uniqueOwnerPhone, new ReceiverUserIdCallback() {
                        @Override
                        public void onReceiverUserIdReceived(String receiverUserId) {
                            if (receiverUserId != null) {
                                sendMessage(senderUserId, receiverUserId, msg, name);
                                readMessages(getContext(), senderUserId, receiverUserId);

                            } else {
                                Toast.makeText(getContext(), "Receiver user ID not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                etMessage.setText("");
            }
        });
        fetchReceiverUserId(getContext(), uniqueOwnerPhone, new ReceiverUserIdCallback() {
            @Override
            public void onReceiverUserIdReceived(String receiverUserId) {
                if (receiverUserId != null) {
                    seenMessages(receiverUserId);

                } else {
                    Toast.makeText(getContext(), "Receiver user ID not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateToken(String.valueOf(FirebaseMessaging.getInstance().getToken()));

    }

    @SuppressLint("SetTextI18n")
    private void dialPadCall(String uniqueOwnerPhone, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.open_whatsapp_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        TextView tvWhatsAppNavigate = dialogView.findViewById(R.id.tvWhatsAppNavigate);
        tvWhatsAppNavigate.setText("You're about to contact with " + name
                + ". By continuing, you'll navigate to your mobile contacts, where you can initiate a phone call to " +
                name + ". Standard call rates may apply.");
        dialogView.findViewById(R.id.btnAgreeWhatsApp).setOnClickListener(v -> {
            openDialPad(uniqueOwnerPhone);
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.btnCancelWhatsApp).setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }

    private void openDialPad(String uniqueOwnerPhone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + uniqueOwnerPhone));
        startActivity(intent);
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void whatsAppChat(String uniqueOwnerPhone, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.open_whatsapp_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        TextView tvWhatsAppNavigate = dialogView.findViewById(R.id.tvWhatsAppNavigate);
        tvWhatsAppNavigate.setText("You're about to open a chat on WhatsApp with " +
                name + ". By continuing, you'll leave Car Go and open WhatsApp. " +
                name + " will be able to see your WhatsApp profile information.");
        dialogView.findViewById(R.id.btnAgreeWhatsApp).setOnClickListener(v -> {
            openWhatsApp(uniqueOwnerPhone);
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.btnCancelWhatsApp).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openWhatsApp(String uniqueOwnerPhone) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + uniqueOwnerPhone));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateToken(String token) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference1.child(fUser.getUid()).setValue(token1);

    }

    private void seenMessages(final String userid) {
        String UUID = fUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(UUID) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchReceiverUserId(Context context, String uniqueOwnerPhone, ReceiverUserIdCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String receiverUserId = null;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = userSnapshot.child("phone").getValue(String.class);
                    if (phoneNumber != null && phoneNumber.equals(uniqueOwnerPhone)) {
                        receiverUserId = userSnapshot.getKey();
                        break;
                    }
                }
                callback.onReceiverUserIdReceived(receiverUserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String name) {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("senderName", Constant.userData.name);
        hashMap.put("recieverName", name);


        reference.child("chats").push().setValue(hashMap);

        final String msg = message;
        if (notify) {
            sendNotification(receiver, Constant.userData.name, msg);
        }
        notify = false;

    }

    private void sendNotification(String receiver, String name, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    if (token != null){
                        FCMService fcmService = new FCMService();
                        fcmService.sendNotification(String.valueOf(token), fUser.getUid(), R.mipmap.ic_launcher, name+": "+msg, "New Message", receiver);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FCM", "Error getting token: "+ error);

            }
        });

    }

    private void readMessages(Context context, String myid, String userid) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChat.add(chat);


                    }
                    if (!mChat.isEmpty()) {
                        tvDisclaimer.setVisibility(View.INVISIBLE);
                    } else {
                        tvDisclaimer.setVisibility(View.VISIBLE);
                    }

                    messageAdapter = new ChatAdapter(context, mChat);
                    rvChat.setAdapter(messageAdapter);
                }
                messageAdapter.notifyDataSetChanged();
                rvChat.scrollToPosition(mChat.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getRecieverData() {
        reference = FirebaseDatabase.getInstance().getReference("allUserAds").child(recieverId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        GetAddModel recieverData = user.getValue(GetAddModel.class);
                        Log.e("check_name", recieverData.getOwnerName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String uniqueOwnerPhone;
        if (Constant.itemGetModel != null && Constant.itemGetModel.getOwnerPhone() != null) {
            uniqueOwnerPhone = Constant.itemGetModel.getOwnerPhone();
        } else {
            uniqueOwnerPhone = Constant.usersModel.getPhone();
        }
        setupStatusListener(uniqueOwnerPhone);

    }

    private void setupStatusListener(String phone) {
        fetchReceiverUserId(getContext(), phone, new ReceiverUserIdCallback() {
            @Override
            public void onReceiverUserIdReceived(String receiverUserId) {
                DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(receiverUserId)
                        .child("status");

                statusRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isAdded()) {
                            String status = snapshot.getValue(String.class);
                            if ("offline".equals(status)) {
                                tvUserStatus.setText("offline");
                                tvUserStatus.setTextColor(getResources().getColor(R.color.offline));
                                cv_img_off.setVisibility(View.VISIBLE);
                                cv_img_on.setVisibility(View.GONE);

                            } else {
                                tvUserStatus.setText("online");
                                tvUserStatus.setTextColor(getResources().getColor(R.color.online));
                                cv_img_off.setVisibility(View.GONE);
                                cv_img_on.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    /*@Override
    public void onPause() {
        super.onPause();
        //reference.removeEventListener(seenListener);
    }*/
}