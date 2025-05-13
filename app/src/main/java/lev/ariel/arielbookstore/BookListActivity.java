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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This activity shows the list of books, allows searching, and plays music via a background service.
public class BookListActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare UI elements and service-related buttons
    ImageButton playService, pauseService, stopService;
    EditText nameSearch;
    Context context;
    Button ByName, ByGenre;
    RecyclerView bookListRv;
    BookAdapter adapter;
    List<Book> bookList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            getSupportActionBar().setTitle("");
        }

        // Set context and Firestore instance
        context = this;
        db = FirebaseFirestore.getInstance();

        // Bind all UI components
        playService = findViewById(R.id.playService);
        pauseService = findViewById(R.id.pauseService);
        stopService = findViewById(R.id.stopService);
        ByName = findViewById(R.id.buttonSearchByName);
        ByGenre = findViewById(R.id.buttonSearchByGenre);
        nameSearch = findViewById(R.id.editTextSearchByName);

        // Set up RecyclerView
        bookListRv = findViewById(R.id.recyclerViewBookList);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList);
        bookListRv.setHasFixedSize(true);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);

        // Load all books initially
        getAll();

        // Handle search by name
        ByName.setOnClickListener(view -> {
            String name = nameSearch.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(context, "Search can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                filterByName(name);
            }
        });

        // Handle search by genre with genre selection dialog
        ByGenre.setOnClickListener(v -> {
            db.collection("Book")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Set<String> genreSet = new HashSet<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String genre = doc.getString("bookGenre");
                            if (genre != null) genreSet.add(genre);
                        }

                        List<String> genreList = new ArrayList<>(genreSet);
                        Collections.sort(genreList);
                        genreList.add(0, "All Genres");

                        String[] genres = genreList.toArray(new String[0]);

                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Choose a Genre")
                                .setItems(genres, (d, which) -> {
                                    String selectedGenre = genres[which];
                                    if (selectedGenre.equals("All Genres")) {
                                        getAll();
                                    } else {
                                        loadBooksByGenre(selectedGenre);
                                    }
                                })
                                .create();

                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
                        dialog.show();
                    });
        });

        // Bind service controls to click listener
        playService.setOnClickListener(this);
        pauseService.setOnClickListener(this);
        stopService.setOnClickListener(this);
    }

    // Load all books from Firestore and update RecyclerView
    public void getAll() {
        db.collection("Book").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String bookAuthor = document.getString(" bookAuthor");
                    String bookDescription = document.getString(" bookDescription");
                    String bookIsbn = document.getString(" bookIsbn");
                    int bookLength = document.getLong(" bookLength").intValue();
                    String bookGenre = document.getString("bookGenre");
                    String bookName = document.getString("bookName");
                    int bookPrice = document.getLong("bookPrice").intValue();

                    Book b = new Book(R.drawable.baseline_photo_24, bookIsbn, bookName, bookAuthor, bookDescription, bookGenre, bookLength, bookPrice);
                    bookList.add(b);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Handle music button clicks
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(context, MyService.class);

        if (id == R.id.playService) {
            Toast.makeText(context, "playing", Toast.LENGTH_SHORT).show();
            intent.setAction("play");
        } else if (id == R.id.pauseService) {
            intent.setAction("pause");
            Toast.makeText(context, "music paused", Toast.LENGTH_SHORT).show();
        } else {
            intent.setAction("stop");
            Toast.makeText(context, "music stopped", Toast.LENGTH_SHORT).show();
        }

        startService(intent);
    }

    // Load and display books from Firestore filtered by genre
    private void loadBooksByGenre(String genre) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Book")
                .whereEqualTo("bookGenre", genre)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> filteredBooks = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);
                        filteredBooks.add(book);
                    }

                    if (filteredBooks.isEmpty()) {
                        Toast.makeText(this, "No books found for genre: " + genre, Toast.LENGTH_SHORT).show();
                    } else {
                        BookAdapter adapter = new BookAdapter(filteredBooks);
                        bookListRv.setLayoutManager(new LinearLayoutManager(this));
                        bookListRv.setAdapter(adapter);
                    }
                    adapter = new BookAdapter(filteredBooks);
                    bookListRv.setLayoutManager(new LinearLayoutManager(this));
                    bookListRv.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching books: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Filter books by name using user input
    public void filterByName(String name) {
        List<Book> res2 = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getBookName().toLowerCase().contains(name.toLowerCase())) {
                res2.add(book);
            }
        }

        adapter = new BookAdapter(res2);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);
    }

    // Inflate toolbar menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle toolbar menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buyBook) {
            Toast.makeText(context,"your already in the buying page",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sellBook) {
            Intent intent = new Intent(BookListActivity.this, SellPageActivity.class);
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
            Intent intent = new Intent(BookListActivity.this, BuyOrSellActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

