package com.example.fyp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageSelectionAdapter extends RecyclerView.Adapter<ImageSelectionAdapter.ImageViewHolder> {

    public interface ImageRemoveListener {
        void onImageRemoved(Uri imageUri);
    }

    private Context context;
    private List<Uri> imageUris;
    private ImageRemoveListener removeListener;

    public ImageSelectionAdapter(Context context, List<Uri> imageUris, ImageRemoveListener removeListener) {
        this.context = context;
        this.imageUris = imageUris;
        this.removeListener = removeListener;

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_images_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri imageUri = imageUris.get(position);
        Picasso.get().load(imageUri).into(holder.selectImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NetworkAlertDialog);
                builder.setTitle("Delete Picture");
                builder.setMessage("Are you sure you want to delete the picture?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeImage(imageUri);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);

                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeImage(Uri imageUri) {
        imageUris.remove(imageUri);
        notifyDataSetChanged();

        if (removeListener != null) {
            removeListener.onImageRemoved(imageUri);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView selectImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            selectImage = itemView.findViewById(R.id.ivSelectImage);
        }
    }
}
