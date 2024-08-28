package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.models.PopularItemsModel;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder  > {
    private List<PopularItemsModel> itemList;

    public ItemsAdapter(List<PopularItemsModel> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_popular_items, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PopularItemsModel item = itemList.get(position);
        holder.carName.setText(item.getCarName());
        holder.popularCar.setImageResource(item.getPopularCar());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView carName;
        ImageView popularCar;
        public ItemViewHolder(View view) {
            super(view);
            carName = itemView.findViewById(R.id.tvPopularCarName);
            popularCar = itemView.findViewById(R.id.ivPopularCar);

        }
    }
}
