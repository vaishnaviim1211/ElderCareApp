package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput;
    Button registerBtn;
    TextView loginLink;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    // CORRECT URL for asia-south1 (Mumbai)
    private static final String DB_URL = "https://eldercare-20d5f-default-rtdb.asia-south1.firebasedatabase.app/";
    private Handler timeoutHandler;
    private Runnable authTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
        } catch (Exception e) {
            Log.e("Firebase", "Init error", e);
            Toast.makeText(this, "Firebase Setup Error", Toast.LENGTH_LONG).show();
        }

        nameInput = findViewById(R.id.regName);
        emailInput = findViewById(R.id.regEmail);
        passwordInput = findViewById(R.id.regPassword);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);

        timeoutHandler = new Handler(Looper.getMainLooper());

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isConnectedToInternet()) {
                Toast.makeText(RegisterActivity.this, "No internet connection. Please turn on Wi-Fi or mobile data.", Toast.LENGTH_LONG).show();
                return;
            }

            if (mAuth == null) {
                Toast.makeText(RegisterActivity.this, "Firebase is not initialized. Check google-services.json and restart the app.", Toast.LENGTH_LONG).show();
                return;
            }

            registerBtn.setEnabled(false);
            registerBtn.setText(R.string.registering);

            authTimeout = () -> {
                registerBtn.setEnabled(true);
                registerBtn.setText(R.string.register);
                Toast.makeText(RegisterActivity.this,
                        "Registration is taking too long. Check your internet connection and try again.",
                        Toast.LENGTH_LONG).show();
            };
            timeoutHandler.postDelayed(authTimeout, 20000);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        timeoutHandler.removeCallbacks(authTimeout);

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Registration is successful once Firebase Auth succeeds.
                            // Do not wait for the Realtime Database write before opening the dashboard.
                            MainActivity.loggedInUserName = name;
                            MainActivity.loggedInUserEmail = email;

                            if (user != null && mDatabase != null) {
                                HashMap<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", email);

                                mDatabase.child("users").child(user.getUid()).child("profile")
                                        .setValue(userData)
                                        .addOnFailureListener(e ->
                                                Log.e("DB", "Profile write failed after registration", e));
                            }

                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            registerBtn.setEnabled(true);
                            registerBtn.setText(R.string.register);
                            Exception exception = task.getException();

                            if (exception != null) {
                                String error = exception.getMessage();
                                Log.e("Auth", "Registration failed", exception);

                                if (error.toLowerCase().contains("email address is already in use")) {
                                    Toast.makeText(RegisterActivity.this,
                                            "This email is already registered. Please log in instead.",
                                            Toast.LENGTH_LONG).show();
                                } else if (error.toLowerCase().contains("network")) {
                                    Toast.makeText(RegisterActivity.this,
                                            "Network error. Check Wi-Fi/mobile data and try again.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed: " + error, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        loginLink.setOnClickListener(v -> finish());
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    protected void onDestroy() {
        if (timeoutHandler != null && authTimeout != null) {
            timeoutHandler.removeCallbacks(authTimeout);
        }
        super.onDestroy();
    }
}