package com.example.fyp.Login;

import static com.example.fyp.network.ConnectionReceiver.isConnected;
import static com.example.fyp.util.NetworkDialogUtils.showNetworkDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fyp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class SignUp extends AppCompatActivity {

    EditText showPassword, confirm_password, userName, companyName, emailAddress, CNICNumber, phoneNumber;
    MaterialButtonToggleGroup toggleButtonGroup;
    CountryCodePicker codePicker;
    CardView cvCompany;
    TextView login;
    MaterialButton btnIndividual, btnCompany, btnSignUp;
    ProgressBar progressBar;
    Boolean individual = true;
    ConstraintLayout signUpLayout;


    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.etEnterName);
        companyName = findViewById(R.id.etCompany);
        emailAddress = findViewById(R.id.etEnterEmail);
        CNICNumber = findViewById(R.id.etCNIC);
        phoneNumber = findViewById(R.id.etPhoneNumber);
        showPassword = findViewById(R.id.etPassword);
        confirm_password = findViewById(R.id.etConfirmPassword);
        login = findViewById(R.id.tvLog_in);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        toggleButtonGroup = findViewById(R.id.toggleButtonGroup);
        cvCompany = findViewById(R.id.cvCompany);
        btnIndividual = findViewById(R.id.btnIndividual);
        btnCompany = findViewById(R.id.btnCompany);
        codePicker = findViewById(R.id.ccp);
        signUpLayout = findViewById(R.id.signUpLayout);

        btnIndividual.setChecked(true);


        toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btnIndividual) {
                        cvCompany.setVisibility(View.GONE);
                        individual = true;
                    } else if (checkedId == R.id.btnCompany) {
                        cvCompany.setVisibility(View.VISIBLE);
                        individual = false;
                    }
                    companyName.setError(null);
                }
            }
        });

        codePicker.setOnCountryChangeListener(() -> {

            String selectedCountryCode = codePicker.getSelectedCountryCode();
            phoneNumber.setText("+" + selectedCountryCode);


        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
            }
        });

    }

    private void signUp() {
        String name = userName.getText().toString().trim();
        String company = companyName.getText().toString().trim();
        String email = emailAddress.getText().toString().trim();
        String CNIC = CNICNumber.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String password = showPassword.getText().toString().trim();
        String confirmPass = confirm_password.getText().toString().trim();
        String completePhoneNumber = getCompletePhoneNumber();

        if (TextUtils.isEmpty(name)) {
            userName.setError("Enter Name");
            userName.requestFocus();
            return;

        } else if (company.equals("") && !individual) {
            if (!individual) {
                companyName.setError("Enter Company");
                companyName.requestFocus();
            } else {
                companyName.setError(null);
            }
            return;

        } else if (TextUtils.isEmpty(email)) {
            emailAddress.setError("Enter Email");
            emailAddress.requestFocus();
            return;

        }
        if (!isValidEmail(email)) {
            emailAddress.setError("Invalid Email");
            emailAddress.requestFocus();
            return;

        } else if (TextUtils.isEmpty(CNIC)) {
            CNICNumber.setError("Enter CNIC");
            CNICNumber.requestFocus();
            return;

        } else if (CNIC.length() < 13) {
            CNICNumber.setError("Something is Missing");
            CNICNumber.requestFocus();
            return;

        } else if (TextUtils.isEmpty(phone)) {
            phoneNumber.setError("Enter Phone");
            phoneNumber.requestFocus();
            return;

        } else if (!isValidPhoneNumber(phone)) {
            phoneNumber.setError("Not Valid Number");
            phoneNumber.requestFocus();
            return;

        } else if (phone.length() < 6 || phone.length() > 13) {
            phoneNumber.setError("Not Valid Number");
            phoneNumber.requestFocus();
            return;

        } else if (TextUtils.isEmpty(password)) {
            showPassword.setError("Enter Password");
            showPassword.requestFocus();
            return;

        } else if (password.length() < 6) {
            showPassword.setError("Password Must be >= 6 Characters");
            showPassword.requestFocus();
            return;

        } else if (confirmPass.isEmpty() || !password.equals(confirmPass)) {
            confirm_password.setError("Invalid Password");
            confirm_password.requestFocus();
            return;
        } else if (!isConnected(getApplicationContext())) {
            showNetworkDialog(SignUp.this);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean phoneNumberExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = userSnapshot.child("phone").getValue(String.class);
                    String emailAddress = userSnapshot.child("email").getValue(String.class);
                    if (phoneNumber != null && phoneNumber.equals(completePhoneNumber)) {
                        phoneNumberExists = true;
                        break;
                    } else if (emailAddress != null && emailAddress.equals(email)) {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.signUpLayout), "Email address already in use.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        progressBar.setVisibility(View.GONE);
                        btnSignUp.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                if (phoneNumberExists) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.signUpLayout), "Phone number already in use.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.VISIBLE);
                    return;

                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            completePhoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            SignUp.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(SignUp.this, OTPVerification.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("company", company);
                                    intent.putExtra("email", email);
                                    intent.putExtra("CNIC", CNIC);
                                    intent.putExtra("phone", completePhoneNumber);
                                    intent.putExtra("password", password);
                                    intent.putExtra("verificationId", verificationId);
                                    startActivity(intent);
                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUp.this, "Error reading data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getCompletePhoneNumber() {
        String countryCode = codePicker.getSelectedCountryCode();
        String myPhoneNumber = phoneNumber.getText().toString();
        return "+" + countryCode + " " + myPhoneNumber;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void showPass(View view) {

        if (view.getId() == R.id.showPassword) {

            if (showPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hide_password);

                //Show Password
                showPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.show_password);

                //Hide Password
                showPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }

            showPassword.setSelection(showPassword.getText().length());

        }
    }

    public void showConfirmPass(View view) {

        if (view.getId() == R.id.ShowConfirmPassword) {

            if (confirm_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hide_password);

                confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.show_password);

                confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }

            confirm_password.setSelection(confirm_password.getText().length());
        }
    }
}