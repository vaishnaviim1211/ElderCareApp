package com.vaishnavi.elderappointment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Locale;

public class HealthTrackerActivity extends AppCompatActivity implements SensorEventListener {

    public static int steps = 0;
    private int initialSteps = -1;
    public static int waterGlasses = 0;
    public static final int STEPS_GOAL = 5000;
    public static final int WATER_GOAL = 8;

    public static String bedtime = "10:00 PM";
    public static String wakeup = "06:00 AM";

    private TextView stepsText, waterText, bedtimeText, wakeupText;
    private ProgressBar stepsProgress, waterProgress;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private static final int ACTIVITY_RECOGNITION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tracker);

        stepsText = findViewById(R.id.stepsText);
        waterText = findViewById(R.id.waterText);
        bedtimeText = findViewById(R.id.bedtimeText);
        wakeupText = findViewById(R.id.wakeupText);
        stepsProgress = findViewById(R.id.stepsProgress);
        waterProgress = findViewById(R.id.waterProgress);

        Button addStepsBtn = findViewById(R.id.addStepsBtn);
        Button addWaterBtn = findViewById(R.id.addWaterBtn);
        Button editSleepBtn = findViewById(R.id.editSleepBtn);

        // Manual add steps for testing or if sensor not available
        addStepsBtn.setOnClickListener(v -> {
            steps += 500;
            updateUI();
        });

        addWaterBtn.setOnClickListener(v -> {
            if (waterGlasses < WATER_GOAL) {
                waterGlasses++;
                updateUI();
            } else {
                Toast.makeText(this, "Water Goal Reached!", Toast.LENGTH_SHORT).show();
            }
        });

        editSleepBtn.setOnClickListener(v -> showSleepEditDialog());

        // Set periodic Water Voice Reminders (Every 2 hours from 9 AM to 9 PM)
        scheduleWaterReminders();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_health);
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

        // Initialize Step Counter
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        checkActivityPermission();

        updateUI();
    }

    private void scheduleWaterReminders() {
        for (int hour = 9; hour <= 21; hour += 2) {
            setVoiceAlarm(hour, 0, "Hydration Break", "Time to drink a glass of water.", "Please drink water now", hour + 100);
        }
    }

    private void checkActivityPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_RECOGNITION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Step counting permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Automatic step counting won't work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialSteps == -1) {
                initialSteps = (int) event.values[0];
            }
            steps = (int) event.values[0] - initialSteps;
            updateUI();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void showSleepEditDialog() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            bedtime = formatTime(hourOfDay, minute);
            setVoiceAlarm(hourOfDay, minute, "Bedtime", "It's time to go to bed.", "It is bedtime, please get some rest", 1);
            
            new TimePickerDialog(this, (view1, hour2, minute1) -> {
                wakeup = formatTime(hour2, minute1);
                setVoiceAlarm(hour2, minute1, "Wake-up", "Good Morning! Time to wake up.", "Good morning, it is time to wake up", 2);
                updateUI();
                Toast.makeText(this, "Alarms set with Voice Reminders!", Toast.LENGTH_SHORT).show();
            }, 6, 0, false).show();
            
        }, 22, 0, false).show();
    }

    private void setVoiceAlarm(int hour, int minute, String title, String msg, String voice, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", msg);
        intent.putExtra("voiceMessage", voice);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private String formatTime(int hour, int minute) {
        String ampm = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, ampm);
    }

    private void updateUI() {
        stepsText.setText(steps + " / " + STEPS_GOAL);
        stepsProgress.setProgress(steps);

        waterText.setText(waterGlasses + " / " + WATER_GOAL + " Glasses");
        waterProgress.setProgress(waterGlasses);

        bedtimeText.setText(bedtime);
        wakeupText.setText(wakeup);
    }
}