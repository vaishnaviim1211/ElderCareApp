package com.vaishnavi.elderappointment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class EmergencyActivity extends AppCompatActivity {

    public static ArrayList<ContactModel> emergencyContacts = new ArrayList<>();
    LinearLayout contactsContainer;
    Button addContactBtn;
    CardView ambulanceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        contactsContainer = findViewById(R.id.contactsContainer);
        addContactBtn = findViewById(R.id.addContactBtn);
        ambulanceCard = findViewById(R.id.ambulanceCard);

        ambulanceCard.setOnClickListener(v -> makeCall("102"));

        addContactBtn.setOnClickListener(v -> {
            // Simplified for now, just adding a dummy or opening a dialog
            // You can replace this with a proper activity or dialog
            showAddContactDialog();
        });

        displayContacts();
    }

    private void showAddContactDialog() {
        // For simplicity, let's just add one to show it works, 
        // or you can create AddContactActivity
        startActivity(new Intent(this, AddContactActivity.class));
    }

    private void displayContacts() {
        contactsContainer.removeAllViews();
        for (ContactModel contact : emergencyContacts) {
            View card = getLayoutInflater().inflate(R.layout.item_contact, contactsContainer, false);
            TextView name = card.findViewById(R.id.contactName);
            TextView phone = card.findViewById(R.id.contactPhone);
            
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            
            card.setOnClickListener(v -> makeCall(contact.getPhone()));
            
            contactsContainer.addView(card);
        }
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayContacts();
    }
}