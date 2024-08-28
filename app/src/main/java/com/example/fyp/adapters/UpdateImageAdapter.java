package com.example.fyp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.PrivateAdsModel;
import com.example.fyp.models.RVImagesModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UpdateImageAdapter extends RecyclerView.Adapter<UpdateImageAdapter.ImageViewHolder> {

    private Context context;
    private List<RVImagesModel> imageUris;

    public UpdateImageAdapter(Context context, List<RVImagesModel> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_car_image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RVImagesModel imageUri = imageUris.get(position);
        holder.rvImageProgressBar.setVisibility(View.VISIBLE);
        Picasso.get().
                load(imageUri.getCarImages()).
                into(holder.ivUpdateImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.rvImageProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.rvImageProgressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUpdateImage;
        ProgressBar rvImageProgressBar;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUpdateImage = itemView.findViewById(R.id.ivUpdateImage);
            rvImageProgressBar = itemView.findViewById(R.id.rvImageProgressBar);
        }
    }
}

