package com.example.fyp.Login;

import static com.example.fyp.network.ConnectionReceiver.isConnected;
import static com.example.fyp.util.NetworkDialogUtils.showNetworkDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.example.fyp.main.MainActivity;
import com.example.fyp.R;
import com.example.fyp.constant.Constant;
import com.example.fyp.models.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    PinView pinView;
    TextView resendOTP, OTPNumber, countDown, tvReceivedOTP;
    MaterialButton confirm;
    ProgressBar progressBar;
    private String verificationId;
    FirebaseAuth fAuth;
    String name, company, email, CNIC, phone, password;
    LinearLayout otpLayout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        pinView = findViewById(R.id.pin_view);
        resendOTP = findViewById(R.id.tvResendOTP);
        confirm = findViewById(R.id.btnConfirm);
        progressBar = findViewById(R.id.ConfirmProgressBar);
        OTPNumber = findViewById(R.id.tvNumber);
        countDown = findViewById(R.id.tvCountDown);
        tvReceivedOTP = findViewById(R.id.tvReceivedOTP);
        otpLayout = findViewById(R.id.otpLayout);

        fAuth = FirebaseAuth.getInstance();

        name = getIntent().getStringExtra("name");
        company = getIntent().getStringExtra("company");
        email = getIntent().getStringExtra("email");
        CNIC = getIntent().getStringExtra("CNIC");
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");

        OTPNumber.setText(phone);
        setResendOTP();

        pinView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        verificationId = getIntent().getStringExtra("verificationId");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinView.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.otpLayout), "Please enter valid code", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                } else if (!isConnected(getApplicationContext())) {
                    showNetworkDialog(OTPVerification.this);
                    return;
                }
                String code = pinView.getText().toString();
                if (verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        createUser();

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        confirm.setVisibility(View.VISIBLE);
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.otpLayout), "Invalid Code", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,
                        60,
                        TimeUnit.SECONDS,
                        OTPVerification.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(OTPVerification.this, "OTP sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                setResendOTP();

            }
        });


    }

    private void setResendOTP() {
        resendOTP.setVisibility(View.GONE);
        tvReceivedOTP.setVisibility(View.INVISIBLE);
        countDown.setVisibility(View.VISIBLE);
        new CountDownTimer(90000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                countDown.setText("Time Remaining: " + f.format(min) + ":" + f.format(sec) + "s");

            }

            public void onFinish() {
                resendOTP.setVisibility(View.VISIBLE);
                tvReceivedOTP.setVisibility(View.VISIBLE);
                countDown.setVisibility(View.GONE);
            }
        }.start();
    }

    private void createUser() {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OTPVerification.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name, company, email, CNIC, phone, UUID, "offline");

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                            if (UUID != null) {
                                databaseReference.child(UUID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Constant.userData = writeUserDetails;
                                            Toast.makeText(OTPVerification.this, "Registered successfully 😍", Toast.LENGTH_SHORT).show();
                                            startActivitySecond();
                                            finish();

                                        } else {
                                            Snackbar snackbar = Snackbar.make(findViewById(R.id.otpLayout), "Failed, Please try again", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(OTPVerification.this, "null uuid", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.otpLayout), "Verify that no one else is using the email.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                            confirm.setVisibility(View.VISIBLE);
                        }

                    }
                });

    }

    private void startActivitySecond() {
        Intent intent = new Intent(OTPVerification.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (countDown.getVisibility() == View.VISIBLE) {
            return;
        }

        super.onBackPressed();
    }
}