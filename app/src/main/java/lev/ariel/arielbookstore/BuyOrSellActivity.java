package lev.ariel.arielbookstore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// This activity allows the user to choose between buying and selling books
public class BuyOrSellActivity extends AppCompatActivity {

    // Store activity context for reuse in handlers
    Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the activity
        super.onCreate(savedInstanceState);

        // Set the layout defined in XML for this screen
        setContentView(R.layout.activity_buy_or_sell);

        // Assign the context to a variable for later use
        context = this;

        // Setup toolbar with custom settings
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Set icon
            getSupportActionBar().setTitle(""); // No title text
        }

        // Get references to UI card elements for actions
        CardView buyCard = findViewById(R.id.buyCard);
        CardView sellCard = findViewById(R.id.sellCard);

        // Define behavior when "Buy" card is tapped
        buyCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            startActivity(intent);
        });

        // Define behavior when "Sell" card is tapped
        sellCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellPageActivity.class);
            startActivity(intent);
        });
    }

    // Inflate the toolbar menu items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Define what happens when toolbar menu items are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.buyBook) {
            Intent intent = new Intent(BuyOrSellActivity.this, BookListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.sellBook) {
            Intent intent = new Intent(BuyOrSellActivity.this, SellPageActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.favorite) {
            FavoritesUtils.showFavoritesDialog(context);
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
            Intent intent = new Intent(BuyOrSellActivity.this, HomeActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
