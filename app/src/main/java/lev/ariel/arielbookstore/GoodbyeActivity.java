package lev.ariel.arielbookstore;


import android.Manifest;
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


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import okhttp3.*;


import org.json.JSONObject;


import java.io.IOException;


// This activity handles the final step after a purchase: deleting the book and sending a confirmation email
public class GoodbyeActivity extends AppCompatActivity {


    // Declare buttons for navigation
    Button back, close;
    // Store the current context
    Context context;
    // API key and sender email for sending email via SendGrid
    private static final String SENDERID_API_KEY = "because of git hub i cant put the real key here";
    private static final String FROM_EMAIL = "ariels.bookstore@mail.com";


    // Firebase Authentication instance
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodbye);


        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.myToolbar);
        context = this;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("");
        }


        // Get current user's email
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        // Retrieve book price and name from intent extras
        String price = getIntent().getStringExtra("bookPrice");
        String bookName = getIntent().getStringExtra("bookName");


        // Extract the name from the email before the @ character
        int etPos = userEmail.indexOf('@');
        String name = userEmail.substring(0, etPos);


        // Generate a random number for receipt ID
        int n = (int)(Math.random() * 100000) + 1;


        // Compose the confirmation email
        String emailSubject = "receipt #" + n + " for " + name;
        String emailMassage = "Thank you for buying " + bookName + ". \n";
        emailMassage += "your total price is " + price + " nis. \n";
        emailMassage += "good day, Ariel :)";


        // Send confirmation email to buyer
        sendEmail(userEmail,emailSubject,emailMassage);


        // Get Firestore instance and book ISBN
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String targetIsbn = getIntent().getStringExtra("bookIsbn");


        // Find book in Firestore and notify seller by email
        db.collection("Book")
                .whereEqualTo(" bookIsbn", targetIsbn)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot bookDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String sellerEmail = bookDoc.getString("sellerEmail");


                        if (sellerEmail != null) {
                            Log.d("SellerEmail", "Email: " + sellerEmail);
                            String newemailSubject = "your book has been sold";
                            String newemailMassage = "the book: " + bookName + "has been sold";
                            newemailMassage += "congradulations";
                            newemailMassage += "good day, Ariel :)";
                            sendEmail(sellerEmail,newemailSubject,newemailMassage);
                        } else {
                            Log.w("SellerEmail", "Seller email not found in document");
                        }
                    } else {
                        Log.w("BookQuery", "No book found with ISBN: " + targetIsbn);
                    }
                })
                .addOnFailureListener(e -> Log.e("BookQuery", "Error querying book", e));


        // Initialize Firebase Authentication and UI buttons
        auth = FirebaseAuth.getInstance();
        back = findViewById(R.id.buttonBackHome);
        close = findViewById(R.id.buttonClose);


        // Handle "Back to Home" button click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodbyeActivity.this, BuyOrSellActivity.class);
                startActivity(intent);
            }
        });


        // Handle "Close App" button click
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });


        // Delete the purchased book from Firestore
        String isbnToDelete = getIntent().getStringExtra("bookIsbn");
        db.collection("Book")
                .whereEqualTo(" bookIsbn", isbnToDelete.trim())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Log.d("DELETE_DEBUG", "Found document to delete: " + documentSnapshot.getId());
                            documentSnapshot.getReference().delete();
                        }
                    }
                });
    }


    // Sends email using SendGrid API
    private void sendEmail(String to, String subject, String message) {
        OkHttpClient client = new OkHttpClient();


        try {
            JSONObject emailData = new JSONObject();
            emailData.put("from", new JSONObject().put("email", FROM_EMAIL));
            emailData.put("subject", subject);


            JSONObject toEmail = new JSONObject().put("email", to);
            JSONObject personalization = new JSONObject()
                    .put("to", new org.json.JSONArray().put(toEmail));


            emailData.put("personalizations", new org.json.JSONArray().put(personalization));
            emailData.put("content", new org.json.JSONArray()
                    .put(new JSONObject()
                            .put("type", "text/plain")
                            .put("value", message)));


            RequestBody body = RequestBody.create(
                    emailData.toString(),
                    MediaType.parse("application/json")
            );


            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .addHeader("Authorization", "Bearer " + SENDERID_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }


                @Override
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(getBaseContext(),"send failed:" + response.code() + " - " + response.message(),Toast.LENGTH_SHORT ).show());
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(getBaseContext(),"Email sent to " + to,Toast.LENGTH_SHORT ).show());
                    }
                    response.close();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Inflate menu options into the toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    // Handle menu option selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buyBook) {
            Intent intent = new Intent(GoodbyeActivity.this, BookListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.sellBook) {
            Intent intent = new Intent(GoodbyeActivity.this, SellPageActivity.class);
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
            Intent intent = new Intent(GoodbyeActivity.this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


