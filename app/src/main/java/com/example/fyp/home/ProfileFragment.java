package com.example.fyp.home;

import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.bottomNavigationView;
import static com.example.fyp.main.MainActivity.dataProgressBar;
import static com.example.fyp.main.MainActivity.dataProgressView;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.example.fyp.constant.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    ImageView goBack;
    CircleImageView profileImage;
    FloatingActionButton uploadProfile;
    ProgressBar loadProfile;
    ActivityResultLauncher<String> launcher;
    TextView accountType, companyName, profileUsername, profileEmail, profilePhone, profileCNIC;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ConstraintLayout clProfileName, clProfileCNIC, clProfileCompany;
    private boolean isBottomSheetOpen = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goBack = view.findViewById(R.id.ivGoBack);
        accountType = view.findViewById(R.id.tvUserAccount);
        companyName = view.findViewById(R.id.tvProfileCompany);
        profileUsername = view.findViewById(R.id.tvProfileName);
        profileEmail = view.findViewById(R.id.tvProfileEmail);
        profilePhone = view.findViewById(R.id.tvProfilePhone);
        profileCNIC = view.findViewById(R.id.tvProfileCNIC);
        loadProfile = view.findViewById(R.id.spin_kit);
        clProfileName = view.findViewById(R.id.clProfileName);
        clProfileCNIC = view.findViewById(R.id.clProfileCNIC);
        clProfileCompany = view.findViewById(R.id.clProfileCompany);

        profileImage = view.findViewById(R.id.profile_image);
        uploadProfile = view.findViewById(R.id.fabUploadProfile);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(getActivity().getSupportFragmentManager(), new HomeFragment());
                NavigationVisibile();
                bottomNavigationView.setSelectedItemId(R.id.home);


            }
        });
        dataProgressBar.setVisibility(View.VISIBLE);
        dataProgressView.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        DatabaseReference databaseReference = database.getReference("users").child(UUID).child("company");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myCompanyName = snapshot.getValue(String.class);

                if (myCompanyName == null || myCompanyName.isEmpty()) {
                    accountType.setText("User Account");
                    clProfileCompany.setVisibility(View.GONE);

                } else {
                    accountType.setText("Company Account");
                    clProfileCompany.setVisibility(View.VISIBLE);
                    companyName.setText(Constant.userData.company);

                }
                dataProgressBar.setVisibility(View.GONE);
                dataProgressView.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileUsername.setText(Constant.userData.name);
        profileEmail.setText(Constant.userData.email);
        profilePhone.setText(Constant.userData.phone);
        profileCNIC.setText(Constant.userData.cnic);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        @SuppressLint("InflateParams")
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.update_data_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView tvUpdateName = bottomSheetView.findViewById(R.id.tvUpdateName);
        EditText etUpdateProfileData = bottomSheetView.findViewById(R.id.etUpdateProfileData);
        TextView btnSaveUpdatedData = bottomSheetView.findViewById(R.id.btnSaveUpdatedData);
        TextView btnCancelUpdatedData = bottomSheetView.findViewById(R.id.btnCancelUpdatedData);
        clProfileCompany.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (!isBottomSheetOpen) {
                    tvUpdateName.setText("Enter your Company name");
                    etUpdateProfileData.setText(Constant.userData.company);
                    openBottomSheet(bottomSheetDialog, btnCancelUpdatedData, etUpdateProfileData);
                    isBottomSheetOpen = true;
                }
            }
        });
        clProfileName.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (!isBottomSheetOpen) {
                    tvUpdateName.setText("Enter your name");
                    etUpdateProfileData.setText(Constant.userData.name);
                    openBottomSheet(bottomSheetDialog, btnCancelUpdatedData, etUpdateProfileData);
                    isBottomSheetOpen = true;
                }
            }
        });

        clProfileCNIC.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetCNIC = new BottomSheetDialog(getContext());
                @SuppressLint("InflateParams")
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.update_cnic_sheet, null);
                bottomSheetCNIC.setContentView(bottomSheetView);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                EditText etUpdateProfileCNIC = bottomSheetView.findViewById(R.id.etUpdateProfileCNIC);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView btnSaveUpdatedCNIC = bottomSheetView.findViewById(R.id.btnSaveUpdatedCNIC);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView btnCancelUpdatedCNIC = bottomSheetView.findViewById(R.id.btnCancelUpdatedCNIC);
                if (!isBottomSheetOpen) {
                    etUpdateProfileCNIC.setText(Constant.userData.cnic);
                    bottomSheetCNIC.show();
                    etUpdateProfileCNIC.setSelection(etUpdateProfileCNIC.getText().length());
                    etUpdateProfileCNIC.requestFocus();
                    btnCancelUpdatedCNIC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetCNIC.cancel();
                        }
                    });
                    bottomSheetCNIC.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isBottomSheetOpen = false;
                        }
                    });
                    isBottomSheetOpen = true;
                }
            }
        });


        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launcher.launch("image/*");
            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {

                        if (uri != null) {
                            loadProfile.setVisibility(View.VISIBLE);
                            uploadProfile.setVisibility(View.GONE);
                            //profileImage.setImageURI(uri);

                            final StorageReference reference = storage.getReference()
                                    .child("profiles").child(UUID);
                            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference("users").child(UUID).child("profile").setValue(uri.toString());
                                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                            loadProfile.setVisibility(View.GONE);
                                            uploadProfile.setVisibility(View.VISIBLE);
                                        }
                                    });


                                }
                            });

                        }

                    }
                });

        database.getReference("users").child(UUID).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.getValue(String.class);
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openBottomSheet(BottomSheetDialog bottomSheetDialog, TextView btnCancelUpdatedData, EditText etUpdateProfileData) {
        bottomSheetDialog.show();
        etUpdateProfileData.setSelection(etUpdateProfileData.getText().length());
        etUpdateProfileData.requestFocus();
        btnCancelUpdatedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isBottomSheetOpen = false;
            }
        });
    }
}