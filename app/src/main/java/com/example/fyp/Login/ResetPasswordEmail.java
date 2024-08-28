package com.example.fyp.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordEmail extends AppCompatActivity {

    EditText email;
    MaterialButton send;
    ProgressBar progressBar;
    ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_email);

        email = findViewById(R.id.etResetEmail);
        send = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.resetProgressBar);
        back = findViewById(R.id.ivBackLogin);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPasswordEmail.this, LoginScreen.class));
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().trim().isEmpty()) {
                    email.setError("Enter Email");
                    email.requestFocus();
                    return;
                } else if (!isValidEmail(email.getText().toString().trim())) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                send.setVisibility(View.INVISIBLE);
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ResetPasswordEmail.this, "Email has been sent to your email", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ResetPasswordEmail.this, "Failed! Please try later", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        send.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

    }
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}