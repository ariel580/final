package lev.ariel.arielbookstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// This activity shows the list of books, allows searching, and plays music via a background service.
public class BookListActivity extends AppCompatActivity implements View.OnClickListener {

    // Music control buttons
    ImageButton playService, pauseService, stopService;

    // Search input fields
    EditText nameSearch, genreSearch;

    // App context
    Context context;

    // Search buttons
    Button ByName, ByGenre;

    // RecyclerView components
    RecyclerView bookListRv;
    BookAdapter adapter;
    List<Book> bookList;

    // Firebase Firestore database instance
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        context = this;
        db = FirebaseFirestore.getInstance();

        // Link buttons and views from layout
        playService = findViewById(R.id.playService);
        pauseService = findViewById(R.id.pauseService);
        stopService = findViewById(R.id.stopService);
        ByName = findViewById(R.id.buttonSearchByName);
        ByGenre = findViewById(R.id.buttonSearchByGenre);
        nameSearch = findViewById(R.id.editTextSearchByName);
        genreSearch = findViewById(R.id.editTextSearchByGenre);

        // Initialize RecyclerView and adapter
        bookListRv = findViewById(R.id.recyclerViewBookList);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList);
        bookListRv.setHasFixedSize(true);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);

        // Load all books from Firestore
        getAll();

        // Set search button actions
        ByName.setOnClickListener(view -> {
            String name = nameSearch.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(context, "Search can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                filterByName(name);
            }
        });

        ByGenre.setOnClickListener(view -> {
            String genre = genreSearch.getText().toString();
            if (genre.isEmpty()) {
                Toast.makeText(context, "Search can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                filterByGenre(genre);
            }
        });

        // Set music buttons
        playService.setOnClickListener(this);
        pauseService.setOnClickListener(this);
        stopService.setOnClickListener(this);
    }

    // Fetch all books from Firestore and display them in the RecyclerView
    public void getAll() {
        db.collection("Book").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Note: fix field names if necessary (no leading spaces)
                    String bookAuthor = document.getString(" bookAuthor");
                    String bookDescription = document.getString(" bookDescription");
                    String bookIsbn = document.getString(" bookIsbn");
                    int bookLength = document.getLong(" bookLength").intValue();
                    String bookGenre = document.getString("bookGenre");
                    String bookName = document.getString("bookName");
                    int bookPrice = document.getLong("bookPrice").intValue();

                    // Create a Book object and add it to the list
                    Book b = new Book(R.drawable.baseline_photo_24, bookIsbn, bookName, bookAuthor, bookDescription, bookGenre, bookLength, bookPrice);
                    bookList.add(b);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Handle music service button clicks (play / pause / stop)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(context, MyService.class);

        if (id == R.id.playService) {
            Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
            intent.setAction("play");
        } else if (id == R.id.pauseService) {
            intent.setAction("pause");
        } else {
            intent.setAction("stop");
        }

        startService(intent);
    }

    // Filter books by genre and update the list shown
    public void filterByGenre(String genre) {
        List<Book> res = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getBookGenre().equals(genre)) {
                res.add(book);
            }
        }

        // Show the filtered list
        adapter = new BookAdapter(res);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);
    }

    // Filter books by name and update the list shown
    public void filterByName(String name) {
        List<Book> res2 = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getBookName().equals(name)) {
                res2.add(book);
            }
        }

        // Show the filtered list
        adapter = new BookAdapter(res2);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);
    }
}
