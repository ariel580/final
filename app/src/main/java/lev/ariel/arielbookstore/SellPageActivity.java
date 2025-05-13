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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellPageActivity extends AppCompatActivity {

    // Declare input fields and buttons for the book form and actions
    EditText AuthorName, BookDsc, Isbn, Length, Genre, Image, Name, Price;
    Button btn, setImage;
    FirebaseFirestore db;
    Context context;

    // Declare variables for handling camera photo capture
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri photoUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_page);

        // Set up custom toolbar
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Set icon
            getSupportActionBar().setTitle(""); // Clear toolbar title
        }

        // Assign context
        context = this;

        // Bind views to variables
        AuthorName = findViewById(R.id.editTextbookauthor);
        BookDsc = findViewById(R.id.editTextTextBoookdescription);
        Isbn = findViewById(R.id.editTextbookisbn);
        Length = findViewById(R.id.editTextbooklength);
        Genre = findViewById(R.id.editTextbookgenre);
        Name = findViewById(R.id.editTextbookname);
        Price = findViewById(R.id.editTextbookprice);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Show instructional message to the user
        Toast.makeText(getBaseContext(),"please fill the blanc text box's with your books information",Toast.LENGTH_LONG).show();

        // Register launcher to take a picture and handle result
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean success) {
                        if (success) {
                            // Get Firebase Storage instance and build file path
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            String fileName = Isbn.getText().toString() + ".jpg";
                            StorageReference fileRef = storageRef.child("images/" + fileName);

                            // Upload image to Firebase Storage
                            fileRef.putFile(photoUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Upload success (optional: UI updates)
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Notify user of failure
                                            Toast.makeText(getBaseContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        // Handle submit button click: save book data to Firestore
        btn = findViewById(R.id.buttonback);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read form inputs
                String author = AuthorName.getText().toString();
                String description = BookDsc.getText().toString();
                String isbn = Isbn.getText().toString();
                int length = Integer.parseInt(Length.getText().toString());
                String genre = Genre.getText().toString();
                String name = Name.getText().toString();
                int price = Integer.parseInt(Price.getText().toString());
                String sellerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                // Create map with book data to store in Firestore
                Map<String, Object> bookData = new HashMap<>();
                bookData.put(" bookAuthor", author);
                bookData.put(" bookDescription", description);
                bookData.put(" bookIsbn", isbn);
                bookData.put(" bookLength", length);
                bookData.put("bookGenre", genre);
                bookData.put("bookName", name);
                bookData.put("bookPrice", price);
                bookData.put("timestamp", FieldValue.serverTimestamp());
                bookData.put("sellerEmail",sellerEmail);

                // Add document to "Book" collection
                db.collection("Book").add(bookData);

                // Notify user and return to previous screen
                Toast.makeText(SellPageActivity.this, "Your book is added", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SellPageActivity.this, BuyOrSellActivity.class);
                startActivity(intent);
            }
        });

        // Handle picture button click to launch camera
        setImage = findViewById(R.id.buttonSetPicture);
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    // Opens the device camera to capture an image
    public void openCamera() {
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
        photoUri = FileProvider.getUriForFile(this, "lev.ariel.arielbookstore.fileprovider", photoFile);
        cameraLauncher.launch(photoUri);
    }

    // Inflate the menu layout for toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle toolbar menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buyBook) {
            Intent intent = new Intent(SellPageActivity.this, BookListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.sellBook) {
            Toast.makeText(getBaseContext(),"your already in the selling page",Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(SellPageActivity.this, BuyOrSellActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}