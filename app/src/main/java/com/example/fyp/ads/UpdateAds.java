package com.example.fyp.ads;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fyp.ads.AddCar.getAllUserAds;
import static com.example.fyp.main.MainActivity.NavigationHide;
import static com.example.fyp.main.MainActivity.dataProgressBar;
import static com.example.fyp.main.MainActivity.dataProgressView;
import static com.example.fyp.main.MainActivity.drawerLayout;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fyp.Boarding.TrackingBoard;
import com.example.fyp.R;
import com.example.fyp.constant.Constant;
import com.example.fyp.map.GetLocation;
import com.example.fyp.map.MapsFragment;
import com.example.fyp.map.OwenerGoogleMapActivity;
import com.example.fyp.map.RandomCodeGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class UpdateAds extends Fragment {

    TextView tvPrivateBookingStatus, tvPrivateCarNameModel, tvPirvateVariantYear, tvPrivatePickupCity, tvPrivateCarRent, tvPrivateGearType, tvPrivateEngineType, tvPrivateDriverAvailability, tvPrivateCarType, tvSheetSeatCapacity, tvPrivateCarMaintainanceDate, tvPrivateCarOwner, tvTitleDescription, tvPrivateDescription, tvPrivateInsuranceStatus;
    ImageSlider privateImageSlider;
    Button btnEdit, btnSetAsBooked, btnStartTrack, btnCancelBooking;
    LinearLayout llUnBookedState, llBookedState;
    GifImageView gifBookingTimer;
    private DatabaseReference databaseReference;
    int code;
    DialogInterface dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_ads, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String id = Constant.itemClicked;

        checkBookingStatus(id);

        tvPrivateCarNameModel = view.findViewById(R.id.tvPrivateCarNameModel);
        tvPirvateVariantYear = view.findViewById(R.id.tvPirvateVariantYear);
        tvPrivatePickupCity = view.findViewById(R.id.tvPrivatePickupCity);
        tvPrivateCarRent = view.findViewById(R.id.tvPrivateCarRent);
        tvPrivateGearType = view.findViewById(R.id.tvPrivateGearType);
        tvPrivateEngineType = view.findViewById(R.id.tvPrivateEngineType);
        tvPrivateDriverAvailability = view.findViewById(R.id.tvPrivateDriverAvailability);
        tvPrivateCarType = view.findViewById(R.id.tvPrivateCarType);
        tvSheetSeatCapacity = view.findViewById(R.id.tvSheetSeatCapacity);
        tvPrivateCarOwner = view.findViewById(R.id.tvPrivateCarOwner);
        tvTitleDescription = view.findViewById(R.id.tvTitleDescription);
        tvPrivateDescription = view.findViewById(R.id.tvPrivateDescription);
        tvPrivateInsuranceStatus = view.findViewById(R.id.tvPrivateInsuranceStatus);
        privateImageSlider = view.findViewById(R.id.privateImageSlider);
        tvPrivateCarMaintainanceDate = view.findViewById(R.id.tvPrivateCarMaintainanceDate);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnSetAsBooked = view.findViewById(R.id.btnSetAsBooked);
        btnCancelBooking = view.findViewById(R.id.btnCancelBooking);
        btnStartTrack = view.findViewById(R.id.btnStartTrack);
//      Booking View
        llUnBookedState = view.findViewById(R.id.llUnBookedState);
        llBookedState = view.findViewById(R.id.llBookedState);
        gifBookingTimer = view.findViewById(R.id.gifBookingTimer);
        tvPrivateBookingStatus = view.findViewById(R.id.tvPrivateBookingStatus);


        if ("Yes".equals(Constant.privateItemModel.getInsurance())) {
            tvPrivateInsuranceStatus.setVisibility(View.VISIBLE);
        } else {
            tvPrivateInsuranceStatus.setVisibility(View.GONE);
        }

        ArrayList<SlideModel> remoteImages = new ArrayList<>();
        for (int i = 0; i < Constant.privateItemModel.getCarImages().size(); i++) {
            if (!Constant.privateItemModel.getCarImages().get(i).isEmpty()) {
                switch (i) {
                    case 0:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(0), ScaleTypes.CENTER_CROP));
                        break;
                    case 1:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(1), ScaleTypes.CENTER_CROP));
                        break;
                    case 2:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(2), ScaleTypes.CENTER_CROP));
                        break;
                    case 3:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(3), ScaleTypes.CENTER_CROP));
                        break;
                    case 4:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(4), ScaleTypes.CENTER_CROP));
                        break;
                    case 5:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(5), ScaleTypes.CENTER_CROP));
                        break;
                    case 6:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(6), ScaleTypes.CENTER_CROP));
                        break;
                    case 7:
                        remoteImages.add(new SlideModel(Constant.privateItemModel.getCarImages().get(7), ScaleTypes.CENTER_CROP));
                        break;
                    default:
                        Toast.makeText(getContext(), "Images not Found", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
        privateImageSlider.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);
        privateImageSlider.setImageList(remoteImages, ScaleTypes.CENTER_CROP);

        tvPrivateCarNameModel.setText(Constant.privateItemModel.getMake() + " " + Constant.privateItemModel.getModel());
        tvPirvateVariantYear.setText(Constant.privateItemModel.getVariant() + " " + Constant.privateItemModel.getYear());
        tvPrivatePickupCity.setText(Constant.privateItemModel.getPickup_city());
        tvPrivateCarRent.setText(Constant.privateItemModel.getRent());
        tvPrivateGearType.setText(Constant.privateItemModel.getTransmission());
        tvPrivateEngineType.setText(Constant.privateItemModel.getEngine_type() + " | " + Constant.privateItemModel.getEngine_capacity());
        tvPrivateDriverAvailability.setText(Constant.privateItemModel.getDriver_available());
        tvPrivateCarType.setText(Constant.privateItemModel.getCar_type());
        tvPrivateCarMaintainanceDate.setText(Constant.privateItemModel.getDate());
        tvSheetSeatCapacity.setText(Constant.privateItemModel.getSeating_capacity());
        tvPrivateCarOwner.setText(Constant.userData.name);

        if (!Constant.privateItemModel.getDesc().isEmpty()) {
            tvTitleDescription.setVisibility(View.VISIBLE);
            tvPrivateDescription.setVisibility(View.VISIBLE);
            tvPrivateDescription.setText(Constant.privateItemModel.getDesc());
        } else {
            tvTitleDescription.setVisibility(View.GONE);
            tvPrivateDescription.setVisibility(View.GONE);
        }

        btnEdit.setOnClickListener(v -> {
            loadFragment(getActivity().getSupportFragmentManager(), new EditAds());
        });
        btnSetAsBooked.setOnClickListener(v -> setAsBooked());
        btnCancelBooking.setOnClickListener(v -> cancelBooking());
        btnStartTrack.setOnClickListener(v -> loadTrackingFrag());

    }

    private void cancelBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        builder.setTitle("Cancel Booking");
        builder.setMessage("Are you sure you want to cancel this booking? After this action, your car will be removed from the booking state.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelBooking(dialog);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        dialog.show();
    }

    private void cancelBooking(DialogInterface dialog) {
        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = Constant.itemClicked;


        dataProgressView.setVisibility(View.VISIBLE);
        dataProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(UUID).child("userAds").child(itemId).child("bookingStatus").removeValue();

        reference.child("allUserAds").child(UUID).child(itemId).child("bookingStatus").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    llUnBookedState.setVisibility(View.VISIBLE);
                    llBookedState.setVisibility(View.GONE);
                    gifBookingTimer.setVisibility(View.GONE);
                    tvPrivateBookingStatus.setVisibility(View.GONE);
                    getAllUserAds();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadFragment(getActivity().getSupportFragmentManager(), new EditAds());
                            dataProgressView.setVisibility(View.GONE);
                            dataProgressBar.setVisibility(View.GONE);
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    dataProgressView.setVisibility(View.GONE);
                    dataProgressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });
    }

    private void checkBookingStatus(String id) {
        dataProgressView.setVisibility(View.VISIBLE);
        dataProgressBar.setVisibility(View.VISIBLE);

        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UUID).child("userAds");
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("bookingStatus")) {
                    llUnBookedState.setVisibility(View.INVISIBLE);
                    llBookedState.setVisibility(View.VISIBLE);
                    gifBookingTimer.setVisibility(View.VISIBLE);
                    tvPrivateBookingStatus.setVisibility(View.VISIBLE);
                    dataProgressView.setVisibility(View.GONE);
                    dataProgressBar.setVisibility(View.GONE);
                } else {
                    llUnBookedState.setVisibility(View.VISIBLE);
                    llBookedState.setVisibility(View.GONE);
                    gifBookingTimer.setVisibility(View.GONE);
                    tvPrivateBookingStatus.setVisibility(View.GONE);
                    dataProgressView.setVisibility(View.GONE);
                    dataProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTrackingFrag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        builder.setTitle("Confirm Tracking");
        builder.setMessage("You are about to track your car. Would you like to begin tracking?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMap();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        dialog.show();
    }

    private void loadMap() {
        code = RandomCodeGenerator.generateRandomCode();

        Map<String, Object> updates = new HashMap<>();
        updates.put(String.valueOf(code), "");

        FirebaseDatabase.getInstance().getReference().child("car_rental_location").updateChildren(updates);

        setFunction();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        builder.setTitle("OTP CODE");
        builder.setMessage("Ask your driver to enter this OTP code : " + code);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog = dialog;
                dialog.dismiss();

                FirebaseDatabase.getInstance().getReference().child("car_rental_location").child(String.valueOf(code)).removeValue();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        dialog.show();


        //startActivity(new Intent(getActivity().getBaseContext(), OwenerGoogleMapActivity.class));

//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyTrackPref", MODE_PRIVATE);
//        boolean onboardingShown = sharedPreferences.getBoolean("onboardingTrackShown", false);
//
//        if (onboardingShown) {
//            loadFragment(getActivity().getSupportFragmentManager(), new MapsFragment());
//            NavigationHide();
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//
//        } else {
//            loadFragment(getActivity().getSupportFragmentManager(), new TrackingBoard());
//            NavigationHide();
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        }
    }

    private void setFunction() {
        databaseReference = FirebaseDatabase.getInstance().getReference("car_rental_location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if (snapshot.child(String.valueOf(code)).child("latitude").exists()) {
                        try {
                            dialog.cancel();
                        } catch (NullPointerException e) {
                        }
                        startActivity(new Intent(getActivity().getBaseContext(), OwenerGoogleMapActivity.class));
                        //dialog.cancel();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAsBooked() {
        Dialog dialog = new Dialog(getContext(), R.style.NetworkAlertDialog);
        dialog.setContentView(R.layout.confirm_booking_dialog);
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);

        Button btnConfirmBooking = dialog.findViewById(R.id.btnConfirmBooking);
        Button btnCancelBooking = dialog.findViewById(R.id.btnCancelBooking);

        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String itemId = Constant.itemClicked;

                dataProgressView.setVisibility(View.VISIBLE);
                dataProgressBar.setVisibility(View.VISIBLE);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(UUID).child("userAds").child(itemId).child("bookingStatus").setValue("Booked");

                reference.child("allUserAds").child(UUID).child(itemId).child("bookingStatus").setValue("Booked").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            llUnBookedState.setVisibility(View.INVISIBLE);
                            llBookedState.setVisibility(View.VISIBLE);
                            gifBookingTimer.setVisibility(View.VISIBLE);
                            tvPrivateBookingStatus.setVisibility(View.VISIBLE);
                            getAllUserAds();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadFragment(getActivity().getSupportFragmentManager(), new MyAdsFragment());
                                    dataProgressView.setVisibility(View.GONE);
                                    dataProgressBar.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } else {
                            Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dataProgressView.setVisibility(View.GONE);
                            dataProgressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.dismiss();

            }
        });

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}