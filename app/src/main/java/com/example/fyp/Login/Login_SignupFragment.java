package com.example.fyp.Login;

import static com.example.fyp.main.MainActivity.NavigationVisibile;
import static com.example.fyp.main.MainActivity.loadFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;;

import com.example.fyp.home.HomeFragment;
import com.example.fyp.R;

public class Login_SignupFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLogin = view.findViewById(R.id.btnFragmentLogin);
        Button btnSignup = view.findViewById(R.id.btnFragmentSignup);
        ImageView goBack = view.findViewById(R.id.iv_back_fragment);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginScreen.class);
                getContext().startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignUp.class);
                getContext().startActivity(intent);

            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(getActivity().getSupportFragmentManager(), new HomeFragment());

                NavigationVisibile();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login__signup, container, false);
    }
}