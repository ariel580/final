package lev.ariel.arielbookstore;

import static androidx.core.content.ContextCompat.startActivity;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Activity for displaying a single book's detailed information
public class BookPageActivity extends AppCompatActivity {

    // Declare Firestore reference and UI elements
    FirebaseFirestore db;
    TextView title, bookisbn, author, description, length, price;
    ImageView image;
    Button back, buy;
    ImageButton favorite;
    Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        // Get book ISBN passed from intent
        String isbn = getIntent().getStringExtra("book_isbn");
        // Get current user's ID from Firebase Authentication
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up toolbar with back navigation
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("");
        }

        // Set current context
        context = this;

        // Link UI elements to their views
        title = findViewById(R.id.textViewOneBookTitle);
        bookisbn = findViewById(R.id.textViewBookIsbn);
        favorite = findViewById(R.id.imageButtonFavorite);
        author = findViewById(R.id.textViewOneBookAuthor);
        description = findViewById(R.id.textViewOneBookDescription);
        length = findViewById(R.id.textViewOneBookLength);
        price = findViewById(R.id.textViewOneBookPrice);
        image = findViewById(R.id.imageViewOneBookImage);
        buy = findViewById(R.id.buttonBuy);
        back = findViewById(R.id.buttonBackToBookListPage);

        // Go back to book list on "Back" button click
        back.setOnClickListener(view -> {
            Intent intent = new Intent(BookPageActivity.this, BookListActivity.class);
            startActivity(intent);
        });

        // Proceed to purchase flow on "Buy" button click
        buy.setOnClickListener(view -> {
            Intent intent = new Intent(BookPageActivity.this, GoodbyeActivity.class);
            intent.putExtra("bookName", title.getText().toString());
            intent.putExtra("bookPrice", price.getText().toString());
            intent.putExtra("bookIsbn", isbn);
            startActivity(intent);
        });

        // Check if the book is in user's favorites and update icon
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference favRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(isbn);
        favRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                favorite.setImageResource(R.drawable.baseline_favorite_24);
            } else {
                favorite.setImageResource(R.drawable.baseline_favorite_border_24);
            }
        });

        // Toggle favorite status on click
        favorite.setOnClickListener(v -> {
            favRef.get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    favRef.delete();
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    favorite.setImageResource(R.drawable.baseline_favorite_border_24);
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("timestamp", FieldValue.serverTimestamp());
                    data.put(" bookIsbn", isbn);
                    favRef.set(data);
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    favorite.setImageResource(R.drawable.baseline_favorite_24);
                }
            });
        });

        // Get book data from Firestore by ISBN
        db = FirebaseFirestore.getInstance();
        CollectionReference isbnRef = db.collection("Book");

        isbnRef.whereEqualTo(" bookIsbn", isbn).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                // Extract data from Firestore document
                String bookAuthor = document.getString(" bookAuthor");
                String bookDescription = document.getString(" bookDescription");
                String bookIsbn = document.getString(" bookIsbn");
                int bookLength = document.getLong(" bookLength").intValue();
                String bookGenre = document.getString("bookGenre");
                String bookName = document.getString("bookName");
                int bookPrice = document.getLong("bookPrice").intValue();

                // Load and display image from Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReference().child("images/" + isbn + ".jpg");
                final long ONE_MEGABYTE = 2048 * 2048;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bitmap);
                });

                // Set text fields with book data
                title.setText(bookName);
                bookisbn.setText("the book isbn: " + bookIsbn);
                author.setText(bookGenre + ",written by: " + bookAuthor);
                description.setText("book description:" + bookDescription);
                length.setText("the book's length: " + bookLength);
                price.setText("the book's price: " + bookPrice + " ");
            }
        });
    }

    // Inflate toolbar menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buyBook) {
            Intent intent = new Intent(BookPageActivity.this, BookListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.sellBook) {
            Intent intent = new Intent(BookPageActivity.this, SellPageActivity.class);
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
            Intent intent = new Intent(BookPageActivity.this, BookListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
