package com.example.fyp.main;

import static com.example.fyp.adapters.GridAdapter.bottomSheetDialog;
import static com.example.fyp.ads.AddCar.uploadingAdsProgress;
import static com.example.fyp.splash.SplashScreen.testArray;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fyp.Boarding.MessagesBoardingFragment;
import com.example.fyp.Boarding.TrackingBoard;
import com.example.fyp.Login.LoginScreen;
import com.example.fyp.Login.Login_SignupFragment;
import com.example.fyp.OtherFragments.HelpFragment;
import com.example.fyp.OtherFragments.PrivacyPolicy;
import com.example.fyp.OtherFragments.SettingsFragment;
import com.example.fyp.R;
import com.example.fyp.adapters.LocationSliderAdapter;
import com.example.fyp.ads.AddCar;
import com.example.fyp.ads.EditAds;
import com.example.fyp.ads.MyAdsFragment;
import com.example.fyp.ads.UpdateAds;
import com.example.fyp.callback.CallbackData;
import com.example.fyp.chat.AccessToken;
import com.example.fyp.chat.FragmentChatHere;
import com.example.fyp.chat.TravelFragment;
import com.example.fyp.chat.UsersFragment;
import com.example.fyp.constant.Constant;
import com.example.fyp.home.HomeCarType;
import com.example.fyp.home.HomeFragment;
import com.example.fyp.home.ProfileFragment;
import com.example.fyp.models.LocationSliderModel;
import com.example.fyp.models.ReadWriteUserDetails;
import com.example.fyp.notifications.Token;
import com.example.fyp.search.SearchNode;
import com.example.fyp.util.Util;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    public LocationSliderAdapter locationSliderAdapter;
    private List<LocationSliderModel> locationSliderModel;
    private Handler sliderHandler;
    public static DrawerLayout drawerLayout;
    NavigationView navigationView;
    public static BottomNavigationView bottomNavigationView;
    public static BottomAppBar bottomAppBar;
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    public static FloatingActionButton fab;
    TextView headerUsername, headerEmail;
    EditText companyName;
    ImageView headerProfile, icNextProfile;
    ConstraintLayout headerProfileDetails;
    ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar dataProgressBar;
    @SuppressLint("StaticFieldLeak")
    public static View dataProgressView;
    View view;
    Menu navMenu;
    MenuItem logoutItem;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 100;
    private boolean doubleBackToExitPressedOnce = false;
    GifImageView ivUpdateLocation, ivLocationLoad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        navMenu = navigationView.getMenu();
        logoutItem = navMenu.findItem(R.id.logout);
        progressBar = findViewById(R.id.pbProgressBar);
        view = findViewById(R.id.pbBackground);
        ivUpdateLocation = findViewById(R.id.ivUpdateLocation);
        ivLocationLoad = findViewById(R.id.ivLocationLoad);
        viewPager = findViewById(R.id.viewPagerLocation);
        dataProgressBar = findViewById(R.id.dataProgressBar);
        dataProgressView = findViewById(R.id.dataProgressView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d("FCM", "Token: " + token);
            if (currentUser != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                Token token1 = new Token(token);
                reference.child(currentUser.getUid()).setValue(token1);
            }
        });

//        Window window = getWindow();
//        window.setStatusBarColor(getResources().getColor(R.color.app_theme_color));
//        window.setNavigationBarColor(Color.TRANSPARENT);
//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        View headerView = navigationView.getHeaderView(0);
        headerUsername = headerView.findViewById(R.id.tvHeaderUserName);
        headerEmail = headerView.findViewById(R.id.tvHeaderUserEmail);
        headerProfile = headerView.findViewById(R.id.ivHeaderProfile);
        icNextProfile = headerView.findViewById(R.id.ivHeaderProfileDetails);
        headerProfileDetails = headerView.findViewById(R.id.clHeaderDetail);


        fab.setEnabled(true);
        sliderHandler = new Handler();

        getCurrentLocation();
        updateLocation();
        ivUpdateLocation.setOnClickListener(v -> {
            ivLocationLoad.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getCurrentLocation();
                    ivLocationLoad.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                }
            }, 5000);
        });


        if (currentUser != null) {
            String UUID = currentUser.getUid();

            database.getReference("users").child(UUID).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String image = snapshot.getValue(String.class);
                    Picasso.get()
                            .load(image)
                            .placeholder(R.drawable.profile_placeholder)
                            .into(headerProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            if (Constant.userData != null) {
                headerUsername.setText(Constant.userData.name);
                headerEmail.setText(Constant.userData.email);
            }
        } else {
            headerUsername.setText("Guest");
            headerEmail.setText("");
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottomNavigationView.setBackground(null);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (currentUser == null) {

                    if (itemId == R.id.home) {
                        loadFragment(getSupportFragmentManager(), new HomeFragment());
                        return true;
                    }
                    notLoggedIn(getSupportFragmentManager());

                } else {

                    String UUID = currentUser.getUid();
                    DatabaseReference databaseReference = database.getReference("users").child(UUID);

                    if (itemId == R.id.home) {
                        loadFragment(getSupportFragmentManager(), new HomeFragment());
                        return true;
                    } else if (itemId == R.id.myAds) {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String myCompanyName = snapshot.child("company").getValue(String.class);

                                if (!(myCompanyName == null || myCompanyName.isEmpty())) {
                                    loadFragment(getSupportFragmentManager(), new MyAdsFragment());
                                    return;

                                } else {
                                    showAlertDialog();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;

                    } else if (itemId == R.id.chats) {
                        loadFragment(getSupportFragmentManager(), new UsersFragment());
                        return true;
                    } else if (itemId == R.id.chat) {
                        loadFragment(getSupportFragmentManager(), new TravelFragment());
                        return true;
                    }
                }
                return false;
            }
        });
        loadFragment(getSupportFragmentManager(), new HomeFragment());

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentWithNullActionLaunch")
            @Override
            public void onClick(View v) {
                loadFragment(getSupportFragmentManager(), new SearchNode());
                NavigationHide();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        headerProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser != null) {
                    loadFragment(getSupportFragmentManager(), new ProfileFragment());
                    NavigationHide();
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                } else {
                    notLoggedIn(getSupportFragmentManager());
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (currentUser == null) {
                    notLoggedIn(getSupportFragmentManager());

                } else {

                    if (id == R.id.settings) {
                        loadFragment(getSupportFragmentManager(), new SettingsFragment());
                    } else if (id == R.id.help) {
                        loadFragment(getSupportFragmentManager(), new HelpFragment());
                        NavigationHide();
                    } else if (id == R.id.privacyPolicy) {
                        loadFragment(getSupportFragmentManager(), new PrivacyPolicy());
                    } else if (id == R.id.invite) {
                        Toast.makeText(MainActivity.this, "Good Luck", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.logout) {
                        drawerLayout.closeDrawer(GravityCompat.START);

                        progressBar.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            view.setVisibility(View.GONE);
                            FirebaseDatabase.getInstance().getReference("users").
                                    child(currentUser.getUid()).
                                    child("status").
                                    setValue("offline");
                            FirebaseAuth.getInstance().signOut();

                            Constant.userData = null;

                            headerUsername.setText("Guest");
                            headerEmail.setText("");
                            headerProfile.setImageResource(R.drawable.profile_placeholder);
                            bottomNavigationView.setSelectedItemId(R.id.home);
                            loadFragment(getSupportFragmentManager(), new HomeFragment());

                            startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                            finish();
                            //startActivity(new Intent(getApplicationContext(), LoginScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        }, 2000);
                        return true;

                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                updateNavigationMenu(currentUser);
            }
        };
        mAuth.addAuthStateListener(authStateListener);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void updateLocation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = sharedPreferences.getBoolean("isTargetFirstTime", true);
        if (isFirstTime) {
            String title = "Update Your Location";
            String desc = "Stay Connected with Real-Time Location Updates. Tap here to update your current location.";

            TapTargetView.showFor(this,
                    TapTarget.forView(ivUpdateLocation, title, desc)
                            .outerCircleColor(R.color.white)
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.black)
                            .titleTextSize(18)
                            .titleTextColor(R.color.black)
                            .descriptionTextSize(13)
                            .descriptionTextColor(R.color.dummy_black)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.black)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .targetRadius(40),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            getCurrentLocation();
                            sharedPreferences.edit().putBoolean("isTargetFirstTime", false).apply();
                        }
                    });
        }
    }

    private void showAlertDialog() {
        Dialog dialog = new Dialog(this, R.style.NetworkAlertDialog);
        dialog.setContentView(R.layout.account_info_dialog);
        dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);

        TextView cancel = dialog.findViewById(R.id.tvCancel);
        TextView btnSwitch = dialog.findViewById(R.id.tvSwitch);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.switch_to_company_dialog);
                companyName = dialog.findViewById(R.id.etSwitchToCompany);
                MaterialButton create = dialog.findViewById(R.id.btnCreate);
                ProgressBar createProgress = dialog.findViewById(R.id.createProgress);

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String company = companyName.getText().toString().trim();

                        if (company.isEmpty()) {
                            companyName.setError("Name required");
                            companyName.requestFocus();
                            return;
                        }
                        hideKeyboard();

                        createProgress.setVisibility(View.VISIBLE);
                        create.setVisibility(View.INVISIBLE);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                .getReference("users");
                        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                databaseReference.child(UUID).child("company")
                                        .setValue(company).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    showSuccessDialog();
                                                    Util.getUserDataOnline(new CallbackData() {
                                                        @Override
                                                        public void getUserCallback(ReadWriteUserDetails user) {
                                                            Constant.userData = user;
                                                            loadFragment(getSupportFragmentManager(), new MyAdsFragment());


                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(MainActivity.this, "Failed to convert", Toast.LENGTH_SHORT).show();
                                                }

                                                createProgress.setVisibility(View.GONE);
                                                create.setVisibility(View.VISIBLE);

                                                dialog.dismiss();

                                            }
                                        });
                            }
                        }, 2000);

                    }
                });
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showSuccessDialog() {
        AlertDialog.Builder successBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View successDialogView = inflater.inflate(R.layout.success_dialog, null);

        AlertDialog successDialog = successBuilder.create();
        successDialog.setView(successDialogView, 0, 0, 0, 0);
        successDialog.setCancelable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                successDialog.dismiss();
            }
        }, 4000);
        successDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        successDialog.show();

        int dialogWidth = getApplicationContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(successDialog.getWindow().getAttributes());
        layoutParams.width = dialogWidth;
        successDialog.getWindow().setAttributes(layoutParams);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {

            inputMethodManager.hideSoftInputFromWindow(companyName.getWindowToken(), 0);
        }
    }

    private void getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission
                        (this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            if (location != null) {
                                updateCityLocation(location);

                            } else {
                                Toast.makeText(MainActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        } else {
            requestPermission();

        }

    }

    private void updateCityLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        locationSliderModel = new ArrayList<>();
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            double latitude = addresses.get(0).getLatitude();
            double longitude = (float) addresses.get(0).getLongitude();
            String city = addresses.get(0).getLocality();
            Constant.latitude = latitude;
            Constant.longitude = longitude;
            Constant.city = city;


            locationSliderModel.add(new LocationSliderModel("Country", addresses.get(0).getCountryName()));
            locationSliderModel.add(new LocationSliderModel("Area", addresses.get(0).getAdminArea()));
            locationSliderModel.add(new LocationSliderModel("City", city));

        } catch (IOException e) {
            e.printStackTrace();
        }
        locationSliderAdapter = new LocationSliderAdapter(locationSliderModel, viewPager);
        viewPager.setAdapter(locationSliderAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {

                int pageWidth = page.getWidth();

                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 0) {
                    page.setAlpha(1 + position);
                    page.setTranslationX(pageWidth * -position);
                    page.setScaleX(1 + position);
                    page.setScaleY(1 + position);
                } else if (position <= 1) {
                    page.setAlpha(1 - position);
                    page.setTranslationX(pageWidth * -position);
                    float scaleFactor = 0.75f + (1 - 0.75f) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                } else {
                    page.setAlpha(0f);
                }
            }
        });
        viewPager.setPageTransformer(compositePageTransformer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });

    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);

        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 5000);

        status("online");
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_ACCESS_LOCATION
        );

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                getCurrentLocation();

            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void updateNavigationMenu(FirebaseUser currentUser) {

        if (currentUser != null) {
            logoutItem.setVisible(true);
            icNextProfile.setVisibility(View.VISIBLE);

        } else {
            logoutItem.setVisible(false);
            icNextProfile.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(authStateListener);
    }

    public static void notLoggedIn(FragmentManager manager) {
        manager.beginTransaction()
                .replace(R.id.fragment_container, new Login_SignupFragment())
                .commit();

        NavigationHide();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void loadFragment(FragmentManager manager, Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();


    }

    public static void NavigationVisibile() {
        toolbar.setVisibility(View.VISIBLE);
        bottomAppBar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        toolbar.animate().translationY(0).setDuration(300).start();
        bottomAppBar.animate().translationY(0).setDuration(300).start();

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static void NavigationHide() {
        toolbar.animate().translationY(-toolbar.getHeight()).setDuration(300).withEndAction(() -> {
            toolbar.setVisibility(View.GONE);
        }).start();

        bottomAppBar.animate().translationY(bottomAppBar.getHeight()).setDuration(300).withEndAction(() -> {
            bottomAppBar.setVisibility(View.GONE);
        }).start();

        fab.animate().translationY(fab.getHeight()).setDuration(300).withEndAction(() -> {
            fab.setVisibility(View.GONE);
        }).start();
    }

    public static void disableBottomNavigation(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(false);
            if (child instanceof ViewGroup) {
                disableBottomNavigation((ViewGroup) child);
            }
        }
    }

    public static void enableBottomNavigation(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(true);
            if (child instanceof ViewGroup) {
                enableBottomNavigation((ViewGroup) child);
            }
        }
    }

    public static void lockNavigationDrawerAndBottomNavigation() {
        disableBottomNavigation(bottomNavigationView);
        fab.setEnabled(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void unlockNavigationDrawerAndBottomNavigation() {
        enableBottomNavigation(bottomNavigationView);
        fab.setEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onBackPressed() {
        if (dataProgressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else if (currentFragment instanceof AddCar) {
            if (dataProgressBar.getVisibility() == View.VISIBLE || uploadingAdsProgress.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, "Please wait ...", Toast.LENGTH_SHORT).show();
                return;
            } else {
                getSupportFragmentManager().popBackStack("MyAdsBackStack", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                NavigationVisibile();
            }
        } else if (currentFragment instanceof UpdateAds) {
            loadFragment(getSupportFragmentManager(), new MyAdsFragment());

        } else if (currentFragment instanceof Login_SignupFragment
                || currentFragment instanceof MyAdsFragment
                || currentFragment instanceof HelpFragment
                || currentFragment instanceof UsersFragment
                || currentFragment instanceof TravelFragment
                || currentFragment instanceof ProfileFragment
                || currentFragment instanceof SearchNode
                || currentFragment instanceof HomeCarType) {

            loadFragment(getSupportFragmentManager(), new HomeFragment());
            NavigationVisibile();
            bottomNavigationView.setSelectedItemId(R.id.home);

        } else if (currentFragment instanceof MessagesBoardingFragment
                || currentFragment instanceof FragmentChatHere) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 1);
                String fragmentName = backEntry.getName();
                if ("homeFragment".equals(fragmentName)) {
                    fragmentManager.popBackStackImmediate("homeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    bottomSheetDialog.show();
                    NavigationVisibile();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    return;
                } else if ("searchFragment".equals(fragmentName)) {
                    fragmentManager.popBackStackImmediate("searchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return;
                } else if ("homeCarTypeFragment".equals(fragmentName)) {
                    fragmentManager.popBackStackImmediate("homeCarTypeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return;
                } else if ("UsersFragment".equals(fragmentName)) {
                    fragmentManager.popBackStackImmediate("UsersFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    NavigationVisibile();
                    return;
                }
            }

        } else if (currentFragment instanceof TrackingBoard
                || currentFragment instanceof EditAds) {
            loadFragment(getSupportFragmentManager(), new UpdateAds());
            NavigationVisibile();

        } else if (doubleBackToExitPressedOnce) {
            testArray.clear();          //      To Solve error temporarily
            finishAffinity();
            super.onBackPressed();
            finish();

        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void status(String status) {
        if (currentUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        }
    }

}