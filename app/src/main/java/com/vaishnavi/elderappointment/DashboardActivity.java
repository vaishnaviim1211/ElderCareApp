package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    TextView upcomingCountText, reminderSubtitle, pendingMedsText, waterCountDashboard, waterQuote;
    TextView greetingText, userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        greetingText = findViewById(R.id.greetingText);
        userNameText = findViewById(R.id.userNameText);
        upcomingCountText = findViewById(R.id.upcomingCountText);
        reminderSubtitle = findViewById(R.id.reminderSubtitle);
        pendingMedsText = findViewById(R.id.pendingMedsText);
        waterCountDashboard = findViewById(R.id.waterCountDashboard);
        waterQuote = findViewById(R.id.waterQuote);

        // SOS and Quick Action Cards
        Button sosButton = findViewById(R.id.sosButton);
        if (sosButton != null) {
            sosButton.setOnClickListener(v -> openEmergency());
        }

        CardView appointmentCardQuick = findViewById(R.id.appointmentCardQuick);
        if (appointmentCardQuick != null) {
            appointmentCardQuick.setOnClickListener(v -> openAppointments());
        }

        CardView medicineCardQuick = findViewById(R.id.medicineCardQuick);
        if (medicineCardQuick != null) {
            medicineCardQuick.setOnClickListener(v -> openMedicines());
        }

        CardView emergencyCardQuick = findViewById(R.id.emergencyCardQuick);
        if (emergencyCardQuick != null) {
            emergencyCardQuick.setOnClickListener(v -> openEmergency());
        }

        CardView healthTrackerCardQuick = findViewById(R.id.healthTrackerCardQuick);
        if (healthTrackerCardQuick != null) {
            healthTrackerCardQuick.setOnClickListener(v -> openHealthTracker());
        }

        // Main Detailed Cards
        CardView appointmentCard = findViewById(R.id.appointmentCard);
        if (appointmentCard != null) {
            appointmentCard.setOnClickListener(v -> openAppointments());
        }

        CardView medicineCard = findViewById(R.id.medicineCard);
        if (medicineCard != null) {
            medicineCard.setOnClickListener(v -> openMedicines());
        }

        CardView healthCard = findViewById(R.id.healthCard);
        if (healthCard != null) {
            healthCard.setOnClickListener(v -> openHealthTracker());
        }

        CardView waterCard = findViewById(R.id.waterCard);
        if (waterCard != null) {
            waterCard.setOnClickListener(v -> openHealthTracker());
        }

        CardView profileCard = findViewById(R.id.profileCard);
        if (profileCard != null) {
            profileCard.setOnClickListener(v -> openProfile());
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_appointments) {
                openAppointments();
                return true;
            } else if (itemId == R.id.nav_medicine) {
                openMedicines();
                return true;
            } else if (itemId == R.id.nav_health) {
                openHealthTracker();
                return true;
            } else if (itemId == R.id.nav_profile) {
                openProfile();
                return true;
            }
            return true;
        });

        updateUI();
    }

    private void updateUI() {
        // Dynamic Greeting
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay < 12) {
            greetingText.setText("Good Morning");
        } else if (timeOfDay < 16) {
            greetingText.setText("Good Afternoon");
        } else if (timeOfDay < 21) {
            greetingText.setText("Good Evening");
        } else {
            greetingText.setText("Good Night");
        }

        // Set Login Name
        userNameText.setText(MainActivity.loggedInUserName);

        // Appointments
        int apptCount = AppointmentActivity.appointments.size();
        upcomingCountText.setText(apptCount + " upcoming");

        // Medicines
        int pendingCount = 0;
        for (MedicineModel med : MedicineActivity.medicines) {
            if (!med.isChecked()) {
                pendingCount++;
            }
        }
        pendingMedsText.setText(pendingCount + " pending");

        // Water
        if (waterCountDashboard != null) {
            waterCountDashboard.setText(HealthTrackerActivity.waterGlasses + " / " + HealthTrackerActivity.WATER_GOAL + " glasses");
        }
        
        if (waterQuote != null) {
            if (HealthTrackerActivity.waterGlasses == 0) {
                waterQuote.setText("Start your day with a glass of water!");
            } else if (HealthTrackerActivity.waterGlasses < 4) {
                waterQuote.setText("Keep going! Hydration is key.");
            } else if (HealthTrackerActivity.waterGlasses < HealthTrackerActivity.WATER_GOAL) {
                waterQuote.setText("Almost there! Drink up.");
            } else {
                waterQuote.setText("Goal reached! You're fully hydrated.");
            }
        }

        if (apptCount > 0) {
            String lastAppointment = AppointmentActivity.appointments.get(apptCount - 1);
            String[] parts = lastAppointment.split("\n");
            String doctor = parts[0];
            String time = (parts.length > 1) ? parts[1] : "";
            reminderSubtitle.setText("Next: " + doctor + " (" + time + ")");
        } else {
            reminderSubtitle.setText(R.string.nothing_scheduled);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void openAppointments() {
        startActivity(new Intent(DashboardActivity.this, AppointmentActivity.class));
    }

    private void openMedicines() {
        startActivity(new Intent(DashboardActivity.this, MedicineActivity.class));
    }

    private void openEmergency() {
        startActivity(new Intent(DashboardActivity.this, EmergencyActivity.class));
    }

    private void openHealthTracker() {
        startActivity(new Intent(DashboardActivity.this, HealthTrackerActivity.class));
    }

    private void openProfile() {
        startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
    }
}