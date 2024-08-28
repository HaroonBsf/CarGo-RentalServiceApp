package com.example.fyp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fyp.R;
import com.example.fyp.models.LocationSliderModel;

import java.util.List;

public class LocationSliderAdapter extends RecyclerView.Adapter<LocationSliderAdapter.SliderViewHolder> {
    List<LocationSliderModel> locationSliderModel;
    ViewPager2 viewPager2;

    public LocationSliderAdapter(List<LocationSliderModel> locationSliderModel, ViewPager2 viewPager2) {
        this.locationSliderModel = locationSliderModel;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.location_view_pager, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setText(locationSliderModel.get(position));
        if (position == locationSliderModel.size() - 2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return locationSliderModel.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView countryLocation, cityLocation;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            countryLocation = itemView.findViewById(R.id.tv_countryLocation);
            cityLocation = itemView.findViewById(R.id.tv_cityLocation);
        }

        void setText(LocationSliderModel locationSliderModel) {
            countryLocation.setText(locationSliderModel.getCountry());
            cityLocation.setText(locationSliderModel.getCity());
        }
    }
    private Runnable runnable = new Runnable() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            locationSliderModel.addAll(locationSliderModel);
            notifyDataSetChanged();
        }
    };
}
