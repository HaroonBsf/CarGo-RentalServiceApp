package com.example.fyp.adapters;

import static com.example.fyp.main.MainActivity.dataProgressView;
import static com.example.fyp.network.ConnectionReceiver.isConnected;
import static com.example.fyp.util.NetworkDialogUtils.showNetworkDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Login.OTPVerification;
import com.example.fyp.R;
import com.example.fyp.callback.OnItemClickCallback;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.PrivateAdsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class PrivateAdsAdapter extends RecyclerView.Adapter<PrivateAdsAdapter.ViewHolder> {

    private List<PrivateAdsModel> itemList;
    Context context;
    private OnItemClickCallback listener;


    public PrivateAdsAdapter(Context context, List<PrivateAdsModel> itemList, OnItemClickCallback listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PrivateAdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_ads_rv_item, parent, false);
        return new PrivateAdsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PrivateAdsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PrivateAdsModel item = itemList.get(position);
        holder.privateCarMake.setText(item.getMake());
        holder.privateModel.setText(item.getModel()+ " "+ item.getYear());
        holder.privateVariant.setText(item.getVariant());
        holder.privatePickUpCity.setText(item.getPickup_city());
        holder.privateRent.setText(item.getRent());

        if ("Yes".equals(item.getInsurance())) {
            holder.tvInsuranceStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvInsuranceStatus.setVisibility(View.GONE);
        }

        if (item.getBookingStatus() != null) {
            holder.bookedItemView.setVisibility(View.VISIBLE);
            holder.ivCarAgreement.setVisibility(View.VISIBLE);
        } else {
            holder.bookedItemView.setVisibility(View.GONE);
            holder.ivCarAgreement.setVisibility(View.GONE);
        }

        if (item.getCarImages() != null && !item.getCarImages().isEmpty()) {
            String image = item.getCarImages().get(0);
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(holder.ivPrivateCar);

        } else {
            holder.ivPrivateCar.setImageResource(R.drawable.ic_place_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(context)) {
                    showNetworkDialog(context);
                    return;
                }

                Constant.privateItemModel = item;

                String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UUID).child("userAds");
                if (position != RecyclerView.NO_POSITION) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int i = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (i == position) {
                                    Constant.itemClicked = snapshot.getKey();
                                    listener.onItemClick(position);
                                    break;
                                }
                                i++;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView privateCarMake,privateModel , privateVariant, privatePickUpCity, privateRent, tvInsuranceStatus;
        ImageView ivPrivateCar;
        GifImageView ivCarAgreement;
        View bookedItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            privateCarMake = itemView.findViewById(R.id.tvPirvateCarName);
            privateModel = itemView.findViewById(R.id.tvPrivateModel);
            privateVariant = itemView.findViewById(R.id.tvPrivateVariant);
            privatePickUpCity = itemView.findViewById(R.id.tvPrivateCity);
            privateRent = itemView.findViewById(R.id.tvPrivatePrice);
            tvInsuranceStatus = itemView.findViewById(R.id.tvPrivateInsurance);
            ivPrivateCar = itemView.findViewById(R.id.ivPrivateCar);
            bookedItemView = itemView.findViewById(R.id.bookedItemView);
            ivCarAgreement = itemView.findViewById(R.id.ivCarAgreement);

        }
    }
}
