package com.example.fyp.home;

import static com.example.fyp.main.MainActivity.NavigationHide;
import static com.example.fyp.main.MainActivity.drawerLayout;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.splash.SplashScreen.testArray;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.adapters.GridAdapter;
import com.example.fyp.adapters.ItemsAdapter;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.PopularItemsModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView tvHatchBack, tvSedan, tvSUV, tvBus;
    List<PopularItemsModel> itemList;
    RecyclerView recyclerView, rvHomeAllVehiclesAds;
    ItemsAdapter itemsAdapter;
    GridAdapter gridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHatchBack = view.findViewById(R.id.tvHatchBack);
        tvSedan = view.findViewById(R.id.tvSedan);
        tvSUV = view.findViewById(R.id.tvSUV);
        tvBus = view.findViewById(R.id.tvBus);
        recyclerView = view.findViewById(R.id.rvPopularItems);
        rvHomeAllVehiclesAds = view.findViewById(R.id.rvHomeAllVehiclesAds);

        gridAdapter = new GridAdapter(testArray, false);

        //Grid List
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvHomeAllVehiclesAds.setLayoutManager(gridLayoutManager);
        rvHomeAllVehiclesAds.setAdapter(gridAdapter);


        //item list
        itemList = generateDummyData();
        itemsAdapter = new ItemsAdapter(itemList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemsAdapter);

        tvHatchBack.setOnClickListener(v -> loadHomeCarType("HatchBack"));
        tvSedan.setOnClickListener(v -> loadHomeCarType("Sedan"));
        tvSUV.setOnClickListener(v -> loadHomeCarType("SUV"));
        tvBus.setOnClickListener(v -> loadHomeCarType("Bus"));

    }

    private void loadHomeCarType(String carTypeName) {
        loadFragment(getActivity().getSupportFragmentManager(), new HomeCarType());
        Constant.homeCarType = carTypeName;
        NavigationHide();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    private List<PopularItemsModel> generateDummyData() {
        List<PopularItemsModel> items = new ArrayList<>();
        items.add(new PopularItemsModel("Honda Civic", R.drawable.iv_civic));
        items.add(new PopularItemsModel("KIA Sportage", R.drawable.iv_kia_sportage));
        items.add(new PopularItemsModel("Honda BRV", R.drawable.iv_honda_brv));
        items.add(new PopularItemsModel("Suzuki Wagon R", R.drawable.iv_suzuki_wagon));

        return items;
    }
}