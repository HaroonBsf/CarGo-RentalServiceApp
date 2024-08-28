package com.example.fyp.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.callback.OnCitySelectedListener;
import com.example.fyp.models.GetAddModel;

import java.util.ArrayList;
import java.util.List;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.MyViewHolder> {

    private Context context;
    private List<String> myModelList;
    private OnCitySelectedListener listener;


    public CitiesAdapter(Context context,List<String> myModelList, OnCitySelectedListener listener) {
        this.context = context;
        this.listener = listener;
        this.myModelList = myModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cities_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = myModelList.get(position);
        holder.cityName.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCity = myModelList.get(holder.getAdapterPosition());
                if (listener != null) {
                    listener.onCitySelected(selectedCity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<String> filteredList) {
        this.myModelList = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.tvCityName);
        }
    }
}
