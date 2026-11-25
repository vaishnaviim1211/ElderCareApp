package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static String loggedInUserName = "User"; 
    public static String loggedInUserEmail = "user@example.com";
    EditText emailInput, passwordInput;
    Button loginBtn;
    TextView registerLink;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    // CORRECT URL for asia-south1 (Mumbai)
    private static final String DB_URL = "https://eldercare-20d5f-default-rtdb.asia-south1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
            Log.d("Firebase", "Connected to Mumbai DB: " + DB_URL);
        } catch (Exception e) {
            Log.e("Firebase", "Init error", e);
        }

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);

        // Auto-login removed from here to prevent loops, but kept as an option
        /*
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            loggedInUserEmail = currentUser.getEmail();
            fetchUserDataAndNavigate(currentUser.getUid());
        }
        */

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter details", Toast.LENGTH_SHORT).show();
                return;
            }

            loginBtn.setEnabled(false);
            loginBtn.setText("Verifying...");

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                loggedInUserEmail = user.getEmail();
                                fetchUserDataAndNavigate(user.getUid());
                            }
                        } else {
                            loginBtn.setEnabled(true);
                            loginBtn.setText("Login");
                            String error = task.getException() != null ? task.getException().getMessage() : "Auth Failed";
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            Log.e("Auth", "Failed: " + error);
                        }
                    });
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void fetchUserDataAndNavigate(String userId) {
        // We navigate to Dashboard IMMEDIATELY to avoid the "hang"
        // and fetch data in the background
        loggedInUserName = loggedInUserEmail.split("@")[0]; // Fallback name
        
        mDatabase.child("users").child(userId).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loggedInUserName = dataSnapshot.child("name").getValue(String.class);
                }
                // No need to call navigate here as we've already done it below
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Background fetch failed, doesn't matter
            }
        });
        
        // Go to dashboard immediately after successful Auth
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        if (!isFinishing()) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
    }
}