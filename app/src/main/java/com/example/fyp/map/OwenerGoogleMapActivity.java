package com.example.fyp.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.example.fyp.R;
import com.example.fyp.main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;


public class OwenerGoogleMapActivity extends FragmentActivity implements OnMapReadyCallback, RouteListener {

    private static final int LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    private Polyline currentPolyline;
    private DatabaseReference databaseReference;
    private DatabaseReference trackedUserReference;
    private List<LatLng> trackedUserPath;
    private boolean isMapAnimated = false;
    private boolean check = false;
    LatLng first = null;

    private ArrayList<Polyline> polylines = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owener_google_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("car_owner_location");
        trackedUserReference = FirebaseDatabase.getInstance().getReference("car_rental_location");

        trackedUserPath = new ArrayList<>();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocation(location);
                    //saveLocationToFirebase(location);
                }
            }
        };

        // Listen for changes in the tracked user's location
        trackedUserReference.child("7746").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GetLocation location = snapshot.getValue(GetLocation.class);
                    if (!check) {
                        updateTrackedUserPath(location);
                        check = true;
                    }
                    //getRoutePoints(new LatLng(location.latitude, location.longitude));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(Location location) {
        if (mMap == null) {
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            if (!isMapAnimated) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                isMapAnimated = true;
            }
        } else {

            currentLocationMarker.setPosition(latLng);

        }
    }

    private void saveLocationToFirebase(Location location) {
        databaseReference.child("0000").setValue(location);
    }

    public void getRoutePoints(LatLng start) {
        if (!isMapAnimated) {
            isMapAnimated = true;
            first = start;
        }

        if (start == null) {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show();
            Log.e("TAG", " latlngs are null");
        } else {
            RouteDrawing routeDrawing = new RouteDrawing.Builder()
                    .context(OwenerGoogleMapActivity.this)  // pass your activity or fragment's context
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this).alternativeRoutes(true)
                    .waypoints(first, start)
                    .build();
            routeDrawing.execute();
        }

    }

    private void updateTrackedUserPath(GetLocation location) {


        if (mMap == null || location == null) {
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        trackedUserPath.add(latLng);

        if (trackedUserPath.size() > 1) {
            if (currentPolyline != null) {
                currentPolyline.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(trackedUserPath)
                    .clickable(false);
            currentPolyline = mMap.addPolyline(polylineOptions);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onRouteFailure(ErrorHandling errorHandling) {

    }

    @Override
    public void onRouteStart() {

    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> arrayList, int index) {
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == index) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing" + index);
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(arrayList.get(index).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = mMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRouteCancelled() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
          /*finish();
          startActivity(new Intent(OwenerGoogleMapActivity.this, MainActivity.class));*/
    }
}