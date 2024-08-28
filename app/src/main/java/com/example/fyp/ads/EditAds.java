package com.example.fyp.ads;

import static com.example.fyp.ads.AddCar.getAllUserAds;
import static com.example.fyp.main.MainActivity.dataProgressBar;
import static com.example.fyp.main.MainActivity.dataProgressView;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp.R;
import com.example.fyp.adapters.CitiesAdapter;
import com.example.fyp.adapters.UpdateImageAdapter;
import com.example.fyp.callback.OnCitySelectedListener;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.RVImagesModel;
import com.example.fyp.util.YearPickerDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditAds extends Fragment implements OnCitySelectedListener {

    TextView tvUpdatePhotosCount, tvUpdateCarMake, tvUpdateCarModel, tvUpdateCarVarient, tvUpdateModelYear, tvUpdatePickupCity, tvUpdateRegistrationCity, tvUpdateDriverAvailable, tvUpdateInsurance;
    EditText etUpdateCarRent, etUpdateCarMileage, etUpdateDesc;
    RecyclerView rvUpdateAdsImages;
    Button btnDeleteAd, btnUpdateAd;
    ImageView ivEditDesc;
    ArrayList<RVImagesModel> remoteImages = new ArrayList<>();
    UpdateImageAdapter updateImageAdapter;
    CitiesAdapter citiesAdapter;
    List<String> citiesList;
    Dialog dialog;
    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_ads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUpdateCarMake = view.findViewById(R.id.tvUpdateCarMake);
        tvUpdateCarModel = view.findViewById(R.id.tvUpdateCarModel);
        tvUpdateCarVarient = (TextView) view.findViewById(R.id.tvUpdateCarVarient);
        tvUpdateModelYear = view.findViewById(R.id.tvUpdateModelYear);
        tvUpdatePickupCity = view.findViewById(R.id.tvUpdatePickupCity);
        tvUpdateRegistrationCity = view.findViewById(R.id.tvUpdateRegistrationCity);
        etUpdateCarRent = view.findViewById(R.id.etUpdateCarRent);
        etUpdateCarMileage = view.findViewById(R.id.etUpdateCarMileage);
        tvUpdateDriverAvailable = view.findViewById(R.id.tvUpdateDriverAvailable);
        tvUpdateInsurance = view.findViewById(R.id.tvUpdateInsurance);
        etUpdateDesc = view.findViewById(R.id.etUpdateDesc);
        tvUpdatePhotosCount = view.findViewById(R.id.tvUpdatePhotosCount);
        btnDeleteAd = view.findViewById(R.id.btnDeleteAd);
        btnUpdateAd = view.findViewById(R.id.btnUpdateAd);
        ivEditDesc = view.findViewById(R.id.ivEditDesc);

        rvUpdateAdsImages = view.findViewById(R.id.rvUpdateAdsImages);
        rvUpdateAdsImages.setHasFixedSize(true);
        rvUpdateAdsImages.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        citiesList = new ArrayList<>();
        citiesAdapter = new CitiesAdapter(getContext(), citiesList, this);
        dialog = new Dialog(getContext(), R.style.NetworkAlertDialog);

        displayData();

        tvUpdateInsurance.setOnClickListener(v -> updateInsurance());
        tvUpdateDriverAvailable.setOnClickListener(v -> updateDriver());
        tvUpdateRegistrationCity.setOnClickListener(v -> updateRegisterCity());
        tvUpdatePickupCity.setOnClickListener(v -> updatePickupCity());
        tvUpdateModelYear.setOnClickListener(v -> updateModelYear());

        btnDeleteAd.setOnClickListener(v -> deleteData());
        btnUpdateAd.setOnClickListener(v -> updateAdsData());

    }

    private void updateAdsData() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = Constant.itemClicked;

        String year = tvUpdateModelYear.getText().toString().trim();
        String pickup_city = tvUpdatePickupCity.getText().toString().trim();
        String registeration = tvUpdateRegistrationCity.getText().toString().trim();
        String rent = etUpdateCarRent.getText().toString().trim();
        String mileage = etUpdateCarMileage.getText().toString().trim();
        String driver_available = tvUpdateDriverAvailable.getText().toString().trim();
        String insurance = tvUpdateInsurance.getText().toString().trim();
        String desc = etUpdateDesc.getText().toString().trim();

//        Find Current Date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        if (TextUtils.isEmpty(rent)) {
            etUpdateCarRent.setError("Enter Car Rent");
            etUpdateCarRent.requestFocus();
            return;

        } else if (TextUtils.isEmpty(mileage)) {
            etUpdateCarMileage.setError("Enter Mileage");
            etUpdateCarMileage.requestFocus();
            return;

        }
        dataProgressBar.setVisibility(View.VISIBLE);
        dataProgressView.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("year", year);
        hashMap.put("pickup_city", pickup_city);
        hashMap.put("registeration", registeration);
        hashMap.put("rent", rent);
        hashMap.put("mileage", mileage);
        hashMap.put("driver_available", driver_available);
        hashMap.put("insurance", insurance);
        hashMap.put("desc", desc);
        hashMap.put("date", currentDate);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uuid)
                .child("userAds")
                .child(itemId)
                .updateChildren(hashMap);
        FirebaseDatabase.getInstance().getReference()
                .child("allUserAds")
                .child(uuid)
                .child(itemId)
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getAllUserAds();
                            dataProgressBar.setVisibility(View.GONE);
                            dataProgressView.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loadFragment(requireActivity().getSupportFragmentManager(), new MyAdsFragment());
                        } else {
                            Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dataProgressBar.setVisibility(View.GONE);
                            dataProgressView.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });
    }

    private void updatePickupCity() {
        if (!dialog.isShowing()) {
            citiesList.clear();
            dialog.setContentView(R.layout.select_city_dialog);
            RecyclerView recyclerView = dialog.findViewById(R.id.rvSelectCity);

            TextView cancelDialog = dialog.findViewById(R.id.tvCancelYearDialog);
            searchView = dialog.findViewById(R.id.searchView);
            ProgressBar citySelectionProgressBar = dialog.findViewById(R.id.citySelectionProgressBar);
            View citySelectionBackground = dialog.findViewById(R.id.citySelectionBackground);
            LinearLayout llSearchedCityNotFound = dialog.findViewById(R.id.llSearchedCityNotFound);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            getCities(citySelectionProgressBar, citySelectionBackground);
            recyclerView.setAdapter(citiesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            cancelDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<String> filteredList = new ArrayList<>();

                    for (String city : citiesList) {
                        if (city.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(city);
                        }
                    }
                    if (filteredList.isEmpty()) {
                        llSearchedCityNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    } else {
                        llSearchedCityNotFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        citiesAdapter.filterList(filteredList);
                    }
                    return true;
                }
            });
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    searchView.setQuery("", false);
                }
            });
        }

    }

    private void getCities(ProgressBar progressBar, View view) {
        String url = "https://cod.callcourier.com.pk/API/CallCourier/GetCityList";

        progressBar.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("CityName");
                        String formattedCityName = toTitleCase(name);
                        citiesList.add(formattedCityName);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jsonArrayRequest);

    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] words = text.split("\\s+");

        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (titleCase.length() > 0) {
                titleCase.append(" ");
            }
            titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }

        return titleCase.toString();
    }

    private void updateModelYear() {
        YearPickerDialog dialog = new YearPickerDialog();
        dialog.setOnYearSelectedListener(new YearPickerDialog.OnYearSelectedListener() {
            @Override
            public void onYearSelected(int year) {
                tvUpdateModelYear.setText(String.valueOf(year));
            }
        });
        dialog.show(getParentFragmentManager(), "YearPickerDialog");
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void updateRegisterCity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_update_register_city, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.tvUpdateRegistrationLHR).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Lahore");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationFSD).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Faisalabad");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationMultan).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Multan");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationPindi).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Rawalpindi");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationIslamabad).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Islamabad");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationKarachi).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Karachi");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationHyderabad).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Hyderabad");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationPsw).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Peshawar");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationQuetta).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Quetta");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationOther).setOnClickListener(v -> {
            tvUpdateRegistrationCity.setText("Other");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateRegistrationCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void updateDriver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_update_driver_avail, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.tvOnlyDriverUpdate).setOnClickListener(v -> {
            tvUpdateDriverAvailable.setText("Only with Driver");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvOnDemandUpdate).setOnClickListener(v -> {
            tvUpdateDriverAvailable.setText("Driver on Demand");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvWithoutDriverUpdate).setOnClickListener(v -> {
            tvUpdateDriverAvailable.setText("Without Driver");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvCancelDriverUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void updateInsurance() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_update_insurance, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.tvUpdateStatusYES).setOnClickListener(v -> {
            tvUpdateInsurance.setText("Yes");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvUpdateStatusNO).setOnClickListener(v -> {
            tvUpdateInsurance.setText("No");
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.tvCancelInsuranceUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void displayData() {
        displayImages();
        tvUpdatePhotosCount.setText(String.valueOf(remoteImages.size()));
        tvUpdateCarMake.setText(Constant.privateItemModel.getMake());
        tvUpdateCarModel.setText(Constant.privateItemModel.getModel());
        tvUpdateCarVarient.setText(Constant.privateItemModel.getVariant());
        tvUpdateModelYear.setText(Constant.privateItemModel.getYear());
        tvUpdatePickupCity.setText(Constant.privateItemModel.getPickup_city());
        tvUpdateRegistrationCity.setText(Constant.privateItemModel.getRegisteration());
        etUpdateCarRent.setText(Constant.privateItemModel.getRent());
        etUpdateCarMileage.setText(Constant.privateItemModel.getMileage());
        tvUpdateDriverAvailable.setText(Constant.privateItemModel.getDriver_available());
        tvUpdateInsurance.setText(Constant.privateItemModel.getInsurance());
        switch (!Constant.privateItemModel.getDesc().isEmpty() ? 1 : 0) {
            case 1:
                etUpdateDesc.setText(Constant.privateItemModel.getDesc());
                ivEditDesc.setVisibility(View.VISIBLE);
                break;
            case 0:
                ivEditDesc.setVisibility(View.GONE);
                break;
        }

    }

    private void displayImages() {
        for (int i = 0; i < Constant.privateItemModel.getCarImages().size(); i++) {
            if (!Constant.privateItemModel.getCarImages().get(i).isEmpty()) {
                switch (i) {
                    case 0:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(0)));
                        break;
                    case 1:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(1)));
                        break;
                    case 2:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(2)));
                        break;
                    case 3:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(3)));
                        break;
                    case 4:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(4)));
                        break;
                    case 5:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(5)));
                        break;
                    case 6:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(6)));
                        break;
                    case 7:
                        remoteImages.add(new RVImagesModel(Constant.privateItemModel.getCarImages().get(7)));
                        break;
                    default:
                        Toast.makeText(getContext(), "Images not Found", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
        updateImageAdapter = new UpdateImageAdapter(getContext(), remoteImages);
        rvUpdateAdsImages.setAdapter(updateImageAdapter);


    }

    private void deleteData() {
        String itemId = Constant.itemClicked;
        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        builder.setTitle("Delete Car");
        builder.setMessage("Are you sure you want to delete your car? You can't undo this action.");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataProgressBar.setVisibility(View.VISIBLE);
                dataProgressView.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                deletePictures(UUID, itemId);
                deleteAd(UUID, itemId);
                dialog.dismiss();
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

    private void deletePictures(String uuid, String itemId) {
        String folderPath = "images/" + uuid + "/" + itemId + "/";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderPath);

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    String itemName = item.getName();
                    storageRef.child(itemName).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Folder does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAd(String uuid, String itemId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uuid)
                .child("userAds")
                .child(itemId)
                .removeValue();
        FirebaseDatabase.getInstance().getReference()
                .child("allUserAds")
                .child(uuid)
                .child(itemId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getAllUserAds();
                            dataProgressBar.setVisibility(View.GONE);
                            dataProgressView.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loadFragment(requireActivity().getSupportFragmentManager(), new MyAdsFragment());
                        } else {
                            Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dataProgressBar.setVisibility(View.GONE);
                            dataProgressView.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });

    }

    @Override
    public void onCitySelected(String cityName) {
        String formattedCityName = toTitleCase(cityName);
        tvUpdatePickupCity.setText(formattedCityName);
        dialog.dismiss();
        searchView.setQuery("", false);
    }
}