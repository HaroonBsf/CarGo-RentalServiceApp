package com.example.fyp.Boarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.fyp.main.MainActivity;
import com.example.fyp.R;
import com.example.fyp.adapters.BoardingViewPagerAdapter;
import com.example.fyp.models.BoardingItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingOne extends AppCompatActivity {

    ViewPager screenPager;
    BoardingViewPagerAdapter boardingViewPagerAdapter;
    TabLayout tabIndicator;
    ImageView skipToNext;
    MaterialButton getStarted;
    TextView textViewSkip;
    Animation btnAnim;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_one);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        if (restorePrefData()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


        tabIndicator = findViewById(R.id.tabIndicator);
        skipToNext = findViewById(R.id.skip_to_next);
        getStarted = findViewById(R.id.btnGetStarted);
        textViewSkip = findViewById(R.id.tvSkip);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.boarding_btn_anim);

        List<BoardingItem> mList = new ArrayList<>();
        mList.add(new BoardingItem("Pick the best car", "You can choose your favourite car", R.drawable.boarding_three));
        mList.add(new BoardingItem("Get fastest service", "We offer fastest service in your area", R.drawable.boarding_one));
        mList.add(new BoardingItem("Find nearest car rental services", "choose best option!", R.drawable.boarding_two));

        screenPager = findViewById(R.id.viewPager);
        boardingViewPagerAdapter = new BoardingViewPagerAdapter(this, mList);
        screenPager.setAdapter(boardingViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);


        skipToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size() - 1) {
                    loadLastScreen();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                savePrefsData();
                finish();

            }
        });


    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpened = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpened;

    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();

    }

    private void loadLastScreen() {

        skipToNext.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        textViewSkip.setVisibility(View.INVISIBLE);

        getStarted.setAnimation(btnAnim);

    }
}