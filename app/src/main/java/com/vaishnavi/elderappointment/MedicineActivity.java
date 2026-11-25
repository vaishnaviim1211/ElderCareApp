package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MedicineActivity extends AppCompatActivity {

    public static ArrayList<MedicineModel> medicines = new ArrayList<>();
    LinearLayout listLayout, emptyState;
    ScrollView scrollView;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        listLayout = findViewById(R.id.listLayout);
        emptyState = findViewById(R.id.emptyState);
        scrollView = findViewById(R.id.scrollView);
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicineActivity.class));
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_medicine);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_appointments) {
                startActivity(new Intent(this, AppointmentActivity.class));
                finish();
                return true;
            }
            return true;
        });

        displayMedicines();
    }

    private void displayMedicines() {
        if (medicines.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            
            listLayout.removeAllViews();
            for (MedicineModel med : medicines) {
                View card = getLayoutInflater().inflate(R.layout.item_medicine, listLayout, false);
                TextView medTitle = card.findViewById(R.id.medTitle);
                TextView medDetails = card.findViewById(R.id.medDetails);
                CheckBox checkBox = card.findViewById(R.id.medCheckBox);
                
                medTitle.setText(med.getName());
                medDetails.setText(med.getDetails());
                checkBox.setChecked(med.isChecked());

                // Style based on checked state
                if (med.isChecked()) {
                    medTitle.setAlpha(0.5f);
                    medDetails.setAlpha(0.5f);
                } else {
                    medTitle.setAlpha(1.0f);
                    medDetails.setAlpha(1.0f);
                }

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    med.setChecked(isChecked);
                    if (isChecked) {
                        medTitle.setAlpha(0.5f);
                        medDetails.setAlpha(0.5f);
                    } else {
                        medTitle.setAlpha(1.0f);
                        medDetails.setAlpha(1.0f);
                    }
                });

                listLayout.addView(card);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMedicines();
    }
}