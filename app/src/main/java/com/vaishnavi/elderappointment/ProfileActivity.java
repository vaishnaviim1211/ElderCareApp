package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    public static String age = "--", blood = "--", weight = "--", height = "--", gender = "--", history = "No records added.";
    TextView tvAge, tvBlood, tvWeight, tvHeight, tvGender, tvHistory, profileName, profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        tvAge = findViewById(R.id.valAge);
        tvBlood = findViewById(R.id.valBlood);
        tvWeight = findViewById(R.id.valWeight);
        tvHeight = findViewById(R.id.valHeight);
        tvGender = findViewById(R.id.valGender);
        tvHistory = findViewById(R.id.medicalHistory);

        Button editProfileBtn = findViewById(R.id.editProfileBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        editProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_appointments) {
                startActivity(new Intent(this, AppointmentActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_medicine) {
                startActivity(new Intent(this, MedicineActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_health) {
                startActivity(new Intent(this, HealthTrackerActivity.class));
                finish();
                return true;
            }
            return true;
        });

        updateUI();
    }

    private void updateUI() {
        profileName.setText(MainActivity.loggedInUserName);
        profileEmail.setText(MainActivity.loggedInUserEmail);
        tvAge.setText(age);
        tvBlood.setText(blood);
        tvWeight.setText(weight);
        tvHeight.setText(height);
        tvGender.setText(gender);
        tvHistory.setText(history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}