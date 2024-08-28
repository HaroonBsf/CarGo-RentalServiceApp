package com.example.fyp.OtherFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.R;
import com.google.android.material.button.MaterialButton;

public class HelpFragment extends Fragment {

    ConstraintLayout btnHowDoRegister, btnHowDoBooking, btnHowUploadAds, btnHowConvertAccount, btnHowNearestCar;
    ImageView ivRegistrationArrow, ivBookingArrow, ivUploadArrow, ivConvertAccountArrow, ivNearestCarArrow;
    TextView tvRegistrationDetails, tvBookingDetails, tvUploadDetails, tvConvertAccountDetails, tvNearestCarDetails;
    ImageView ivEmailSupport, ivCallSupport, ivWhatsAppSupport, ivHintsSupport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Expandable Text
        btnHowDoRegister = view.findViewById(R.id.clHowDoRegister);
        tvRegistrationDetails = view.findViewById(R.id.tvRegistrationDetails);
        ivRegistrationArrow = view.findViewById(R.id.ivRegistrationArrow);
        btnHowDoBooking = view.findViewById(R.id.clHowDoBooking);
        tvBookingDetails = view.findViewById(R.id.tvBookingDetails);
        ivBookingArrow = view.findViewById(R.id.ivBookingArrow);
        btnHowUploadAds = view.findViewById(R.id.clHowUploadAds);
        tvUploadDetails = view.findViewById(R.id.tvUploadDetails);
        ivUploadArrow = view.findViewById(R.id.ivUploadArrow);
        btnHowConvertAccount = view.findViewById(R.id.clHowConvertAccount);
        tvConvertAccountDetails = view.findViewById(R.id.tvConvertAccountDetails);
        ivConvertAccountArrow = view.findViewById(R.id.ivConvertAccountArrow);
        btnHowNearestCar = view.findViewById(R.id.clHowNearestCar);
        tvNearestCarDetails = view.findViewById(R.id.tvNearestCarDetails);
        ivNearestCarArrow = view.findViewById(R.id.ivNearestCarArrow);

//        Support & HelpLine
        ivEmailSupport = view.findViewById(R.id.ivEmailSupport);
        ivCallSupport = view.findViewById(R.id.ivCallSupport);
        ivWhatsAppSupport = view.findViewById(R.id.ivWhatsAppSupport);
        ivHintsSupport = view.findViewById(R.id.ivHintsSupport);

        btnHowDoRegister.setOnClickListener(v -> expandableText(tvRegistrationDetails, ivRegistrationArrow));
        btnHowDoBooking.setOnClickListener(v -> expandableText(tvBookingDetails, ivBookingArrow));
        btnHowUploadAds.setOnClickListener(v -> expandableText(tvUploadDetails, ivUploadArrow));
        btnHowConvertAccount.setOnClickListener(v -> expandableText(tvConvertAccountDetails, ivConvertAccountArrow));
        btnHowNearestCar.setOnClickListener(v -> expandableText(tvNearestCarDetails, ivNearestCarArrow));

        ivEmailSupport.setOnClickListener(v -> openGmail());
        ivCallSupport.setOnClickListener(v -> openPhoneDailer());
        ivWhatsAppSupport.setOnClickListener(v -> openWhatsApp());
        ivHintsSupport.setOnClickListener(v -> startGuidedTour());

    }

    private void startGuidedTour() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_hint_support, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout btnCancelCharges = dialogView.findViewById(R.id.btnCancelTour);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        MaterialButton btnCallHelpCenter = dialogView.findViewById(R.id.btnStartTour);
        btnCallHelpCenter.setOnClickListener(v -> {
            savePreferences();
            getActivity().finish();
            startActivity(getActivity().getIntent());
            getActivity().overridePendingTransition(0, 0);
        });
        btnCancelCharges.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void savePreferences() {
//        Update Location Preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isTargetFirstTime", true);
        editor.apply();
    }

    private void openWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + "+923015955488"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPhoneDailer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_call_support, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.SlideLeftRightDialog);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout btnCancelCharges = dialogView.findViewById(R.id.btnCancelCharges);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        MaterialButton btnCallHelpCenter = dialogView.findViewById(R.id.btnCallHelpCenter);
        btnCallHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "+923015955488"));
            startActivity(intent);
            dialog.dismiss();
        });
        btnCancelCharges.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openGmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + "bsf2002739@ue.edu.pk"));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No app found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    private void expandableText(TextView expand, ImageView arrow) {
        if (expand.getVisibility() == View.GONE) {
            expand.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.ic_up_arrow);
        } else {
            expand.setVisibility(View.GONE);
            arrow.setImageResource(R.drawable.ic_arrow_down);
        }


    }
}