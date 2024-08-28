package com.example.fyp.Boarding;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.example.fyp.ads.UpdateAds;
import com.example.fyp.chat.FragmentChatHere;
import com.example.fyp.map.MapsFragment;

public class TrackingBoard extends Fragment {

    Button btnBoardingStartTracking;
    ImageView ivBackToPrivateAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBoardingStartTracking = view.findViewById(R.id.btnBoardingStartTracking);
        ivBackToPrivateAd = view.findViewById(R.id.ivBackToPrivateAd);

        btnBoardingStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(getActivity().getSupportFragmentManager(), new MapsFragment());

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyTrackPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("onboardingTrackShown", true);
                editor.apply();
            }
        });

        ivBackToPrivateAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(getActivity().getSupportFragmentManager(), new UpdateAds());
                NavigationVisibile();
            }
        });

    }
}