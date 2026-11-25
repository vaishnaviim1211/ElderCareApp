package com.vaishnavi.elderappointment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    EditText ageInput, bloodInput, weightInput, heightInput, genderInput, historyInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ageInput = findViewById(R.id.editAge);
        bloodInput = findViewById(R.id.editBlood);
        weightInput = findViewById(R.id.editWeight);
        heightInput = findViewById(R.id.editHeight);
        genderInput = findViewById(R.id.editGender);
        historyInput = findViewById(R.id.editHistory);
        saveBtn = findViewById(R.id.saveProfileBtn);

        // Pre-fill only if they are not the initial dashes or default text
        if (!ProfileActivity.age.equals("--")) ageInput.setText(ProfileActivity.age);
        if (!ProfileActivity.blood.equals("--")) bloodInput.setText(ProfileActivity.blood);
        if (!ProfileActivity.weight.equals("--")) weightInput.setText(ProfileActivity.weight);
        if (!ProfileActivity.height.equals("--")) heightInput.setText(ProfileActivity.height);
        if (!ProfileActivity.gender.equals("--")) genderInput.setText(ProfileActivity.gender);
        if (!ProfileActivity.history.equals("No records added.")) historyInput.setText(ProfileActivity.history);

        saveBtn.setOnClickListener(v -> {
            String age = ageInput.getText().toString().trim();
            String blood = bloodInput.getText().toString().trim();
            String weight = weightInput.getText().toString().trim();
            String height = heightInput.getText().toString().trim();
            String gender = genderInput.getText().toString().trim();
            String history = historyInput.getText().toString().trim();

            ProfileActivity.age = age.isEmpty() ? "--" : age;
            ProfileActivity.blood = blood.isEmpty() ? "--" : blood;
            ProfileActivity.weight = weight.isEmpty() ? "--" : weight;
            ProfileActivity.height = height.isEmpty() ? "--" : height;
            ProfileActivity.gender = gender.isEmpty() ? "--" : gender;
            ProfileActivity.history = history.isEmpty() ? "No records added." : history;

            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}