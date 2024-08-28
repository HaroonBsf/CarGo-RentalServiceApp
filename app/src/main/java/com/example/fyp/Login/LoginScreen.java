package com.example.fyp.Login;

import static com.example.fyp.network.ConnectionReceiver.isConnected;
import static com.example.fyp.util.NetworkDialogUtils.showNetworkDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fyp.constant.Constant;
import com.example.fyp.main.MainActivity;
import com.example.fyp.R;
import com.example.fyp.callback.CallbackData;
import com.example.fyp.models.ReadWriteUserDetails;
import com.example.fyp.network.ConnectionReceiver;
import com.example.fyp.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginScreen extends AppCompatActivity {


    EditText edit_password, emailAddress;
    TextView signup, tvForgetPassword;
    MaterialButton btnLogin;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    BroadcastReceiver broadcastReceiver;
    ConstraintLayout loginLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        edit_password = findViewById(R.id.etLoginPassword);
        emailAddress = findViewById(R.id.etLoginEmail);
        signup = findViewById(R.id.tvSignUp);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        btnLogin = findViewById(R.id.btn_Login);
        progressBar = findViewById(R.id.loginProgressBar);
        loginLayout = findViewById(R.id.loginLayout);

        fAuth = FirebaseAuth.getInstance();

        broadcastReceiver = new ConnectionReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Dialog dialog = new Dialog(this, R.style.NetworkAlertDialog);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailAddress.getText().toString().trim();
                String password = edit_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailAddress.setError("Enter Email");
                    emailAddress.requestFocus();
                    return;
                } else if (!isValidEmail(email)) {
                    emailAddress.setError("Invalid Email");
                    emailAddress.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    edit_password.setError("Enter Password");
                    edit_password.requestFocus();
                    return;
                } else if (password.length() < 6) {
                    edit_password.setError("Password Must be >= 6 Characters");
                    edit_password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Util.getUserDataOnline(new CallbackData() {
                                @Override
                                public void getUserCallback(ReadWriteUserDetails user) {

                                    Constant.userData = user;
                                    Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    startActivitySecond();
                                }
                            });

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "User not found", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "Invalid email or password", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            } else if (!isConnected(getApplicationContext())) {
                                showNetworkDialog(LoginScreen.this);

                            } else {
                                dialog.setContentView(R.layout.custome_dialog_layout);
                                dialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

                                Button btnCancel = dialog.findViewById(R.id.btnDialogCancel);
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "Login Failed", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            }
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, ResetPasswordEmail.class));
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });

    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void startActivitySecond() {
        Intent intent = new Intent(LoginScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void ShowHidePass(View view) {

        if (view.getId() == R.id.ivShowPassword) {

            if (edit_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hide_password);

                //Show Password
                edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.show_password);

                //Hide Password
                edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
            edit_password.setSelection(edit_password.getText().length());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }
}
