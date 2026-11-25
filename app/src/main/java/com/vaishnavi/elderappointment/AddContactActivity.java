package com.vaishnavi.elderappointment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    EditText nameInput, phoneInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameInput = findViewById(R.id.contactNameInput);
        phoneInput = findViewById(R.id.contactPhoneInput);
        saveBtn = findViewById(R.id.saveContactBtn);

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            EmergencyActivity.emergencyContacts.add(new ContactModel(name, phone));
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}