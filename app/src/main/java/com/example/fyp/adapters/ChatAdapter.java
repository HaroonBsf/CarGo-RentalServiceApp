package com.example.fyp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<ChatModel> mChat;
    Context context;
    FirebaseUser fUser;

    public ChatAdapter(Context context, List<ChatModel> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ChatAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ItemViewHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ItemViewHolder holder, int position) {
        ChatModel chatModel = mChat.get(position);
        holder.show_message.setText(chatModel.getMessage());

        if (position == mChat.size()-1) {
            if (chatModel.isIsseen()) {
                holder.txt_seen.setText("Seen");
                holder.txt_seen.setTextColor(Color.RED);
            } else {
                holder.txt_seen.setText("Delivered");
                holder.txt_seen.setTextColor(Color.BLACK);

            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView show_message;
        TextView txt_seen;

        public ItemViewHolder(View view) {
            super(view);
            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
