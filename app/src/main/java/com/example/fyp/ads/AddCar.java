package com.example.fyp.ads;

import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.dataProgressBar;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.splash.SplashScreen.testArray;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.fyp.adapters.ImageSelectionAdapter;
import com.example.fyp.callback.OnCitySelectedListener;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.UploadAdsData;
import com.example.fyp.util.YearPickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddCar extends Fragment implements OnCitySelectedListener, ImageSelectionAdapter.ImageRemoveListener {

    Spinner carMake, carModel, carTransmission, engineType, bodyColor, carVariant, engineCapacity, carType, seatingCapacity, insured, registerCity, driverAvailability;
    EditText etCarMileage, etCarRent, etDesc;
    TextView selectYear, pickupCity, photosCount, uploadingPercentage, tvBackToMyAds;
    Button btnSave;
    Dialog dialog;
    ConstraintLayout parentLayout;
    CitiesAdapter citiesAdapter;
    List<String> citiesList;
    Typeface customFont;
    ProgressBar citySelectionProgressBar;
    SearchView searchView;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar uploadingAdsProgress;
    View citySelectionBackground, uploadingProgressView;
    LinearLayout openGallery;
    private ActivityResultLauncher<String> launcher;
    private List<Uri> selectedImages;
    private ImageSelectionAdapter imageAdapter;
    RecyclerView rvCarAdsImages;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    DatabaseReference databaseReference;
    private ArrayList<String> addImages = new ArrayList<>();
    private boolean isClicked = false;
    private int toastCount = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_car, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carMake = view.findViewById(R.id.carMakeSpinner);
        carModel = view.findViewById(R.id.carModelSpinner);
        carTransmission = view.findViewById(R.id.carTransmissionSpinner);
        engineType = view.findViewById(R.id.engineTypeSpinner);
        bodyColor = view.findViewById(R.id.bodyColorSpinner);
        carVariant = view.findViewById(R.id.carVariantSpinner);
        engineCapacity = view.findViewById(R.id.engineCapacitySpinner);
        carType = view.findViewById(R.id.carTypeSpinner);
        seatingCapacity = view.findViewById(R.id.seatingCapacitySpinner);
        pickupCity = view.findViewById(R.id.tvPickupCity);
        insured = view.findViewById(R.id.insuredSpinner);
        registerCity = view.findViewById(R.id.registrationCitySpinner);
        driverAvailability = view.findViewById(R.id.driverAvailabilitySpinner);
        etCarMileage = view.findViewById(R.id.etCarMileage);
        etCarRent = view.findViewById(R.id.etCarRent);
        etDesc = view.findViewById(R.id.etDesc);
        selectYear = view.findViewById(R.id.tvSelectYear);
        btnSave = view.findViewById(R.id.btnSave);
        parentLayout = view.findViewById(R.id.clParentLayout);
        photosCount = view.findViewById(R.id.tvPhotosCount);
        openGallery = view.findViewById(R.id.llOpenGallery);
        rvCarAdsImages = view.findViewById(R.id.rvCarAdsImages);
        uploadingPercentage = view.findViewById(R.id.tvUploadingPercent);
        uploadingAdsProgress = view.findViewById(R.id.uploadProgress);
        uploadingProgressView = view.findViewById(R.id.uploadingProgressView);
        tvBackToMyAds = view.findViewById(R.id.tvBackToMyAds);

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        customFont = ResourcesCompat.getFont(requireContext(), R.font.product_sans_regular);
        citiesList = new ArrayList<>();
        citiesAdapter = new CitiesAdapter(getContext(), citiesList, this);
        dialog = new Dialog(getContext(), R.style.NetworkAlertDialog);

        selectedImages = new ArrayList<>();
        imageAdapter = new ImageSelectionAdapter(requireContext(), selectedImages, this);
        rvCarAdsImages.setHasFixedSize(true);
        rvCarAdsImages.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCarAdsImages.setAdapter(imageAdapter);

        setupSpinner(carMake);
        setupSpinner(carModel);
        setupSpinner(carTransmission);
        setupSpinner(engineType);
        setupSpinner(bodyColor);
        setupSpinner(carVariant);
        setupSpinner(engineCapacity);
        setupSpinner(carType);
        setupSpinner(seatingCapacity);
        setupSpinner(insured);
        setupSpinner(registerCity);
        setupSpinner(driverAvailability);

        independentSpinners();

        carMake.setOnItemSelectedListener(new CarMakeItemSelectedListener());
        registerCity.setOnItemSelectedListener(new RegisterCityItemSelectedListener());
        insured.setOnItemSelectedListener(new InsuranceItemSelectedListener());
        driverAvailability.setOnItemSelectedListener(new DriverAvailabilityItemSelectedListener());
        carModel.setOnItemSelectedListener(new CarModelItemSelectedListener());
        carTransmission.setOnItemSelectedListener(new CarTransmissionItemSelectedListener());
        engineType.setOnItemSelectedListener(new EngineTypeItemSelectedListener());
        bodyColor.setOnItemSelectedListener(new BodyColorItemSelectedListener());
        carVariant.setOnItemSelectedListener(new CarVariantItemSelectedListener());
        engineCapacity.setOnItemSelectedListener(new EngineCapacityItemSelectedListener());
        carType.setOnItemSelectedListener(new CarTypeItemSelectedListener());
        seatingCapacity.setOnItemSelectedListener(new SeatingCapacityItemSelectedListener());

        tvBackToMyAds.setOnClickListener(v -> goBack());

        uploadingProgressView.setOnClickListener(v -> alertToast());

        parentLayout.setOnClickListener(v -> setupText());
        selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupText();
                yearPickerDialog();
            }
        });
        pickupCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupText();
                if (!dialog.isShowing()) {
                    citySelectionDialog();
                }
            }
        });

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupText();
                if (selectedImages.size() >= 8) {
                    Toast.makeText(getContext(), "Maximum limit(8 images) reached", Toast.LENGTH_SHORT).show();
                    return;
                }
                launcher.launch("image/*");

            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
                new ActivityResultCallback<List<Uri>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onActivityResult(List<Uri> uris) {
                        if (uris != null && !uris.isEmpty()) {

                            int remainingSlots = 8 - selectedImages.size();

                            if (remainingSlots > 0) {
                                int numToAdd = Math.min(remainingSlots, uris.size());
                                selectedImages.addAll(uris.subList(0, numToAdd));

                                photosCount.setText(String.valueOf(selectedImages.size()));

                                imageAdapter.notifyDataSetChanged();
                                if (uris.size() > numToAdd) {
                                    Toast.makeText(getContext(), "Maximum limit(8 images) reached", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getContext(), "Maximum limit(8 images) reached", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String make = carMake.getSelectedItem().toString().trim();
                String model = carModel.getSelectedItem().toString().trim();
                String transmission = carTransmission.getSelectedItem().toString().trim();
                String variant = carVariant.getSelectedItem().toString().trim();
                String color = bodyColor.getSelectedItem().toString().trim();
                String typeOfEngine = engineType.getSelectedItem().toString().trim();
                String capacityOfEngine = engineCapacity.getSelectedItem().toString().trim();
                String capacityOfSeating = seatingCapacity.getSelectedItem().toString().trim();
                String typeOfCar = carType.getSelectedItem().toString().trim();
                String insure = insured.getSelectedItem().toString().trim();
                String registration = registerCity.getSelectedItem().toString().trim();
                String availabilityOfDriver = driverAvailability.getSelectedItem().toString().trim();
                String photos = photosCount.getText().toString().trim();
                String year = selectYear.getText().toString().trim();
                String city = pickupCity.getText().toString().trim();
                String mileage = etCarMileage.getText().toString().trim();
                String rent = etCarRent.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();

                String ownerName = Constant.userData.name;
                String ownerCompany = Constant.userData.company;
                String ownerPhone = Constant.userData.phone;

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());


                if (year.equals("Select Model Year")) {
                    Toast.makeText(getContext(), "Kindly Select Model Year", Toast.LENGTH_SHORT).show();
                    return;

                } else if (city.equals("Select Pickup City")) {
                    Toast.makeText(getContext(), "Kindly Select Pickup City", Toast.LENGTH_SHORT).show();
                    return;

                } else if (TextUtils.isEmpty(mileage)) {
                    etCarMileage.setError("Enter Mileage");
                    etCarMileage.requestFocus();
                    return;

                } else if (TextUtils.isEmpty(rent)) {
                    etCarRent.setError("Enter Car Rent");
                    etCarRent.requestFocus();
                    return;

                } else if ("Select Insurance Status".equals(insure)) {
                    Toast.makeText(getContext(), "Kindly Select Insurance Status", Toast.LENGTH_SHORT).show();
                    return;

                } else if (photos.equals("0")) {
                    Toast.makeText(getContext(), "Kindly Add Car Images", Toast.LENGTH_SHORT).show();
                    return;

                } else if (isClicked) {
                    Toast.makeText(getContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
                    return;
                }
                isClicked = true;

                dataProgressBar.setVisibility(View.VISIBLE);
                uploadingProgressView.setVisibility(View.VISIBLE);
                setupText();

                UploadAdsData adsData = new UploadAdsData(make, model, year, transmission, variant, color, typeOfEngine, capacityOfEngine, capacityOfSeating, typeOfCar, city, insure, registration, availabilityOfDriver, mileage, rent, desc, ownerName, ownerCompany, ownerPhone, currentDate);

                String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = firebaseDatabase.getReference("users").child(UUID);
                DatabaseReference reference = databaseReference.child("userAds");
                String uniqueKey = reference.push().getKey();

                reference.child(uniqueKey).setValue(adsData);

                //store value for all user
                databaseReference = firebaseDatabase.getReference("allUserAds").child(UUID);
                databaseReference.child(uniqueKey).setValue(adsData);

                addImages.clear();

                int count = 0;

                /*for (Uri imageUri : selectedImages) {
                    count++;
                    uploadImage(imageUri, uniqueKey, count);
                }*/

                uploadImage(selectedImages, uniqueKey, count);

            }
        });


    }

    private void alertToast() {

        if (toastCount < 2) {
            showToastMessage();
            toastCount++;
        }
    }

    private void showToastMessage() {
        Toast.makeText(getContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        if (dataProgressBar.getVisibility() == View.VISIBLE || uploadingAdsProgress.getVisibility() == View.VISIBLE) {
            Toast.makeText(getContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
            return;
        } else {
            requireActivity().getSupportFragmentManager().popBackStack("MyAdsBackStack", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            NavigationVisibile();
        }
    }

    @SuppressLint("SetTextI18n")
    private void uploadImage(List<Uri> imageUri, String uniqueKey, int count) {
        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (count < imageUri.size()) {
            StorageReference fileReference = storage.getReference()
                    .child("images").child(UUID).child(uniqueKey).child(System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = fileReference.putFile(imageUri.get(count));


            uploadTask.addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            addImages.add(imageUrl);

                            if (count != selectedImages.size()) {
//                                recursion method
                                uploadImage(selectedImages, uniqueKey, count + 1);
                            }
                        });
                        dataProgressBar.setVisibility(View.GONE);
                        uploadingAdsProgress.setVisibility(View.VISIBLE);
                        uploadingPercentage.setVisibility(View.VISIBLE);
                        int progress = (int) ((count + 1) / (float) imageUri.size() * 100);
                        uploadingAdsProgress.setProgress(progress);
                        uploadingPercentage.setText(progress + "%");

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dataProgressBar.setVisibility(View.GONE);
                            uploadingProgressView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveImageUrlToDatabase(uniqueKey, addImages);
        }
    }

    private void saveImageUrlToDatabase(String uniqueKey, ArrayList<String> addImages) {
        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference("users").child(UUID);

        DatabaseReference reference = databaseReference.child("userAds").child(uniqueKey);
        DatabaseReference userReference = reference.child("carImages");


        userReference.setValue(addImages);

        DatabaseReference allUsersReference = firebaseDatabase.getReference("allUserAds").child(UUID).child(uniqueKey);
        DatabaseReference myReference = allUsersReference.child("carImages");
        myReference.setValue(addImages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getAllUserAds();

                        uploadingProgressView.setVisibility(View.GONE);
                        uploadingAdsProgress.setVisibility(View.GONE);
                        uploadingPercentage.setVisibility(View.GONE);

                        loadFragment(getActivity().getSupportFragmentManager(), new MyAdsFragment());
                        NavigationVisibile();

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image to database: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void citySelectionDialog() {
        citiesList.clear();
        dialog.setContentView(R.layout.select_city_dialog);

        RecyclerView recyclerView = dialog.findViewById(R.id.rvSelectCity);

        TextView cancelDialog = dialog.findViewById(R.id.tvCancelYearDialog);
        searchView = dialog.findViewById(R.id.searchView);
        citySelectionProgressBar = dialog.findViewById(R.id.citySelectionProgressBar);
        citySelectionBackground = dialog.findViewById(R.id.citySelectionBackground);
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
        }, new Response.ErrorListener() {
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

    private void independentSpinners() {
        ArrayAdapter<CharSequence> carMakeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_make, android.R.layout.simple_spinner_item);
        carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carMake.setAdapter(carMakeAdapter);

        ArrayAdapter<CharSequence> registerCityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.registration_city, android.R.layout.simple_spinner_item);
        registerCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registerCity.setAdapter(registerCityAdapter);

        ArrayAdapter<CharSequence> insuranceAdapter = ArrayAdapter.createFromResource(getContext(), R.array.insurance, android.R.layout.simple_spinner_item);
        insuranceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        insured.setAdapter(insuranceAdapter);

        ArrayAdapter<CharSequence> driverAdapter = ArrayAdapter.createFromResource(getContext(), R.array.driver_availability, android.R.layout.simple_spinner_item);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverAvailability.setAdapter(driverAdapter);
    }

    private void yearPickerDialog() {
        YearPickerDialog dialog = new YearPickerDialog();
        dialog.setOnYearSelectedListener(new YearPickerDialog.OnYearSelectedListener() {
            @Override
            public void onYearSelected(int year) {
                selectYear.setText(String.valueOf(year));
                selectYear.setTextColor(Color.BLACK);
                selectYear.setTextSize(12);
                selectYear.setLetterSpacing(.05F);
                selectYear.setTypeface(customFont, Typeface.BOLD);
            }
        });
        dialog.show(getParentFragmentManager(), "YearPickerDialog");
    }

    private void setupText() {
        etCarMileage.clearFocus();
        etCarRent.clearFocus();
        etDesc.clearFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etCarMileage.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etCarRent.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etDesc.getWindowToken(), 0);
    }

    private void setupSpinner(Spinner spinner) {
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etCarMileage.clearFocus();
                etCarRent.clearFocus();
                etDesc.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etCarMileage.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(etCarRent.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(etDesc.getWindowToken(), 0);
                return false;

            }
        });
    }

    @Override
    public void onCitySelected(String cityName) {
        String formattedCityName = toTitleCase(cityName);
        pickupCity.setText(formattedCityName);
        pickupCity.setTextColor(Color.BLACK);
        pickupCity.setLetterSpacing(.05F);
        pickupCity.setTextSize(12);
        pickupCity.setTypeface(customFont, Typeface.BOLD);
        dialog.dismiss();
        searchView.setQuery("", false);

    }

    @Override
    public void onImageRemoved(Uri imageUri) {
        int count = selectedImages.size();
        photosCount.setText(String.valueOf(count));
    }

    private class CarMakeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);
            updateAccordingToCarMake(parent);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class CarModelItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            spinnerTextAppearance(parent, view);
            updateAccordingToCarModel();

        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private void updateAccordingToCarModel() {
        String carMakeItemSelected = (String) carMake.getSelectedItem();
        String carModelItemSelected = (String) carModel.getSelectedItem();

        if ("Audi".equals(carMakeItemSelected)
                || "BMW".equals(carMakeItemSelected) || "GMC".equals(carMakeItemSelected) || "Haval".equals(carMakeItemSelected)
                || "Land Rover".equals(carMakeItemSelected) || "Lexus".equals(carMakeItemSelected) || "MG".equals(carMakeItemSelected)
                || "Rolls-Royce".equals(carMakeItemSelected) || "Atrai Wagon".equals(carModelItemSelected) || "Bego".equals(carModelItemSelected)
                || "CR-Z".equals(carModelItemSelected) || "Fit".equals(carModelItemSelected) || "N One".equals(carModelItemSelected)
                || "N Wagon".equals(carModelItemSelected) || "Vezel".equals(carModelItemSelected) || "Hijet".equals(carModelItemSelected)
                || "Elantra".equals(carModelItemSelected) || "Sonata".equals(carModelItemSelected) || "Tucson".equals(carModelItemSelected)
                || "Carnival".equals(carModelItemSelected) || "Sportage".equals(carModelItemSelected) || "Stonic".equals(carModelItemSelected)
                || "C Class".equals(carModelItemSelected) || "E Class".equals(carModelItemSelected) || "Aqua".equals(carModelItemSelected)
                || "Corolla-Fielder".equals(carModelItemSelected) || "Mark X".equals(carModelItemSelected) || "Noah".equals(carModelItemSelected)
                || "Passo".equals(carModelItemSelected) || "Platz".equals(carModelItemSelected) || "Prius".equals(carModelItemSelected)
                || "Probox".equals(carModelItemSelected) || "Rush".equals(carModelItemSelected) || "Tundra".equals(carModelItemSelected)
                || "EK Wagon".equals(carModelItemSelected) || "Clipper".equals(carModelItemSelected) || "Dayz".equals(carModelItemSelected)
                || "Juke".equals(carModelItemSelected) || "X70".equals(carModelItemSelected) || "S Class".equals(carModelItemSelected)
                || "Move".equals(carModelItemSelected) || "Move Conte".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_transmission_three, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carTransmission.setAdapter(updatedAdapter);


        } else if ("Alsvin".equals(carModelItemSelected) || "Chery".equals(carMakeItemSelected) || "DFSK".equals(carMakeItemSelected)
                || "Daewoo".equals(carMakeItemSelected) || "Higer".equals(carMakeItemSelected) || "Isuzu".equals(carMakeItemSelected)
                || "Accord".equals(carModelItemSelected) || "Airwave".equals(carModelItemSelected) || "BR-V".equals(carModelItemSelected)
                || "City".equals(carModelItemSelected) || "Civic".equals(carModelItemSelected) || "Copen".equals(carModelItemSelected)
                || "RX8".equals(carModelItemSelected) || "Scrum".equals(carModelItemSelected) || "Hiace".equals(carModelItemSelected)
                || "Lancer".equals(carModelItemSelected) || "Pajero".equals(carModelItemSelected) || "Pajero-Mini".equals(carModelItemSelected)
                || "Picanto".equals(carModelItemSelected) || "Spectra".equals(carModelItemSelected) || "Cuore".equals(carModelItemSelected)
                || "350 Z".equals(carModelItemSelected) || "AD Van".equals(carModelItemSelected) || "Sunny".equals(carModelItemSelected)
                || "Saga".equals(carModelItemSelected) || "Alto".equals(carModelItemSelected) || "Baleno".equals(carModelItemSelected)
                || "Ciaz".equals(carModelItemSelected) || "Cultus".equals(carModelItemSelected) || "Every".equals(carModelItemSelected)
                || "Coaster".equals(carModelItemSelected) || "Corolla".equals(carModelItemSelected) || "Corolla-Axio".equals(carModelItemSelected)
                || "Crown".equals(carModelItemSelected) || "Fortuner".equals(carModelItemSelected) || "Hilux".equals(carModelItemSelected)
                || "Land Cruiser".equals(carModelItemSelected) || "Prado".equals(carModelItemSelected) || "Premio".equals(carModelItemSelected)
                || "Surf".equals(carModelItemSelected) || "Vitz".equals(carModelItemSelected) || "Yaris".equals(carModelItemSelected)
                || "Liana".equals(carModelItemSelected) || "Margalla".equals(carModelItemSelected) || "Swift".equals(carModelItemSelected)
                || "Wagon R".equals(carModelItemSelected) || "Grand Starex".equals(carModelItemSelected) || "Mira".equals(carModelItemSelected)
                || "Yutong".equals(carMakeItemSelected)) {

            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_transmission_two, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carTransmission.setAdapter(updatedAdapter);

        } else if ("Karvaan".equals(carModelItemSelected) || "M9".equals(carModelItemSelected) || "FAW".equals(carMakeItemSelected)
                || "JW".equals(carMakeItemSelected) || "Panther".equals(carModelItemSelected) || "Santro".equals(carModelItemSelected)
                || "APV".equals(carModelItemSelected) || "Bolan".equals(carModelItemSelected) || "Khyber".equals(carModelItemSelected)
                || "Mehran".equals(carModelItemSelected) || "Ravi".equals(carModelItemSelected) || "Shehzore".equals(carModelItemSelected)
                || "Prince".equals(carMakeItemSelected) || "B2200".equals(carModelItemSelected) || "United".equals(carMakeItemSelected)) {

            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_transmission_one, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carTransmission.setAdapter(updatedAdapter);

        }

        updateEngineType(carMakeItemSelected, carModelItemSelected);
        updateBodyColor(carMakeItemSelected, carModelItemSelected);
        updateCarVariant_EngineCapacity(carMakeItemSelected, carModelItemSelected);
        updateCarType(carModelItemSelected);
        updateSeatingCapacity(carModelItemSelected);


    }

    private void updateSeatingCapacity(String carModelItemSelected) {
        if ("A3".equals(carModelItemSelected)
                || "A4".equals(carModelItemSelected) || "A5".equals(carModelItemSelected) || "A6".equals(carModelItemSelected)
                || "Alsvin".equals(carModelItemSelected) || "QQ".equals(carModelItemSelected) || "Accord".equals(carModelItemSelected)
                || "Airwave".equals(carModelItemSelected) || "CR-Z".equals(carModelItemSelected) || "City".equals(carModelItemSelected)
                || "Civic".equals(carModelItemSelected) || "Fit".equals(carModelItemSelected) || "Vezel".equals(carModelItemSelected)
                || "Elantra".equals(carModelItemSelected) || "Sierra".equals(carModelItemSelected) || "Bego".equals(carModelItemSelected)
                || "V2".equals(carModelItemSelected) || "Sonata".equals(carModelItemSelected) || "Tucson".equals(carModelItemSelected)
                || "D-Max".equals(carModelItemSelected) || "CT200h".equals(carModelItemSelected) || "Forland".equals(carModelItemSelected)
                || "Picanto".equals(carModelItemSelected) || "Spectra".equals(carModelItemSelected) || "Sportage".equals(carModelItemSelected)
                || "RX Series".equals(carModelItemSelected) || "Stonic".equals(carModelItemSelected) || "Santro".equals(carModelItemSelected)
                || "Glory 580".equals(carModelItemSelected) || "C Class".equals(carModelItemSelected) || "E Class".equals(carModelItemSelected)
                || "S Class".equals(carModelItemSelected) || "B2200".equals(carModelItemSelected) || "Lancer".equals(carModelItemSelected)
                || "Pajero".equals(carModelItemSelected) || "AD Van".equals(carModelItemSelected) || "Juke".equals(carModelItemSelected)
                || "Baleno".equals(carModelItemSelected) || "Sunny".equals(carModelItemSelected) || "Ciaz".equals(carModelItemSelected)
                || "3 Series".equals(carModelItemSelected) || "5 Series".equals(carModelItemSelected) || "7 Series".equals(carModelItemSelected)
                || "Khyber".equals(carModelItemSelected) || "Liana".equals(carModelItemSelected) || "Margalla".equals(carModelItemSelected)
                || "Cultus".equals(carModelItemSelected) || "Swift".equals(carModelItemSelected) || "Aqua".equals(carModelItemSelected)
                || "Corolla".equals(carModelItemSelected) || "Corolla-Axio".equals(carModelItemSelected) || "Corolla-Fielder".equals(carModelItemSelected)
                || "Crown".equals(carModelItemSelected) || "Hilux".equals(carModelItemSelected) || "Mark X".equals(carModelItemSelected)
                || "Passo".equals(carModelItemSelected) || "Platz".equals(carModelItemSelected) || "Premio".equals(carModelItemSelected)
                || "Prius".equals(carModelItemSelected) || "Probox".equals(carModelItemSelected) || "Rush".equals(carModelItemSelected)
                || "Vitz".equals(carModelItemSelected) || "Yaris".equals(carModelItemSelected) || "Phantom".equals(carModelItemSelected)
                || "e-tron GT".equals(carModelItemSelected) || "e-tron GT RS".equals(carModelItemSelected) || "e-tron".equals(carModelItemSelected)
                || "X1".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Q7".equals(carModelItemSelected)
                || "Fortuner".equals(carModelItemSelected)
                || "Prado".equals(carModelItemSelected)
                || "Land Cruiser".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_7, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Karvaan".equals(carModelItemSelected)
                || "Atrai Wagon".equals(carModelItemSelected)
                || "X-PV".equals(carModelItemSelected)
                || "BR-V".equals(carModelItemSelected)
                || "Range Rover".equals(carModelItemSelected)
                || "Clipper".equals(carModelItemSelected)
                || "K07".equals(carModelItemSelected)
                || "Bolan".equals(carModelItemSelected)
                || "Surf".equals(carModelItemSelected)
                || "Every".equals(carModelItemSelected)
                || "X5 Series".equals(carModelItemSelected)
                || "Hijet".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5_6_7, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("M9".equals(carModelItemSelected)
                || "Copen".equals(carModelItemSelected)
                || "Ravi".equals(carModelItemSelected)
                || "350 Z".equals(carModelItemSelected)
                || "Shehzore".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_2, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("C37".equals(carModelItemSelected)
                || "Carnival".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_11, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Cuore".equals(carModelItemSelected)
                || "Mira".equals(carModelItemSelected)
                || "Move Conte".equals(carModelItemSelected)
                || "N One".equals(carModelItemSelected)
                || "N Wagon".equals(carModelItemSelected)
                || "EK Wagon".equals(carModelItemSelected)
                || "RX8".equals(carModelItemSelected)
                || "Dayz".equals(carModelItemSelected)
                || "Alto".equals(carModelItemSelected)
                || "Pearl".equals(carModelItemSelected)
                || "Mehran".equals(carModelItemSelected)
                || "Pajero-Mini".equals(carModelItemSelected)
                || "Move".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_4, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Standard".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_30_45, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Grand Starex".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_12, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("LX Series".equals(carModelItemSelected)
                || "Noah".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5_7_8, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Panther".equals(carModelItemSelected)
                || "Hiace".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_12_14, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Scrum".equals(carModelItemSelected)
                || "Wagon R".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_4_5, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("APV".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_8, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Coaster".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_26_30, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Tundra".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_15, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        }

    }

    private void updateCarType(String carModelItemSelected) {
        if ("A3".equals(carModelItemSelected)
                || "A4".equals(carModelItemSelected)
                || "A5".equals(carModelItemSelected)
                || "A6".equals(carModelItemSelected)
                || "QQ".equals(carModelItemSelected)
                || "C Class".equals(carModelItemSelected)
                || "E Class".equals(carModelItemSelected)
                || "S Class".equals(carModelItemSelected)
                || "3 Series".equals(carModelItemSelected)
                || "7 Series".equals(carModelItemSelected)
                || "5 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_sedan_sports, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Q7".equals(carModelItemSelected)
                || "e-tron".equals(carModelItemSelected)
                || "X5 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_SUV_sports, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("e-tron GT".equals(carModelItemSelected)
                || "e-tron GT RS".equals(carModelItemSelected)
                || "RX8".equals(carModelItemSelected)
                || "350 Z".equals(carModelItemSelected)
                || "Copen".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_sports, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("X1".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_Crossover_sports, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Alsvin".equals(carModelItemSelected)
                || "Accord".equals(carModelItemSelected)
                || "Sonata".equals(carModelItemSelected)
                || "Elantra".equals(carModelItemSelected)
                || "Lancer".equals(carModelItemSelected)
                || "Sunny".equals(carModelItemSelected)
                || "Phantom".equals(carModelItemSelected)
                || "Baleno".equals(carModelItemSelected)
                || "Liana".equals(carModelItemSelected)
                || "Margalla".equals(carModelItemSelected)
                || "Ciaz".equals(carModelItemSelected)
                || "Mark X".equals(carModelItemSelected)
                || "Premio".equals(carModelItemSelected)
                || "Yaris".equals(carModelItemSelected)
                || "Corolla".equals(carModelItemSelected)
                || "Corolla-Axio".equals(carModelItemSelected)
                || "Crown".equals(carModelItemSelected)
                || "Saga".equals(carModelItemSelected)
                || "Platz".equals(carModelItemSelected)
                || "Spectra".equals(carModelItemSelected)
                || "Civic".equals(carModelItemSelected)
                || "City".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_sedan, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Karvaan".equals(carModelItemSelected)
                || "Atrai Wagon".equals(carModelItemSelected)
                || "X-PV".equals(carModelItemSelected)
                || "Scrum".equals(carModelItemSelected)
                || "Clipper".equals(carModelItemSelected)
                || "Bolan".equals(carModelItemSelected)
                || "Every".equals(carModelItemSelected)
                || "K07".equals(carModelItemSelected)
                || "Hijet".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_mini_van, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("M9".equals(carModelItemSelected)
                || "Sierra".equals(carModelItemSelected)
                || "B2200".equals(carModelItemSelected)
                || "Forland".equals(carModelItemSelected)
                || "Hilux".equals(carModelItemSelected)
                || "Tundra".equals(carModelItemSelected)
                || "D-Max".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_pickup_truck, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("C37".equals(carModelItemSelected)
                || "Grand Starex".equals(carModelItemSelected)
                || "Panther".equals(carModelItemSelected)
                || "Hiace".equals(carModelItemSelected)
                || "APV".equals(carModelItemSelected)
                || "Noah".equals(carModelItemSelected)
                || "Carnival".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_Van, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Glory 580".equals(carModelItemSelected)
                || "Tucson".equals(carModelItemSelected)
                || "HS".equals(carModelItemSelected)
                || "Rush".equals(carModelItemSelected)
                || "Pajero-Mini".equals(carModelItemSelected)
                || "Sportage".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_compact_SUV, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Bego".equals(carModelItemSelected)
                || "Cuore".equals(carModelItemSelected)
                || "V2".equals(carModelItemSelected)
                || "CR-Z".equals(carModelItemSelected)
                || "CT200h".equals(carModelItemSelected)
                || "EK Wagon".equals(carModelItemSelected)
                || "Picanto".equals(carModelItemSelected)
                || "Pearl".equals(carModelItemSelected)
                || "Fit".equals(carModelItemSelected)
                || "Dayz".equals(carModelItemSelected)
                || "Cultus".equals(carModelItemSelected)
                || "Alto".equals(carModelItemSelected)
                || "Mehran".equals(carModelItemSelected)
                || "Khyber".equals(carModelItemSelected)
                || "Swift".equals(carModelItemSelected)
                || "Wagon R".equals(carModelItemSelected)
                || "Aqua".equals(carModelItemSelected)
                || "Passo".equals(carModelItemSelected)
                || "Prius".equals(carModelItemSelected)
                || "N One".equals(carModelItemSelected)
                || "N Wagon".equals(carModelItemSelected)
                || "Santro".equals(carModelItemSelected)
                || "Vitz".equals(carModelItemSelected)
                || "Alpha".equals(carModelItemSelected)
                || "Bravo".equals(carModelItemSelected)
                || "Move".equals(carModelItemSelected)
                || "Move Conte".equals(carModelItemSelected)
                || "Mira".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_Hatchback, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Standard".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_bus, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("H6".equals(carModelItemSelected)
                || "Range Rover".equals(carModelItemSelected)
                || "Pajero".equals(carModelItemSelected)
                || "Fortuner".equals(carModelItemSelected)
                || "Land Cruiser".equals(carModelItemSelected)
                || "Prado".equals(carModelItemSelected)
                || "Surf".equals(carModelItemSelected)
                || "LX Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_SUV, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Jolion".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_crossover_SUV, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Airwave".equals(carModelItemSelected)
                || "AD Van".equals(carModelItemSelected)
                || "Probox".equals(carModelItemSelected)
                || "Corolla-Fielder".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_station_wagon, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("BR-V".equals(carModelItemSelected)
                || "Vezel".equals(carModelItemSelected)
                || "RX Series".equals(carModelItemSelected)
                || "ZS".equals(carModelItemSelected)
                || "ZS EV".equals(carModelItemSelected)
                || "X70".equals(carModelItemSelected)
                || "Juke".equals(carModelItemSelected)
                || "Stonic".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_crossover, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Shehzore".equals(carModelItemSelected)
                || "Ravi".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_pickup_loader, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        } else if ("Coaster".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updateCarTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_type_mini_bus, android.R.layout.simple_spinner_item);
            updateCarTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carType.setAdapter(updateCarTypeAdapter);

        }

    }

    private void updateCarVariant_EngineCapacity(String carMakeItemSelected, String carModelItemSelected) {

        if ("A3".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_audi_one, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_A3, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("A4".equals(carModelItemSelected)
                || "A5".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_audi_one, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_A4_A5, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("A6".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_A6, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_A6, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Q7".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Q7, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_Q7, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("e-tron".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_e_tron, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_e_tron, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("e-tron GT".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_e_tron_gt, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_e_tron_GT, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("e-tron GT RS".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_e_tron_gt_rs, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_audi_e_tron_GT_RS, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("3 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_BMW_3_series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_3_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("5 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_BMW_5_series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_5_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("7 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_BMW_7_series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_7_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("X1".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_BMW_X1, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_X1_or_honda_vezel, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("X5 Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_BMW_X5_series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_X5_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Alsvin".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_changan_alsvin, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_DFSK_C37_or_changan_alsvin_or_honda_city, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Karvaan".equals(carModelItemSelected)
                || "M9".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_changan_two, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_chery_or_changan_karvaan_M9, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Chery".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_chery, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_chery_or_changan_karvaan_M9, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("C37".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_DFSK_C37, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_DFSK_C37_or_changan_alsvin_or_honda_city, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Glory 580".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_DFSK_Glory_580, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_DFSK_glory_or_honda_airwave, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Atrai Wagon".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Atari_Wagon, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Bego".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Bego, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Copen".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Copen, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Cuore".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Cuore, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_cuore, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Hijet".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Hijet, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Mira".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Mira, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Move".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Move, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Move Conte".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daihatsu_Move_Conte, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Daewoo".equals(carMakeItemSelected)
                || "Higer".equals(carMakeItemSelected)
                || "Yutong".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Daewoo_Standard, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daewoo_or_higer_or_yutong_Standard, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("V2".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_FAW_V2, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_FAW_V2, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("X-PV".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_FAW_X_PV, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_FAW_XPV, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("GMC".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_GMC_Sierra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_GMC, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Haval".equals(carMakeItemSelected)) {
            if ("H6".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Haval_H6, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);


            } else if ("Jolion".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Haval_Jolion, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

            }
            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_haval, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Accord".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_Accord, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_honda_accord, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Airwave".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_Airwave, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_DFSK_glory_or_honda_airwave, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("BR-V".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_BR_V, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("CR-Z".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_CR_Z, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("City".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_City, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_DFSK_C37_or_changan_alsvin_or_honda_city,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Civic".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_Civic, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_honda_civic, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Fit".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_Fit, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("N One".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_N_One, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("N Wagon".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_N_Wagon, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Vezel".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Honda_Vezel, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_BMW_X1_or_honda_vezel, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Elantra".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Elantra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_elantra, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Grand Starex".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Grand_Starex, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_grand_starex, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Santro".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Santro, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_santro, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Shehzore".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Shehzore, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_shehzore, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Sonata".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Sonata, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_sonata, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Tucson".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Hyundai_Tucson, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_hyundai_tucson, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Isuzu".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Isuzu_D_Max, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_isuzu, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("JW".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_JW_Forland, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_JW, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Carnival".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_KIA_Carnival, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_KIA_carnival, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Picanto".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_KIA_Picanto, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_KIA_picanto, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Spectra".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_KIA_Spectra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_KIA_spectra, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Sportage".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_KIA_Sportage, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_KIA_sportage, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Stonic".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_KIA_Stonic, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_KIA_stonic, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Land Rover".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Land_Rover_Range_Rover, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_land_rover, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("CT200h".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Lexus_CT200h, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_lexus_CT200h, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("LX Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Lexus_LX_Series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_lexus_LX_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("RX Series".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Lexus_RX_Series, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_lexus_RX_series, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("C Class".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mercedes_Benz_C_Class, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_mercedes_benz_C_class, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("E Class".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mercedes_Benz_E_Class, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_mercedes_benz_E_class, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Panther".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mercedes_Benz_Panther, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_mercedes_benz_panther, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("S Class".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mercedes_Benz_S_Class, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_mercedes_benz_S_class, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("HS".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_MG_HS, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("ZS".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_MG_ZS, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_MG_ZS, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("ZS EV".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_MG_ZS_EV, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_MG_ZS_EV, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("B2200".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mazda_B2200, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Mazda_B2200, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("RX8".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mazda_RX8, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Mazda_RX8, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Scrum".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mazda_Scrum, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Mitsubishi".equals(carMakeItemSelected)) {
            if ("EK Wagon".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mitsubishi_Ek_Wagon, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                        android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Hiace".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mitsubishi_Hiace, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Mitsubishi_hiace, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Lancer".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mitsubishi_Lancer, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Mitsubishi_lancer, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Pajero".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mitsubishi_Pajero, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Mitsubishi_pajero, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Pajero-Mini".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Mitsubishi_Pajero_Mini, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                        android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            }

        } else if ("350 Z".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_350_Z, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_nissan_350_Z, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("AD Van".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_AD_Van, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_nissan_AD_van_sunny, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Clipper".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_Clipper, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Dayz".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_Dayz, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_atrai_wagon_or_copen_or_hijet_or_mira_or_move_or_move_conte_or_honda_N_One_Wagon_or_mazda_scrum_or_mitsubishi_EK_wagon_pajero_mini_or_nissan_clipper_dayz,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Juke".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_Juke, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_daihatsu_bego_or_honda_BRV_CRZ_Fit_or_MG_HS_or_nissan_juke,
                    android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Sunny".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Nissan_Sunny, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_nissan_AD_van_sunny, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Saga".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Proton_Saga, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_proton_saga, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("X70".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Proton_X70, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_proton_X70, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("K07".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Prince_K07, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_prince_K07, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Pearl".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Prince_Pearl, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_prince_pearl, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Phantom".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Rolls_Royce_Phantom, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_rolls_royce, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("APV".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_APV, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_APV, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Ghost".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_varient_ghost, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_ghost, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Alto".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Alto, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Alto, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Baleno".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Baleno, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Baleno, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Bolan".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Bolan, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Bolan, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Ciaz".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Ciaz, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Ciaz, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Cultus".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Cultus, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Cultus, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Every".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Every, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Every, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Khyber".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Khyber, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Khyber, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Liana".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Liana, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Liana, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Margalla".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Margalla, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Margalla, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Mehran".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Mehran, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Mehran, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Ravi".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Ravi, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Ravi, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Swift".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Swift, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Swift, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Wagon R".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Suzuki_Wagon_R, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_Suzuki_Wagon_R, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Toyota".equals(carMakeItemSelected)) {
            if ("Aqua".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Aqua, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Aqua, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Coaster".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Coaster, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Coaster, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Corolla".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Corolla, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Corolla, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Corolla-Axio".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Corolla_Axio, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Corolla_Axio, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Corolla-Fielder".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Corolla_Fielder, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Corolla_Fielder, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Crown".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Crown, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Crown, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Fortuner".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Fortuner, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Fortuner, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Hiace".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Hiace, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Hiace, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Hilux".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Hilux, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Hilux, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Land Cruiser".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Land_Cruiser, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Land_Cruiser, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Mark X".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Mark_X, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Mark_X, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Noah".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Noah, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Noah, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Passo".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Passo, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Passo, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Platz".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Platz, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Platz, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Prado".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Prado, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Prado, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Premio".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Premio, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Premio, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Prius".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Prius, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Prius, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Probox".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Probox, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Probox, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Rush".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Rush, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Rush, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Surf".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Surf, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Surf, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Tundra".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Tundra, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Tundra, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Vitz".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Vitz, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Vitz, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            } else if ("Yaris".equals(carModelItemSelected)) {
                ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_Toyota_Yaris, android.R.layout.simple_spinner_item);
                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carVariant.setAdapter(updatedAdapter);

                ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.engine_capacity_Toyota_Yaris, android.R.layout.simple_spinner_item);
                updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                engineCapacity.setAdapter(updatedEngineCapacityAdapter);

            }

        } else if ("Alpha".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_United_Alpha, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_United_Alpha, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        } else if ("Bravo".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_variant_United_Bravo, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carVariant.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updatedEngineCapacityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.engine_capacity_United_Bravo, android.R.layout.simple_spinner_item);
            updatedEngineCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineCapacity.setAdapter(updatedEngineCapacityAdapter);

        }

    }

    private void updateBodyColor(String carMakeItemSelected, String carModelItemSelected) {
        if ("A3".equals(carModelItemSelected)
                || "A4".equals(carModelItemSelected)
                || "A5".equals(carModelItemSelected)
                || "A6".equals(carModelItemSelected)
                || "Q7".equals(carModelItemSelected)
                || "5 Series".equals(carModelItemSelected)
                || "7 Series".equals(carModelItemSelected)
                || "X1".equals(carModelItemSelected)
                || "Grand Starex".equals(carModelItemSelected)
                || "Santro".equals(carModelItemSelected)
                || "Bravo".equals(carModelItemSelected)
                || "Tucson".equals(carModelItemSelected)
                || "Karvaan".equals(carModelItemSelected)
                || "X5 Series".equals(carModelItemSelected)
                || "e-tron".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedBodyColorAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_one, android.R.layout.simple_spinner_item);
            updatedBodyColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedBodyColorAdapter);

        } else if ("e-tron GT".equals(carModelItemSelected)
                || "e-tron GT RS".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedBodyColorAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_e_tron_GT, android.R.layout.simple_spinner_item);
            updatedBodyColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedBodyColorAdapter);

        } else if ("3 Series".equals(carModelItemSelected)
                || "Alsvin".equals(carModelItemSelected)
                || "Spectra".equals(carModelItemSelected)
                || "HS".equals(carModelItemSelected)
                || "M9".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_two, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("Chery".equals(carMakeItemSelected)
                || "Daewoo".equals(carMakeItemSelected)
                || "Rolls-Royce".equals(carMakeItemSelected)
                || "Yutong".equals(carMakeItemSelected)
                || "Mitsubishi".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_one, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("Higer".equals(carMakeItemSelected)
                || "FAW".equals(carMakeItemSelected)
                || "Prince".equals(carMakeItemSelected)
                || "Lexus".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_elantra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("DFSK".equals(carMakeItemSelected)
                || "Daihatsu".equals(carMakeItemSelected)
                || "Honda".equals(carMakeItemSelected)
                || "Isuzu".equals(carMakeItemSelected)
                || "Toyota".equals(carMakeItemSelected)
                || "Nissan".equals(carMakeItemSelected)
                || "Suzuki".equals(carMakeItemSelected)
                || "Land Rover".equals(carMakeItemSelected)
                || "Mercedes-Benz".equals(carMakeItemSelected)
                || "GMC".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_two, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("H6".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_haval_h6, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Jolion".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_haval_jolion, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Elantra".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_elantra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Shehzore".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_shehzore, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Sonata".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_sonata, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("JW".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_JW, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("Carnival".equals(carModelItemSelected)
                || "Picanto".equals(carModelItemSelected)
                || "Sportage".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_elantra, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Stonic".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_stonic, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("ZS".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_ZS, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("ZS EV".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_ZS_EV, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Mazda".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_three, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);

        } else if ("Saga".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_proton_saga, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("X70".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_proton_x70, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        } else if ("Alpha".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.body_color_united_alpha, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bodyColor.setAdapter(updatedAdapter);
        }


    }

    private void updateEngineType(String carMakeItemSelected, String carModelItemSelected) {

        if ("A3".equals(carModelItemSelected)
                || "A4".equals(carModelItemSelected) || "A5".equals(carModelItemSelected) || "Atrai Wagon".equals(carModelItemSelected)
                || "Bego".equals(carModelItemSelected) || "Hijet".equals(carModelItemSelected) || "Move".equals(carModelItemSelected)
                || "BR-V".equals(carModelItemSelected) || "HS".equals(carModelItemSelected) || "ZS".equals(carModelItemSelected)
                || "Mark X".equals(carModelItemSelected) || "Rush".equals(carModelItemSelected) || "Yaris".equals(carModelItemSelected)
                || "Alpha".equals(carModelItemSelected) || "Carnival".equals(carModelItemSelected) || "Picanto".equals(carModelItemSelected)
                || "Sportage".equals(carModelItemSelected) || "Stonic".equals(carModelItemSelected) || "Ciaz".equals(carModelItemSelected)
                || "Elantra".equals(carModelItemSelected) || "RX8".equals(carModelItemSelected) || "350 Z".equals(carModelItemSelected)
                || "Changan".equals(carMakeItemSelected)
                || "Chery".equals(carMakeItemSelected) || "GMC".equals(carMakeItemSelected) || "Haval".equals(carMakeItemSelected)
                || "DFSK".equals(carMakeItemSelected) || "Rolls-Royce".equals(carMakeItemSelected) || "Prince".equals(carMakeItemSelected)
                || "EK Wagon".equals(carModelItemSelected) || "Pajero-Mini".equals(carModelItemSelected) || "Sonata".equals(carModelItemSelected)
                || "Tucson".equals(carModelItemSelected) || "N One".equals(carModelItemSelected) || "N Wagon".equals(carModelItemSelected)
                || "Move Conte".equals(carModelItemSelected) || "Copen".equals(carModelItemSelected) || "Proton".equals(carMakeItemSelected)) {

            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("A6".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_hybrid_electric, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Q7".equals(carModelItemSelected) || "Shehzore".equals(carModelItemSelected) || "JW".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_diesel, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("e-tron".equals(carModelItemSelected) || "e-tron GT".equals(carModelItemSelected)
                || "ZS EV".equals(carModelItemSelected) || "e-tron GT RS".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_electric, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("3 Series".equals(carModelItemSelected)
                || "5 Series".equals(carModelItemSelected) || "AD Van".equals(carModelItemSelected) || "Grand Starex".equals(carModelItemSelected)
                || "C Class".equals(carModelItemSelected) || "Hiace".equals(carModelItemSelected) || "Coaster".equals(carModelItemSelected)
                || "Fortuner".equals(carModelItemSelected) || "Land Cruiser".equals(carModelItemSelected) || "Prado".equals(carModelItemSelected)
                || "Premio".equals(carModelItemSelected) || "Surf".equals(carModelItemSelected) || "Tundra".equals(carModelItemSelected)
                || "Hilux".equals(carModelItemSelected) || "Pajero".equals(carModelItemSelected) || "B2200".equals(carModelItemSelected)
                || "Daewoo".equals(carMakeItemSelected) || "Higer".equals(carMakeItemSelected) || "Yutong".equals(carMakeItemSelected)
                || "Panther".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("7 Series".equals(carModelItemSelected) || "X1".equals(carModelItemSelected) || "X5 Series".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel_hybrid, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Cuore".equals(carModelItemSelected)
                || "FAW".equals(carMakeItemSelected)
                || "Mira".equals(carModelItemSelected) || "Clipper".equals(carModelItemSelected) || "Dayz".equals(carModelItemSelected)
                || "APV".equals(carModelItemSelected) || "Alto".equals(carModelItemSelected) || "Cultus".equals(carModelItemSelected)
                || "Every".equals(carModelItemSelected) || "Khyber".equals(carModelItemSelected) || "Liana".equals(carModelItemSelected)
                || "Margalla".equals(carModelItemSelected) || "Mehran".equals(carModelItemSelected) || "Ravi".equals(carModelItemSelected)
                || "Swift".equals(carModelItemSelected) || "Passo".equals(carModelItemSelected) || "Scrum".equals(carModelItemSelected)
                || "Lancer".equals(carModelItemSelected) || "Platz".equals(carModelItemSelected) || "Spectra".equals(carModelItemSelected)
                || "Bravo".equals(carModelItemSelected) || "City".equals(carModelItemSelected) || "Civic".equals(carModelItemSelected)
                || "Probox".equals(carModelItemSelected) || "Santro".equals(carModelItemSelected) || "Wagon R".equals(carModelItemSelected)
                || "Bolan".equals(carModelItemSelected) || "Baleno".equals(carModelItemSelected) || "Sunny".equals(carModelItemSelected)) {

            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_CNG, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Accord".equals(carModelItemSelected) || "Airwave".equals(carModelItemSelected) || "Fit".equals(carModelItemSelected)
                || "Aqua".equals(carModelItemSelected) || "Corolla-Axio".equals(carModelItemSelected) || "Crown".equals(carModelItemSelected)
                || "Juke".equals(carModelItemSelected) || "Noah".equals(carModelItemSelected) || "Prius".equals(carModelItemSelected)
                || "Vezel".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_hybrid, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("CR-Z".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_hybrid_electric, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Isuzu".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Land Rover".equals(carMakeItemSelected) || "Lexus".equals(carMakeItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel_hybrid, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("E Class".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel_hybrid, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("S Class".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_diesel_hybrid_electric, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Corolla".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_CNG_diesel, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        } else if ("Corolla-Fielder".equals(carModelItemSelected) || "Vitz".equals(carModelItemSelected)) {
            ArrayAdapter<CharSequence> updatedEngineTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.engine_type_petrol_CNG_hybrid, android.R.layout.simple_spinner_item);
            updatedEngineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            engineType.setAdapter(updatedEngineTypeAdapter);

        }


    }

    private void updateAccordingToCarMake(AdapterView<?> parent) {
        String selectedItem = (String) parent.getSelectedItem();

        if ("Audi".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_audi, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("BMW".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_BMW, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);


        } else if ("Changan".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_changan, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Chery".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_chery, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("DFSK".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_DFSK, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Daihatsu".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_daihatsu, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Daewoo".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_Daewoo, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("FAW".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_FAW, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("GMC".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_GMC, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Haval".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_haval, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Higer".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_higer, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Honda".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_Honda, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Hyundai".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_hyundai, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Isuzu".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_isuzu, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("JW".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_JW, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("KIA".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_KIA, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Land Rover".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_land_rover, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Lexus".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_lexus, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Mercedes-Benz".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_mercedes_benz, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("MG".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_MG, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Mazda".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_mazda, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Mitsubishi".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_mitsubishi, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Nissan".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_nissan, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Proton".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_proton, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_5, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Prince".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_prince, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Rolls-Royce".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_rolls_royce, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Suzuki".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_suzuki, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("Toyota".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_toyota, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        } else if ("United".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_united, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

            ArrayAdapter<CharSequence> updateSeatingCapacityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seating_capacity_4, android.R.layout.simple_spinner_item);
            updateSeatingCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seatingCapacity.setAdapter(updateSeatingCapacityAdapter);

        } else if ("Yutong".equals(selectedItem)) {
            ArrayAdapter<CharSequence> updatedAdapter = ArrayAdapter.createFromResource(getContext(), R.array.car_model_yutong, android.R.layout.simple_spinner_item);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carModel.setAdapter(updatedAdapter);

        }

    }


    private void spinnerTextAppearance(AdapterView<?> parent, View view) {

        for (int i = 0; i < parent.getChildCount(); i++) {
            TextView textView = (TextView) parent.getChildAt(i);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(12);
            textView.setLetterSpacing(.05F);
        }
        ((TextView) view).setTypeface(customFont, Typeface.BOLD);
    }

    private class CarTransmissionItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class EngineTypeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class BodyColorItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class CarVariantItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class EngineCapacityItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class CarTypeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class DriverAvailabilityItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class RegisterCityItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class InsuranceItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class SeatingCapacityItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTextAppearance(parent, view);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public static void getAllUserAds() {
        testArray.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("allUserAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        String key = user.getKey();
                        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userData : snapshot.getChildren()) {
                                    GetAddModel adsData = userData.getValue(GetAddModel.class);
                                    testArray.add(adsData);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}