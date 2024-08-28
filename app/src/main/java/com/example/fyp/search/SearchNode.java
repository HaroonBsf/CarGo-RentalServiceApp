package com.example.fyp.search;

import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.splash.SplashScreen.testArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.fyp.home.HomeFragment;
import com.example.fyp.models.GetAddModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchNode extends Fragment {

    TextView tvBackFromSearch, tvAllVehicles, tvNearest, tvReserved, tvNearestCityName;
    View selectedAllAdsBG, selectedNearestBG, selectedReserveBG;
    EditText etSearchCar;
    LinearLayout llSearchNotFound, llNearestLocation;
    ShimmerFrameLayout shimmerLayout;
    RecyclerView rvSearchAds;
    GridAdapter adapter;
    List<GetAddModel> matchingAds = new ArrayList<>();
    List<GetAddModel> bookedAdsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_node, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tvBackFromSearch = view.findViewById(R.id.tvBackFromSearch);
        etSearchCar = view.findViewById(R.id.etSearchCar);
        llSearchNotFound = view.findViewById(R.id.llSearchNotFound);
        llNearestLocation = view.findViewById(R.id.llNearestLocation);
        tvNearestCityName = view.findViewById(R.id.tvNearestCity);

        selectedAllAdsBG = view.findViewById(R.id.selectedAllAdsBG);
        tvAllVehicles = view.findViewById(R.id.tvAllVehicles);

        selectedNearestBG = view.findViewById(R.id.selectedNearestBG);
        tvNearest = view.findViewById(R.id.tvNearest);

        selectedReserveBG = view.findViewById(R.id.selectedReserveBG);
        tvReserved = view.findViewById(R.id.tvReserved);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvSearchAds = view.findViewById(R.id.rvSearchAds);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSearchAds.setLayoutManager(linearLayoutManager);

        getAllAds();
        tvAllVehicles.setOnClickListener(v -> getAllAds());
        tvNearest.setOnClickListener(v -> getNearestAds());
        tvReserved.setOnClickListener(v -> getReservedAds());

        rvSearchAds.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setEdittextFocus(requireContext());
                return false;
            }
        });
        etSearchCar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvBackFromSearch.setOnClickListener(v -> {
            loadFragment(getActivity().getSupportFragmentManager(), new HomeFragment());
            NavigationVisibile();
        });
    }

    private void getReservedAds() {
//        Clear Edit Focus
        etSearchCar.clearFocus();
        etSearchCar.setText("");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchCar.getWindowToken(), 0);

//        Set Visibility
        llSearchNotFound.setVisibility(View.GONE);
        selectedAllAdsBG.setVisibility(View.GONE);
        selectedNearestBG.setVisibility(View.GONE);
        selectedReserveBG.setVisibility(View.VISIBLE);
        tvAllVehicles.setTextColor(Color.BLACK);
        tvNearest.setTextColor(Color.BLACK);
        tvReserved.setTextColor(Color.WHITE);
        llNearestLocation.setVisibility(View.GONE);

        rvSearchAds.setVisibility(View.GONE);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        bookedAdsList.clear();
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("allUserAds");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                            String key = adSnapshot.getKey();
                            adsRef.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        GetAddModel ad = snapshot1.getValue(GetAddModel.class);
                                        if (snapshot1.hasChild("bookingStatus")) {
                                            bookedAdsList.add(ad);
                                            adapter = new GridAdapter(bookedAdsList, true);
                                        }
                                    }
                                    rvSearchAds.setAdapter(adapter);
                                    shimmerLayout.stopShimmer();
                                    rvSearchAds.setVisibility(View.VISIBLE);
                                    shimmerLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
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

        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    private void getNearestAds() {
//        Clear Edit Focus
        etSearchCar.clearFocus();
        etSearchCar.setText("");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchCar.getWindowToken(), 0);

//        Set Visibility
        llSearchNotFound.setVisibility(View.GONE);
        selectedAllAdsBG.setVisibility(View.GONE);
        selectedNearestBG.setVisibility(View.VISIBLE);
        selectedReserveBG.setVisibility(View.GONE);
        tvAllVehicles.setTextColor(Color.BLACK);
        tvNearest.setTextColor(Color.WHITE);
        tvReserved.setTextColor(Color.BLACK);
        llNearestLocation.setVisibility(View.VISIBLE);

        String userLocation = Constant.city;
        if (userLocation != null) {
            tvNearestCityName.setText(userLocation);
        } else {
            tvNearestCityName.setText("City");
        }

        rvSearchAds.setVisibility(View.GONE);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        matchingAds.clear();
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("allUserAds");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                            String key = adSnapshot.getKey();
                            adsRef.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        GetAddModel ad = snapshot1.getValue(GetAddModel.class);
                                        String pickupCity = ad.getPickup_city();

                                        if (pickupCity != null && pickupCity.equals(userLocation)) {
                                            matchingAds.add(ad);
                                            adapter = new GridAdapter(matchingAds, true);
                                        }
                                    }
                                    rvSearchAds.setAdapter(adapter);
                                    shimmerLayout.stopShimmer();
                                    rvSearchAds.setVisibility(View.VISIBLE);
                                    shimmerLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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
        }, 1000);
    }

    private void getAllAds() {
//        Clear Edit Focus
        etSearchCar.clearFocus();
        etSearchCar.setText("");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchCar.getWindowToken(), 0);

//        Set Visibility
        llSearchNotFound.setVisibility(View.GONE);
        selectedAllAdsBG.setVisibility(View.VISIBLE);
        selectedNearestBG.setVisibility(View.GONE);
        selectedReserveBG.setVisibility(View.GONE);
        tvAllVehicles.setTextColor(Color.WHITE);
        tvNearest.setTextColor(Color.BLACK);
        tvReserved.setTextColor(Color.BLACK);
        llNearestLocation.setVisibility(View.GONE);

        adapter = new GridAdapter(testArray, true);
        rvSearchAds.setVisibility(View.GONE);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvSearchAds.setAdapter(adapter);
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                rvSearchAds.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    private void filter(String text) {
        List<GetAddModel> filteredList = new ArrayList<>();

        for (GetAddModel item : testArray) {
            String search = item.getMake().toLowerCase() + " " + item.getModel().toLowerCase();

            if (search.contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            llSearchNotFound.setVisibility(View.VISIBLE);
            rvSearchAds.setVisibility(View.GONE);
        } else {
            llSearchNotFound.setVisibility(View.GONE);
            rvSearchAds.setVisibility(View.VISIBLE);
            adapter.filterList(filteredList);
        }
    }

    private void setEdittextFocus(Context context) {
        etSearchCar.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchCar.getWindowToken(), 0);
    }


}