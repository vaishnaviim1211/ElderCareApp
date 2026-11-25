package com.vaishnavi.elderappointment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    EditText medName, medMg;
    CheckBox chkMorning, chkAfternoon, chkNight;
    RadioButton radioBefore, radioAfter;
    Button saveMedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        medName = findViewById(R.id.medName);
        medMg = findViewById(R.id.medMg);
        chkMorning = findViewById(R.id.chkMorning);
        chkAfternoon = findViewById(R.id.chkAfternoon);
        chkNight = findViewById(R.id.chkNight);
        radioBefore = findViewById(R.id.radioBefore);
        radioAfter = findViewById(R.id.radioAfter);
        saveMedBtn = findViewById(R.id.saveMedBtn);

        saveMedBtn.setOnClickListener(v -> {
            String name = medName.getText().toString().trim();
            String mg = medMg.getText().toString().trim();
            
            ArrayList<String> times = new ArrayList<>();
            if (chkMorning.isChecked()) times.add("Morning");
            if (chkAfternoon.isChecked()) times.add("Afternoon");
            if (chkNight.isChecked()) times.add("Night");

            if (name.isEmpty() || mg.isEmpty() || times.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            String timing = radioBefore.isChecked() ? "Before Meal" : "After Meal";
            String frequency = String.join(", ", times);
            
            String medDetails = frequency + " • " + timing;
            String medTitle = name + " (" + mg + "mg)";
            
            MedicineActivity.medicines.add(new MedicineModel(medTitle, medDetails));

            // Set Voice Alarms for Medicine
            if (chkMorning.isChecked()) setMedicineAlarm(8, 0, name);
            if (chkAfternoon.isChecked()) setMedicineAlarm(13, 0, name);
            if (chkNight.isChecked()) setMedicineAlarm(20, 0, name);

            Toast.makeText(this, "Medicine Added with Voice Alarms", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setMedicineAlarm(int hour, int minute, String medName) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", "Medicine Time");
        intent.putExtra("message", "Please take your " + medName);
        intent.putExtra("voiceMessage", "Medicine time. Please take your " + medName);

        int requestId = (medName + hour + minute).hashCode();
        PendingIntent pi = PendingIntent.getBroadcast(this, requestId, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }
}
