package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AppointmentActivity extends AppCompatActivity {

    public static ArrayList<String> appointments = new ArrayList<>();
    LinearLayout listLayout, emptyState;
    ScrollView scrollView;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        listLayout = findViewById(R.id.listLayout);
        emptyState = findViewById(R.id.emptyState);
        scrollView = findViewById(R.id.scrollView);
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddAppointmentActivity.class));
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_appointments);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            }
            return true;
        });

        loadAppointmentsLocally();
    }

    private void loadAppointmentsLocally() {
        appointments.clear();
        String userKey = "appts_" + MainActivity.loggedInUserEmail;
        SharedPreferences prefs = getSharedPreferences("ElderCareData", MODE_PRIVATE);
        String savedData = prefs.getString(userKey, "");

        if (!savedData.isEmpty()) {
            String[] apptArray = savedData.split("###");
            for (String s : apptArray) {
                appointments.add(s);
            }
        }
        displayAppointments();
    }

    private void displayAppointments() {
        if (appointments.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            
            listLayout.removeAllViews();
            for (int i = 0; i < appointments.size(); i++) {
                final int index = i;
                String apptData = appointments.get(i);
                String[] parts = apptData.split("\\|");

                View card = getLayoutInflater().inflate(R.layout.item_appointment, listLayout, false);
                TextView doctorName = card.findViewById(R.id.doctorName);
                TextView dateTime = card.findViewById(R.id.dateTime);
                ImageView btnDelete = card.findViewById(R.id.btnDeleteAppt);
                ImageView btnEdit = card.findViewById(R.id.btnEditAppt);
                
                if (parts.length >= 3) {
                    doctorName.setText(parts[0]);
                    dateTime.setText(parts[1] + " - " + parts[2]);
                } else {
                    doctorName.setText(apptData);
                }

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                        .setTitle("Delete Appointment")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            appointments.remove(index);
                            saveListLocally();
                            displayAppointments();
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
                });

                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(this, AddAppointmentActivity.class);
                    intent.putExtra("editIndex", index);
                    intent.putExtra("data", apptData);
                    startActivity(intent);
                });
                
                listLayout.addView(card);
            }
        }
    }

    private void saveListLocally() {
        String userKey = "appts_" + MainActivity.loggedInUserEmail;
        SharedPreferences prefs = getSharedPreferences("ElderCareData", MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < appointments.size(); i++) {
            sb.append(appointments.get(i));
            if (i < appointments.size() - 1) sb.append("###");
        }
        prefs.edit().putString(userKey, sb.toString()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointmentsLocally();
    }
}