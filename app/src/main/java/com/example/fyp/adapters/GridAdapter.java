package com.example.fyp.adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fyp.main.MainActivity.NavigationHide;
import static com.example.fyp.main.MainActivity.drawerLayout;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.main.MainActivity.notLoggedIn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fyp.Boarding.MessagesBoardingFragment;
import com.example.fyp.R;
import com.example.fyp.chat.FragmentChatHere;
import com.example.fyp.constant.Constant;
import com.example.fyp.home.HomeCarType;
import com.example.fyp.home.HomeFragment;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.search.SearchNode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    @SuppressLint("StaticFieldLeak")
    public static BottomSheetDialog bottomSheetDialog;
    private boolean isBottomSheetOpen = false;
    private List<GetAddModel> itemList;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isFirstRecyclerView;

    public GridAdapter(List<GetAddModel> itemList, boolean isFirstRecyclerView) {
        this.itemList = itemList;
        this.isFirstRecyclerView = isFirstRecyclerView;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<GetAddModel> filteredList) {
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isFirstRecyclerView) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_search_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_grid_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GetAddModel item = itemList.get(position);
        holder.carMake.setText(item.getMake() + " " + item.getModel());
        holder.variant.setText(item.getVariant());
        holder.pickUpCity.setText(item.getPickup_city());
        holder.rent.setText(item.getRent());

        if (isFirstRecyclerView) {
            holder.tvYear.setText(item.getYear());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBottomSheetOpen) {
                    Constant.usersModel = null;
                    Constant.itemGetModel = item;
                    openBottomSheet(v.getContext(), item);
                    isBottomSheetOpen = true;
                }

            }
        });

        if ("Yes".equals(item.getInsurance())) {
            holder.tvInsuranceStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvInsuranceStatus.setVisibility(View.GONE);
        }

        if (item.getBookingStatus() != null) {
            holder.tvCarBooked.setVisibility(View.VISIBLE);
        } else {
            holder.tvCarBooked.setVisibility(View.GONE);
        }

        if (item.getCarImages() != null && !item.getCarImages().isEmpty()) {
            String image = item.getCarImages().get(0);
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(holder.image);

        } else {
            holder.image.setImageResource(R.drawable.ic_place_holder);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView carMake, variant, pickUpCity, rent, tvInsuranceStatus, tvYear, tvCarBooked;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            carMake = itemView.findViewById(R.id.tvCarName);
            variant = itemView.findViewById(R.id.tv_dummy);
            pickUpCity = itemView.findViewById(R.id.tvCity);
            rent = itemView.findViewById(R.id.tvPrice);
            tvInsuranceStatus = itemView.findViewById(R.id.tvInsuranceStatus);
            image = itemView.findViewById(R.id.ivLamborgini);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvCarBooked = itemView.findViewById(R.id.tvCarBooked);

        }
    }

    @SuppressLint("SetTextI18n")
    private void openBottomSheet(Context context, GetAddModel item) {
        if (context == null) {
            Toast.makeText(context, "Error!.", Toast.LENGTH_SHORT).show();
            return;
        }

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
        Context appContext = context.getApplicationContext();
        @SuppressLint("InflateParams")
        View bottomSheetView = LayoutInflater.from(appContext).inflate(R.layout.renter_contact_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        TextView carNameModelYear = bottomSheetView.findViewById(R.id.tvSheetCarName);
        TextView carVariant = bottomSheetView.findViewById(R.id.tvSheetVariant);
        TextView carPickupCity = bottomSheetView.findViewById(R.id.tvSheetPickupCity);
        TextView carRent = bottomSheetView.findViewById(R.id.tvSheetCarRent);
        TextView carGearType = bottomSheetView.findViewById(R.id.tvSheetGearType);
        TextView engineTypeCapacity = bottomSheetView.findViewById(R.id.tvSheetEngineType);
        TextView driverAvailability = bottomSheetView.findViewById(R.id.tvDriverAvailability);
        TextView carType = bottomSheetView.findViewById(R.id.tvSheetCarType);
        TextView carSeatCapacity = bottomSheetView.findViewById(R.id.tvSheetSeatCapacity);
        TextView simpleDesc = bottomSheetView.findViewById(R.id.tvDescription);
        TextView carDescription = bottomSheetView.findViewById(R.id.tvSheetDescription);
        TextView tvSheetInsuranceStatus = bottomSheetView.findViewById(R.id.tvSheetInsuranceStatus);
        TextView carOwnerName = bottomSheetView.findViewById(R.id.tvSheetCarOwner);
        Button btnContactRenter = bottomSheetView.findViewById(R.id.btnContactRenter);
        TextView tvCarMaintainanceDate = bottomSheetView.findViewById(R.id.tvCarMaintainanceDate);
        ProgressBar contactProgressBar = bottomSheetView.findViewById(R.id.contactProgressBar);

//        Same renter alert visibility
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView cvContactRenter = bottomSheetView.findViewById(R.id.cvContactRenter);
        GifImageView gifMyAdWarning = bottomSheetView.findViewById(R.id.gifMyAdWarning);


        if ("Yes".equals(item.getInsurance())) {
            tvSheetInsuranceStatus.setVisibility(View.VISIBLE);
        } else {
            tvSheetInsuranceStatus.setVisibility(View.GONE);
        }

        ImageSlider imageSlider = bottomSheetView.findViewById(R.id.image_slider);
        ArrayList<SlideModel> remoteImages = new ArrayList<>();
        for (int i = 0; i < item.getCarImages().size(); i++) {
            if (!item.getCarImages().get(i).isEmpty()) {
                switch (i) {
                    case 0:
                        remoteImages.add(new SlideModel(item.getCarImages().get(0), ScaleTypes.CENTER_CROP));
                        break;
                    case 1:
                        remoteImages.add(new SlideModel(item.getCarImages().get(1), ScaleTypes.CENTER_CROP));
                        break;
                    case 2:
                        remoteImages.add(new SlideModel(item.getCarImages().get(2), ScaleTypes.CENTER_CROP));
                        break;
                    case 3:
                        remoteImages.add(new SlideModel(item.getCarImages().get(3), ScaleTypes.CENTER_CROP));
                        break;
                    case 4:
                        remoteImages.add(new SlideModel(item.getCarImages().get(4), ScaleTypes.CENTER_CROP));
                        break;
                    case 5:
                        remoteImages.add(new SlideModel(item.getCarImages().get(5), ScaleTypes.CENTER_CROP));
                        break;
                    case 6:
                        remoteImages.add(new SlideModel(item.getCarImages().get(6), ScaleTypes.CENTER_CROP));
                        break;
                    case 7:
                        remoteImages.add(new SlideModel(item.getCarImages().get(7), ScaleTypes.CENTER_CROP));
                        break;
                    default:
                        Toast.makeText(appContext, "Images not Found", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
        imageSlider.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);
        imageSlider.setImageList(remoteImages, ScaleTypes.CENTER_CROP);

        carNameModelYear.setText(item.getMake() + " " + item.getModel() + " " + item.getYear());
        carVariant.setText(item.getVariant());
        carPickupCity.setText(item.getPickup_city());
        carRent.setText(item.getRent());
        carGearType.setText(item.getTransmission());
        engineTypeCapacity.setText(item.getEngine_type() + " | " + item.getEngine_capacity());
        driverAvailability.setText(item.getDriver_available());
        carType.setText(item.getCar_type());
        tvCarMaintainanceDate.setText(item.getDate());
        carSeatCapacity.setText(item.getSeating_capacity());
        carOwnerName.setText(item.getOwnerName());

        if (!item.getDesc().isEmpty()) {
            simpleDesc.setVisibility(View.VISIBLE);
            carDescription.setVisibility(View.VISIBLE);
            carDescription.setText(item.getDesc());
        } else {
            simpleDesc.setVisibility(View.GONE);
            carDescription.setVisibility(View.GONE);
        }

        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isBottomSheetOpen = false;
            }
        });
        ImageView closeSheet = bottomSheetView.findViewById(R.id.ivCloseSheet);
        closeSheet.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });

        myAdsWarning(context, cvContactRenter, gifMyAdWarning, contactProgressBar);

        SharedPreferences sharedPreferences = ((FragmentActivity) context).getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean onboardingShown = sharedPreferences.getBoolean("onboardingShown", false);

        //Get Current Fragment
        Fragment currentFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        btnContactRenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    notLoggedIn(((FragmentActivity) context).getSupportFragmentManager());
                    bottomSheetDialog.hide();
                    return;
                } else if (onboardingShown) {
                    if (currentFragment instanceof HomeFragment) {
                        addToStack(context, new FragmentChatHere(), "homeFragment");

                    } else if (currentFragment instanceof SearchNode) {
                        addToStack(context, new FragmentChatHere(), "searchFragment");

                    } else if (currentFragment instanceof HomeCarType) {
                        addToStack(context, new FragmentChatHere(), "homeCarTypeFragment");

                    } else {
                        loadFragment(((FragmentActivity) context).getSupportFragmentManager(), new FragmentChatHere());
                    }
                    NavigationHide();
                    bottomSheetDialog.hide();
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                } else {
                    if (currentFragment instanceof HomeFragment) {
                        addToStack(context, new MessagesBoardingFragment(), "homeFragment");

                    } else if (currentFragment instanceof SearchNode) {
                        addToStack(context, new MessagesBoardingFragment(), "searchFragment");

                    } else if (currentFragment instanceof HomeCarType) {
                        addToStack(context, new MessagesBoardingFragment(), "homeCarTypeFragment");

                    }
                    NavigationHide();
                    bottomSheetDialog.hide();
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        });

    }

    private void myAdsWarning(Context context, CardView cvContactRenter, GifImageView gifMyAdWarning, ProgressBar contactProgressBar) {
        String uniqueOwnerPhone = Constant.itemGetModel.getOwnerPhone();
        String UUID = null;
        if (currentUser != null) {
            UUID = currentUser.getUid();
        }
        contactProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String finalUUID = UUID;
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = userSnapshot.child("phone").getValue(String.class);
                    if (phoneNumber != null && phoneNumber.equals(uniqueOwnerPhone)) {
                        String itemClickedId = userSnapshot.getKey();
                        if (Objects.equals(itemClickedId, finalUUID)) {
                            cvContactRenter.setVisibility(View.INVISIBLE);
                            gifMyAdWarning.setVisibility(View.VISIBLE);
                            contactProgressBar.setVisibility(View.GONE);
                        } else {
                            cvContactRenter.setVisibility(View.VISIBLE);
                            gifMyAdWarning.setVisibility(View.GONE);
                            contactProgressBar.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addToStack(Context context, Fragment fragment, String stackTitle) {
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(stackTitle);
        transaction.commit();
    }

}
