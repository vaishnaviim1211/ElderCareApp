package com.vaishnavi.elderappointment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText doctor, dateInput, timeInput;
    private Button saveBtn;
    private TextView title;
    private int editIndex = -1;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        doctor = findViewById(R.id.doctor);
        dateInput = findViewById(R.id.date);
        timeInput = findViewById(R.id.time);
        saveBtn = findViewById(R.id.saveBtn);
        title = findViewById(R.id.title);

        dateInput.setFocusable(false);
        dateInput.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateInput.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        timeInput.setFocusable(false);
        timeInput.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                String ampm = (hourOfDay >= 12) ? "PM" : "AM";
                int displayHour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, ampm));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });

        if (getIntent().hasExtra("editIndex")) {
            editIndex = getIntent().getIntExtra("editIndex", -1);
            String data = getIntent().getStringExtra("data");
            if (data != null) {
                String[] parts = data.split("\\|");
                if (parts.length >= 3) {
                    doctor.setText(parts[0]);
                    dateInput.setText(parts[1]);
                    timeInput.setText(parts[2]);
                    saveBtn.setText("Update Appointment");
                    if (title != null) title.setText("Edit Appointment");
                }
            }
        }

        saveBtn.setOnClickListener(v -> {
            String d = doctor.getText().toString().trim();
            String dt = dateInput.getText().toString().trim();
            String t = timeInput.getText().toString().trim();

            if (d.isEmpty() || dt.isEmpty() || t.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String userKey = "appts_" + MainActivity.loggedInUserEmail;
                SharedPreferences prefs = getSharedPreferences("ElderCareData", MODE_PRIVATE);
                String savedData = prefs.getString(userKey, "");
                ArrayList<String> list = new ArrayList<>();
                if (!savedData.isEmpty()) {
                    list.addAll(Arrays.asList(savedData.split("###")));
                }

                String newData = d + "|" + dt + "|" + t;

                if (editIndex != -1 && editIndex < list.size()) {
                    list.set(editIndex, newData);
                } else {
                    list.add(newData);
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i));
                    if (i < list.size() - 1) sb.append("###");
                }
                prefs.edit().putString(userKey, sb.toString()).apply();

                // Schedule Notification 1 hour before
                scheduleNotification(d, calendar.getTimeInMillis());

                Toast.makeText(this, editIndex != -1 ? "Updated Successfully!" : "Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void scheduleNotification(String doctorName, long apptTimeInMillis) {
        long reminderTime = apptTimeInMillis - (60 * 60 * 1000); // 1 hour before

        if (reminderTime < System.currentTimeMillis()) return;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow exact alarms for reminders", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", "Appointment Reminder");
        intent.putExtra("message", "Upcoming appointment with " + doctorName + " in 1 hour!");
        intent.putExtra("voiceMessage", "Your appointment is scheduled with " + doctorName + " in 1 hour");

        int notificationId = (int) (reminderTime / 1000);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }
}
