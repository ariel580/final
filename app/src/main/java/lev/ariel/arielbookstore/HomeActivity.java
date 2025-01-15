package lev.ariel.arielbookstore;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginPageBtn, registerPageBtn,notificationBtn;
    ImageButton moreInfoIb;

    Context context;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        context = this;

        loginPageBtn = findViewById(R.id.buttonLoginPage);
        registerPageBtn = findViewById(R.id.buttonRegisterPage);
        notificationBtn = findViewById(R.id.buttonNotification);
        moreInfoIb = findViewById(R.id.imageButtonMoreInfo);
        calendar = Calendar.getInstance();
        loginPageBtn.setOnClickListener(this);
        registerPageBtn.setOnClickListener(this);
        moreInfoIb.setOnClickListener(this);
        createNotificationChannel();

    }
    public void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutsignin,fragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.buttonLoginPage)
        {
            loadFragment(new LoginFragment());
        }
        else if(id == R.id.buttonRegisterPage)
        {
            loadFragment(new RegisterFragment());
        } else if (id == R.id.buttonNotification) {
            calendar.set(Calendar.HOUR_OF_DAY,5);
            calendar.set(Calendar.MINUTE,0);
            scheduleNotification();
        }
        else
        {
            createDialog();
        }
    }

    private void createDialog()
    {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.more_info);
        d.setCancelable(true);
        d.setTitle("more information");
        Button exitBtn = d.findViewById(R.id.buttonCloseDialog);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
    private void scheduleNotification() {
        String message = "Did you know its healthy to read before you sleep?";
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(2025,1,15,14,28,0);

        Intent notificationIntent = new Intent(this, ScheduleBroadCastReceiver.class);
        notificationIntent.putExtra("message", message);

        // Generate unique request code using current timestamp
        int requestCode = (int) System.currentTimeMillis();

        // Add unique extra to Intent to differentiate PendingIntents
        notificationIntent.setAction("lev.ariel.arielbookstore.ACTION" + requestCode);

        // Create a unique PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode, // Use unique request code
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long triggerTime = scheduledTime.getTimeInMillis();
        Log.d("NotificationApp", "Scheduled notification at: " + triggerTime);

        // Schedule the notification
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Toast.makeText(this, "Notification scheduled", Toast.LENGTH_SHORT).show();
        }

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for scheduled notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}