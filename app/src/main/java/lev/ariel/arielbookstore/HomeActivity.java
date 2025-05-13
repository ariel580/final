package lev.ariel.arielbookstore;

import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginPageBtn, registerPageBtn; // Buttons for login and register
    ImageButton moreInfoIb; // Button to show information dialog
    private Calendar calendar; // For time-based functions like notifications
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toast.makeText(context,"to get inside the app you need to login or register",Toast.LENGTH_LONG).show();

        // Bind UI elements
        loginPageBtn = findViewById(R.id.buttonLoginPage);
        registerPageBtn = findViewById(R.id.buttonRegisterPage);
        moreInfoIb = findViewById(R.id.imageButtonMoreInfo);

        // Setup toolbar with back arrow and no title
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("");
        }

        // Initialize the calendar instance
        calendar = Calendar.getInstance();

        // Assign click listeners to buttons
        loginPageBtn.setOnClickListener(this);
        registerPageBtn.setOnClickListener(this);
        moreInfoIb.setOnClickListener(this);
    }

    // Load a fragment into the designated container
    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutsignin, fragment);
        ft.commit();
    }

    // Handle clicks on buttons
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonLoginPage) {
            loadFragment(new LoginFragment());
        } else if (id == R.id.buttonRegisterPage) {
            loadFragment(new RegisterFragment());
        } else {
            createDialog();
        }
    }

    // Show a dialog with more information and a close button
    private void createDialog() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.more_info);
        d.setCancelable(true);
        d.setTitle("more information");

        Button exitBtn = d.findViewById(R.id.buttonCloseDialog);
        exitBtn.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    // Inflate toolbar menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buyBook) {
            Toast.makeText(context,"this feature isnt available at this page",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sellBook) {
            Toast.makeText(context,"this feature isnt available at this page",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.favorite) {
            Toast.makeText(context,"this feature isnt available at this page",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.kill) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to close the app?")
                    .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                    .setNegativeButton("No", null)
                    .show();
            return true;
        } else if (id == R.id.notification) {
            NotificationUtils.createNotificationChannel(context);
            NotificationUtils.scheduleNotification(context);
            return true;
        } else if (id == android.R.id.home) {
            Toast.makeText(context,"you cant go back in this page",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
