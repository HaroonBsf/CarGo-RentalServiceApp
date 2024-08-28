package com.example.fyp.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.adapters.GridAdapter;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.GetAddModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class HomeCarType extends Fragment {

    TextView tvCarTypeName, tvResultsCount, tvSortArray, tvAdsNotAvailable;
    RecyclerView rvCarTypeSearch;
    ShimmerFrameLayout shimmerFrameLayout;
    GridAdapter adapter;
    List<GetAddModel> adsList = new ArrayList<>();
    DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("allUserAds");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_car_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCarTypeName = view.findViewById(R.id.tvCarTypeName);
        tvResultsCount = view.findViewById(R.id.tvResultsCount);
        tvSortArray = view.findViewById(R.id.tvSortArray);
        rvCarTypeSearch = view.findViewById(R.id.rvCarTypeSearch);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        tvAdsNotAvailable = view.findViewById(R.id.tvAdsNotAvailable);

        tvCarTypeName.setText(Constant.homeCarType);

        adapter = new GridAdapter(adsList, true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvCarTypeSearch.setLayoutManager(linearLayoutManager);

        if (Objects.equals(Constant.homeCarType, "HatchBack")) {
            getCarTypeAds("Hatchback");
        } else if (Objects.equals(Constant.homeCarType, "Sedan")) {
            getCarTypeAds("Sedan");
        } else if (Objects.equals(Constant.homeCarType, "SUV")) {
            getCarTypeAds("SUV");
        } else if (Objects.equals(Constant.homeCarType, "Bus")) {
            getCarTypeAds("Bus");
        }

        tvSortArray.setOnClickListener(v -> sortArray());

    }

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged", "SetTextI18n"})
    private void sortArray() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.sorting_array_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.tvLowToHigh).setOnClickListener(v -> {
            tvSortArray.setText("Low to High Rent");
            Collections.sort(adsList, new Comparator<GetAddModel>() {
                @Override
                public int compare(GetAddModel o1, GetAddModel o2) {
                    return Double.compare(Double.parseDouble(o1.getRent()), Double.parseDouble(o2.getRent()));
                }
            });
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvHighToLow).setOnClickListener(v -> {
            tvSortArray.setText("High to Low Rent");
            Collections.sort(adsList, new Comparator<GetAddModel>() {
                @Override
                public int compare(GetAddModel o1, GetAddModel o2) {
                    return Double.compare(Double.parseDouble(o2.getRent()), Double.parseDouble(o1.getRent()));
                }
            });
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvOldToNew).setOnClickListener(v -> {
            tvSortArray.setText("Old to New Model");
            Collections.sort(adsList, new Comparator<GetAddModel>() {
                @Override
                public int compare(GetAddModel o1, GetAddModel o2) {
                    return Double.compare(Double.parseDouble(o1.getYear()), Double.parseDouble(o2.getYear()));
                }
            });
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvNewToOld).setOnClickListener(v -> {
            tvSortArray.setText("New to Old Model");
            Collections.sort(adsList, new Comparator<GetAddModel>() {
                @Override
                public int compare(GetAddModel o1, GetAddModel o2) {
                    return Double.compare(Double.parseDouble(o2.getYear()), Double.parseDouble(o1.getYear()));
                }
            });
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvCancelSorting).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getCarTypeAds(String carTypeName) {
        shimmerFrameLayout.startShimmer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adsList.clear();
                adsRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                            String key = adSnapshot.getKey();
                            adsRef.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        GetAddModel ad = snapshot1.getValue(GetAddModel.class);
                                        if (ad.getCar_type().equals(carTypeName)) {
                                            adsList.add(ad);
                                            tvResultsCount.setText(String.valueOf(adsList.size()));
                                        }
                                    }
                                    rvCarTypeSearch.setAdapter(adapter);
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    rvCarTypeSearch.setVisibility(View.VISIBLE);

                                    if (adsList.isEmpty()) {
                                        tvAdsNotAvailable.setVisibility(View.VISIBLE);
                                        tvAdsNotAvailable.setText("We're sorry, but currently, there are no ads available for " + Constant.homeCarType + ".");
                                    } else {
                                        tvAdsNotAvailable.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Error! " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 2000);
    }
}