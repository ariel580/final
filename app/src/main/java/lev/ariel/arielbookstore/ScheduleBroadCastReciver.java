package lev.ariel.arielbookstore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import lev.ariel.arielbookstore.HomeActivity;
import lev.ariel.arielbookstore.R;

// BroadcastReceiver triggered by AlarmManager to check for new books added in the last 24 hours
public class ScheduleBroadCastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Calculate the timestamp for 24 hours ago (used to filter recent books)
        long now = System.currentTimeMillis();
        long yesterday = now - 24 * 60 * 60 * 1000;

        // Query Firestore for books added after 'yesterday'
        db.collection("Book")
                .whereGreaterThan("timestamp", new Timestamp(new Date(yesterday)))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Count how many new books were added
                    int newBooks = querySnapshot.size();

                    // If any new books were added, show a notification
                    if (newBooks > 0) {
                        showNotification(context, newBooks);
                    }
                });
    }

    // Create and show a notification with the number of new books
    private void showNotification(Context context, int count) {
        // Create an Intent to open HomeActivity when notification is clicked
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyChannel")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Notification icon
                .setContentTitle("Daily Book Digest") // Title of the notification
                .setContentText(count + " new books were added today!") // Notification message
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority to make it visible immediately
                .setContentIntent(pendingIntent) // What happens when user taps the notification
                .setAutoCancel(true); // Dismiss notification on click

        // Show the notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1234, builder.build());
    }
}
