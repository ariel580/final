package lev.ariel.arielbookstore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ScheduleBroadCastReceiver extends BroadcastReceiver {
    String message=" ";

    public void onReceive(Context context, Intent intent) {
        //get the message from the intent
        message = intent.getStringExtra("message");
        Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Use a unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Scheduled Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}

