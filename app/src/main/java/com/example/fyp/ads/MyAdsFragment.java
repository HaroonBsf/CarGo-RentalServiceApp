package com.example.fyp.ads;

import static com.example.fyp.main.MainActivity.NavigationHide;
import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.dataProgressBar;
import static com.example.fyp.main.MainActivity.dataProgressView;
import static com.example.fyp.main.MainActivity.drawerLayout;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.main.MainActivity.lockNavigationDrawerAndBottomNavigation;
import static com.example.fyp.main.MainActivity.unlockNavigationDrawerAndBottomNavigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.adapters.PrivateAdsAdapter;
import com.example.fyp.callback.OnItemClickCallback;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.PrivateAdsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdsFragment extends Fragment {

    TextView myRentalCars, addNewCar;
    FragmentManager manager;
    FragmentTransaction transaction;
    LinearLayout noCarsAdded;
    RecyclerView rvPrivateAds;
    PrivateAdsAdapter privateAdsAdapter;
    ArrayList<PrivateAdsModel> privateArray = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_ads, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myRentalCars = view.findViewById(R.id.tvMyRentalCars);
        addNewCar = view.findViewById(R.id.tvAddNewCar);
        if (Constant.userData != null) {
            myRentalCars.setText(Constant.userData.company);
        } else {
            myRentalCars.setText("No Services");

        }
        rvPrivateAds = view.findViewById(R.id.rvPrivateAds);
        noCarsAdded = view.findViewById(R.id.llNoCarsAdded);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvPrivateAds.setLayoutManager(gridLayoutManager);

        addNewCar.setEnabled(true);
        addNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCar.setTextColor(Color.BLACK);
                loadFragmentWithBackStack(new AddCar(), "MyAdsBackStack");
                NavigationHide();
                showAgreeCancelDialog();

            }
        });
        privateArray.clear();
        dataProgressBar.setVisibility(View.VISIBLE);
        dataProgressView.setVisibility(View.VISIBLE);
        addNewCar.setEnabled(false);
        lockNavigationDrawerAndBottomNavigation();

        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(UUID).child("userAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        PrivateAdsModel adsData = user.getValue(PrivateAdsModel.class);
                        privateArray.add(adsData);

                        privateAdsAdapter = new PrivateAdsAdapter(getContext(), privateArray, new OnItemClickCallback() {
                            @Override
                            public void onItemClick(int position) {
                                loadFragment(requireActivity().getSupportFragmentManager(), new UpdateAds());
                            }
                        });
                        rvPrivateAds.setAdapter(privateAdsAdapter);
                        dataProgressBar.setVisibility(View.GONE);
                        dataProgressView.setVisibility(View.GONE);
                        addNewCar.setEnabled(true);
                        unlockNavigationDrawerAndBottomNavigation();

                    }
                    noCarsAdded.setVisibility(View.GONE);

                } else {
                    noCarsAdded.setVisibility(View.VISIBLE);
                    dataProgressBar.setVisibility(View.GONE);
                    dataProgressView.setVisibility(View.GONE);
                    addNewCar.setEnabled(true);
                    unlockNavigationDrawerAndBottomNavigation();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                noCarsAdded.setVisibility(View.VISIBLE);
                dataProgressBar.setVisibility(View.GONE);
                dataProgressView.setVisibility(View.GONE);
                addNewCar.setEnabled(true);
                unlockNavigationDrawerAndBottomNavigation();

            }
        });


    }

    private void loadFragmentWithBackStack(Fragment fragment, String backStackTag) {
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(backStackTag);
        transaction.commit();
    }

    private void showAgreeCancelDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.NetworkAlertDialog);
        dialog.setContentView(R.layout.terms_of_service);
        dialog.setCancelable(false);
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);

        Button btnAgree = dialog.findViewById(R.id.btnAgree);
        Button cancel = dialog.findViewById(R.id.btnCancel);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack("MyAdsBackStack", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                NavigationVisibile();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

}