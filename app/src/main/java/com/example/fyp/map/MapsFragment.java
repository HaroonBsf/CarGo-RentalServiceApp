package com.example.fyp.map;

import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.example.fyp.ads.UpdateAds;
import com.example.fyp.constant.Constant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    ImageView ivMapLayers, ivMoveToCurrentLocation, ivZoomToDefault, ivBackToUpdate;
    View defaultMapSelectedView, defaultMapView, satelliteMapSelectedView, satelliteMapView, terrainMapSelectedView, terrainMapView, hybridMapSelectedView, hybridMapView;
    TextView tvDefault, tvSatellite, tvTerrain, tvHybrid;
    private boolean isBottomSheetOpen = false;
    double latitude, longitude;
    String city;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        ivMapLayers = view.findViewById(R.id.ivMapLayers);
        ivMoveToCurrentLocation = view.findViewById(R.id.ivMoveToCurrentLocation);
        ivZoomToDefault = view.findViewById(R.id.ivZoomToDefault);
        ivBackToUpdate = view.findViewById(R.id.ivBackToUpdate);

        latitude = Constant.latitude;
        longitude = Constant.longitude;
        city = Constant.city;

        mapFragment.getMapAsync(this);
        ivMapLayers.setOnClickListener(v -> openMapTypeSheet());
        ivBackToUpdate.setOnClickListener(v -> exitTrackingDialog());

    }

    private void exitTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Exit");
        builder.setMessage("Are you sure you want to stop tracking?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadFragment(getActivity().getSupportFragmentManager(), new UpdateAds());
                NavigationVisibile();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(city);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));

        mMap.getUiSettings().setCompassEnabled(false);
        ivMoveToCurrentLocation.setOnClickListener(v -> moveToCurrentLocation(latLng));
        ivZoomToDefault.setOnClickListener(v -> zoomToDefault(latLng));


    }

    private void zoomToDefault(LatLng latLng) {
        CameraPosition defaultCameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(defaultCameraPosition), 1000, null);
    }

    private void moveToCurrentLocation(LatLng target) {
        float zoomLevel = 17f;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoomLevel)
                .tilt(45)
                .bearing(90)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

    }

    @SuppressLint("MissingInflatedId")
    private void openMapTypeSheet() {
        BottomSheetDialog bottomSheetMapType = new BottomSheetDialog(getContext());
        bottomSheetMapType.getWindow().getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
        @SuppressLint("InflateParams")
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.map_types_sheet, null);
        bottomSheetMapType.setContentView(bottomSheetView);

        ImageView ivCloseMapType = bottomSheetView.findViewById(R.id.ivCloseMapType);
        CardView defaultMap = bottomSheetView.findViewById(R.id.defaultMap);
        CardView satelliteMap = bottomSheetView.findViewById(R.id.satelliteMap);
        CardView terrainMap = bottomSheetView.findViewById(R.id.terrainMap);
        CardView hybridMap = bottomSheetView.findViewById(R.id.hybridMap);

//      Default Map Selection Views
        defaultMapSelectedView = bottomSheetView.findViewById(R.id.defaultMapSelectedView);
        defaultMapView = bottomSheetView.findViewById(R.id.defaultMapView);
        tvDefault = bottomSheetView.findViewById(R.id.tvDefault);

//      Satellite Map Selection Views
        satelliteMapSelectedView = bottomSheetView.findViewById(R.id.satelliteMapSelectedView);
        satelliteMapView = bottomSheetView.findViewById(R.id.satelliteMapView);
        tvSatellite = bottomSheetView.findViewById(R.id.tvSatellite);

//      Terrain Map Selection Views
        terrainMapSelectedView = bottomSheetView.findViewById(R.id.terrainMapSelectedView);
        terrainMapView = bottomSheetView.findViewById(R.id.terrainMapView);
        tvTerrain = bottomSheetView.findViewById(R.id.tvTerrain);

//      Hybrid Map Selection Views
        hybridMapSelectedView = bottomSheetView.findViewById(R.id.hybridMapSelectedView);
        hybridMapView = bottomSheetView.findViewById(R.id.hybridMapView);
        tvHybrid = bottomSheetView.findViewById(R.id.tvHybrid);

        if (!isBottomSheetOpen) {
            checkMapSelectionType();

            bottomSheetMapType.show();

            ivCloseMapType.setOnClickListener(v1 -> bottomSheetMapType.cancel());
            defaultMap.setOnClickListener(v1 -> defaultMapSetting());
            satelliteMap.setOnClickListener(v1 -> satelliteMapSetting());
            terrainMap.setOnClickListener(v1 -> terrainMapSetting());
            hybridMap.setOnClickListener(v1 -> hybridMapSetting());
            bottomSheetMapType.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isBottomSheetOpen = false;
                }
            });
            isBottomSheetOpen = true;
        }
    }

    private void hybridMapSetting() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        checkMapSelectionType();
    }

    private void terrainMapSetting() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        checkMapSelectionType();

    }

    private void satelliteMapSetting() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        checkMapSelectionType();

    }

    private void defaultMapSetting() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        checkMapSelectionType();

    }

    private void checkMapSelectionType() {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            defaultMapSelectedView.setVisibility(View.VISIBLE);
            defaultMapView.setVisibility(View.GONE);
            tvDefault.setTextColor(getResources().getColor(R.color.app_theme_color));
            hybridMapSelectedView.setVisibility(View.INVISIBLE);
            hybridMapView.setVisibility(View.VISIBLE);
            tvHybrid.setTextColor(getResources().getColor(R.color.dummy_black));
            terrainMapSelectedView.setVisibility(View.INVISIBLE);
            terrainMapView.setVisibility(View.VISIBLE);
            tvTerrain.setTextColor(getResources().getColor(R.color.dummy_black));
            satelliteMapSelectedView.setVisibility(View.INVISIBLE);
            satelliteMapView.setVisibility(View.VISIBLE);
            tvSatellite.setTextColor(getResources().getColor(R.color.dummy_black));

        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            satelliteMapSelectedView.setVisibility(View.VISIBLE);
            satelliteMapView.setVisibility(View.GONE);
            tvSatellite.setTextColor(getResources().getColor(R.color.app_theme_color));
            hybridMapSelectedView.setVisibility(View.INVISIBLE);
            hybridMapView.setVisibility(View.VISIBLE);
            tvHybrid.setTextColor(getResources().getColor(R.color.dummy_black));
            terrainMapSelectedView.setVisibility(View.INVISIBLE);
            terrainMapView.setVisibility(View.VISIBLE);
            tvTerrain.setTextColor(getResources().getColor(R.color.dummy_black));
            defaultMapSelectedView.setVisibility(View.INVISIBLE);
            defaultMapView.setVisibility(View.VISIBLE);
            tvDefault.setTextColor(getResources().getColor(R.color.dummy_black));

        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
            terrainMapSelectedView.setVisibility(View.VISIBLE);
            terrainMapView.setVisibility(View.GONE);
            tvTerrain.setTextColor(getResources().getColor(R.color.app_theme_color));
            hybridMapSelectedView.setVisibility(View.INVISIBLE);
            hybridMapView.setVisibility(View.VISIBLE);
            tvHybrid.setTextColor(getResources().getColor(R.color.dummy_black));
            satelliteMapSelectedView.setVisibility(View.INVISIBLE);
            satelliteMapView.setVisibility(View.VISIBLE);
            tvSatellite.setTextColor(getResources().getColor(R.color.dummy_black));
            defaultMapSelectedView.setVisibility(View.INVISIBLE);
            defaultMapView.setVisibility(View.VISIBLE);
            tvDefault.setTextColor(getResources().getColor(R.color.dummy_black));

        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            hybridMapSelectedView.setVisibility(View.VISIBLE);
            hybridMapView.setVisibility(View.GONE);
            tvHybrid.setTextColor(getResources().getColor(R.color.app_theme_color));
            terrainMapSelectedView.setVisibility(View.INVISIBLE);
            terrainMapView.setVisibility(View.VISIBLE);
            tvTerrain.setTextColor(getResources().getColor(R.color.dummy_black));
            satelliteMapSelectedView.setVisibility(View.INVISIBLE);
            satelliteMapView.setVisibility(View.VISIBLE);
            tvSatellite.setTextColor(getResources().getColor(R.color.dummy_black));
            defaultMapSelectedView.setVisibility(View.INVISIBLE);
            defaultMapView.setVisibility(View.VISIBLE);
            tvDefault.setTextColor(getResources().getColor(R.color.dummy_black));

        }
    }

}