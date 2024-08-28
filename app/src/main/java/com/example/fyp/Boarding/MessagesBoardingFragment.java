package com.example.fyp.Boarding;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.bottomNavigationView;
import static com.example.fyp.main.MainActivity.loadFragment;
import static com.example.fyp.adapters.GridAdapter.bottomSheetDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.chat.FragmentChatHere;
import com.example.fyp.home.HomeFragment;
import com.example.fyp.R;
import com.google.android.material.button.MaterialButton;

public class MessagesBoardingFragment extends Fragment {

    MaterialButton btnStartChat;
    ImageView ivBackToSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages_boarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartChat = view.findViewById(R.id.btnStartChat);
        ivBackToSheet = view.findViewById(R.id.ivBackToSheet);

        ivBackToSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
                loadFragment(getActivity().getSupportFragmentManager(), new HomeFragment());
                NavigationVisibile();
                bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });

        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(getActivity().getSupportFragmentManager(), new FragmentChatHere());

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("onboardingShown", true);
                editor.apply();
            }
        });

    }
}