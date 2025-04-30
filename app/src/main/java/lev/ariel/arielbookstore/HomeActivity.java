package lev.ariel.arielbookstore;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;

import java.util.Calendar;

// This is the main "home screen" activity that lets users log in, register, or schedule a notification.
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginPageBtn, registerPageBtn, notificationBtn; // Buttons for navigation and scheduling notification
    ImageButton moreInfoIb; // Info button to show a dialog
    private Calendar calendar; // Used for time-based scheduling
    int requestCode = 123; // Used to uniquely identify the PendingIntent
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createNotificationChannel(); // Create notification channel on startup (Android 8+)

        // Connect buttons and image button from layout
        loginPageBtn = findViewById(R.id.buttonLoginPage);
        registerPageBtn = findViewById(R.id.buttonRegisterPage);
        notificationBtn = findViewById(R.id.buttonNotification);
        moreInfoIb = findViewById(R.id.imageButtonMoreInfo);

        // Initialize calendar object to use for scheduling notifications
        calendar = Calendar.getInstance();

        // Set click listeners for all buttons
        loginPageBtn.setOnClickListener(this);
        registerPageBtn.setOnClickListener(this);
        moreInfoIb.setOnClickListener(this);
        notificationBtn.setOnClickListener(this);
    }

    // Helper method to replace the current fragment in the layout
    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager(); // Get fragment manager
        FragmentTransaction ft = fm.beginTransaction(); // Start fragment transaction
        ft.replace(R.id.frameLayoutsignin, fragment); // Replace the placeholder with the new fragment
        ft.commit(); // Commit the transaction
    }

    // Handle button clicks
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonLoginPage) {
            // Load login screen
            loadFragment(new LoginFragment());
        } else if (id == R.id.buttonRegisterPage) {
            // Load register screen
            loadFragment(new RegisterFragment());
        } else if (id == R.id.buttonNotification) {
            // Schedule notification
            scheduleNotification();
        } else {
            // Show dialog with more information
            createDialog();
        }
    }

    // Display a dialog window with extra information
    private void createDialog() {
        Dialog d = new Dialog(this); // Create a dialog
        d.setContentView(R.layout.more_info); // Use a layout file for the dialog
        d.setCancelable(true); // Allow user to cancel it
        d.setTitle("more information"); // Set dialog title

        // Close button inside dialog
        Button exitBtn = d.findViewById(R.id.buttonCloseDialog);
        exitBtn.setOnClickListener(v -> d.dismiss()); // Close the dialog when button is clicked
        d.show(); // Show the dialog
    }

    // Schedule a notification using AlarmManager
    private void scheduleNotification() {
        Intent intent = new Intent(this, ScheduleBroadCastReciver.class); // Intent for BroadcastReceiver

        int requestCode = (int) System.currentTimeMillis(); // Use current time to ensure uniqueness

        // Create a PendingIntent to trigger the BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // Get AlarmManager

        // Set the notification time to 20:00:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // If the time has already passed for today, schedule it for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Notification scheduled", Toast.LENGTH_SHORT).show();
    }

    // Create a notification channel (required for Android 8+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the channel properties
            NotificationChannel channel = new NotificationChannel(
                    "notifyChannel", // Channel ID
                    "Scheduled Notifications", // Channel name
                    NotificationManager.IMPORTANCE_HIGH // Importance level
            );
            channel.setDescription("Channel for daily reminder");

            // Create the channel
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
        }
    }
}
