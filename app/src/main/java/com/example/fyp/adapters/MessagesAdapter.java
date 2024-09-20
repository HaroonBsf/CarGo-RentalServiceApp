package com.example.fyp.adapters;

import static com.example.fyp.main.MainActivity.NavigationHide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.chat.FragmentChatHere;
import com.example.fyp.chat.UsersFragment;
import com.example.fyp.constant.Constant;
import com.example.fyp.home.HomeFragment;
import com.example.fyp.models.ChatModel;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.ReadWriteUserDetails;
import com.example.fyp.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<UsersModel> usersChatList;
    private Context context;
    private boolean isChat;
    String theLastMSG;

    public MessagesAdapter(Context context, List<UsersModel> usersChatList, boolean isChat) {
        this.context = context;
        this.usersChatList = usersChatList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_messages_item, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        UsersModel item = usersChatList.get(position);
        holder.tvChatUsername.setText(item.getName());
        String company = item.getCompany();
        if (company != null && !company.isEmpty()) {
            holder.tvChatCarNameModel.setText(item.getCompany());
        } else {
            holder.tvChatCarNameModel.setText("No services available by " + item.getName());
            holder.tvChatCarNameModel.setTextColor(Color.GRAY);
        }

        if (item.getProfile() != null && !item.getProfile().isEmpty()) {
            String image = item.getProfile();
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_guest)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_guest);
        }

        if (isChat){
            lastMessage(item.getId(), holder.lastMSG);
        } else {
            holder.lastMSG.setVisibility(View.GONE);

        }

        if (isChat) {
            if (item.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Constant.itemGetModel = null;
            Constant.usersModel = item;
            addToStack(context, new FragmentChatHere(), "UsersFragment");
            NavigationHide();
        });

    }

    @Override
    public int getItemCount() {
        return usersChatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChatUsername, tvChatCarNameModel, lastMSG;
        ImageView image;
        CardView img_on, img_off;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChatUsername = itemView.findViewById(R.id.tvChatUsername);
            tvChatCarNameModel = itemView.findViewById(R.id.tvChatCarNameAndModel);
            lastMSG = itemView.findViewById(R.id.tvChatCarModelYear);
            image = itemView.findViewById(R.id.ivProfile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

        }
    }

    private void addToStack(Context context, Fragment fragment, String stackTitle) {
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(stackTitle);
        transaction.commit();
    }

    private void lastMessage(String userid, TextView last_msg) {
        theLastMSG = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMSG = chat.getMessage();
                    }
                }

                switch (theLastMSG){
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMSG);
                        break;

                }
                theLastMSG = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
