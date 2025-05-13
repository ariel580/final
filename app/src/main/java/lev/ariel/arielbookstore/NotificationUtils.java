package lev.ariel.arielbookstore;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class NotificationUtils {

    // Schedules a daily notification at 20:00 (8 PM)
    public static void scheduleNotification(Context context) {
        // Create an intent to trigger the broadcast receiver
        Intent intent = new Intent(context, ScheduleBroadCastReciver.class);
        int requestCode = (int) System.currentTimeMillis(); // Unique request code

        // Create a PendingIntent to schedule with AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Get the system AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set the desired notification time (8 PM today or tomorrow)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // If 8 PM already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule the exact time notification
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Notify user that scheduling succeeded
        Toast.makeText(context, "Notification scheduled", Toast.LENGTH_SHORT).show();
    }

    // Creates a notification channel (required on Android 8.0+)
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the channel with ID, name, and importance level
            NotificationChannel channel = new NotificationChannel(
                    "notifyChannel",
                    "Scheduled Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for daily reminder");

            // Register the channel with the system
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // On Android 13+, request permission to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != context.getPackageManager().PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (android.app.Activity) context,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
        }
    }
}
