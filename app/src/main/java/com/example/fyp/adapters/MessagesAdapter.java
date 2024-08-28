package com.example.fyp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.models.GetAddModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter  extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<GetAddModel> usersChatList;
    private Context context;

    public MessagesAdapter(Context context, List<GetAddModel> usersChatList) {
        this.context = context;
        this.usersChatList = usersChatList;
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
        GetAddModel item = usersChatList.get(position);
        holder.tvChatUsername.setText(item.getOwnerName());
        /*holder.tvChatCarNameModel.setText(item.getMake()+ " "+ item.getModel()+ " "+ item.getVariant());
        holder.tvChatYear.setText(item.getYear());*/
    }

    @Override
    public int getItemCount() {
        return usersChatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChatUsername,tvChatCarNameModel , tvChatYear;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChatUsername = itemView.findViewById(R.id.tvChatUsername);
            tvChatCarNameModel = itemView.findViewById(R.id.tvChatCarNameAndModel);
            tvChatYear = itemView.findViewById(R.id.tvChatCarModelYear);

        }
    }
}
