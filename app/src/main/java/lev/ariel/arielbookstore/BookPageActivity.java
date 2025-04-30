package lev.ariel.arielbookstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Activity for displaying a single book's detailed information
public class BookPageActivity extends AppCompatActivity {

    FirebaseFirestore db; // Firestore database reference
    TextView title, bookisbn, author, description, length, price; // UI components for book data
    ImageView image; // ImageView for displaying book image
    Button back, buy; // Buttons for back and buy actions

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_page);

        // Get the ISBN passed from previous activity
        String isbn = getIntent().getStringExtra("book_isbn");

        // Connect UI components
        title = findViewById(R.id.textViewOneBookTitle);
        bookisbn = findViewById(R.id.textViewBookIsbn);
        author = findViewById(R.id.textViewOneBookAuthor);
        description = findViewById(R.id.textViewOneBookDescription);
        length = findViewById(R.id.textViewOneBookLength);
        price = findViewById(R.id.textViewOneBookPrice);
        image = findViewById(R.id.imageViewOneBookImage);
        buy = findViewById(R.id.buttonBuy);
        back = findViewById(R.id.buttonBackToBookListPage);

        // Handle "Back" button click to return to the book list
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookPageActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        // Handle "Buy" button click to go to the GoodbyeActivity with book info
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookPageActivity.this, GoodbyeActivity.class);
                intent.putExtra("bookName", title.getText().toString());
                intent.putExtra("bookPrice", price.getText().toString());
                intent.putExtra("bookIsbn", isbn);
                startActivity(intent);
            }
        });

        // Initialize Firestore database
        db = FirebaseFirestore.getInstance();
        CollectionReference isbnRef = db.collection("Book");

        // Query Firestore to find the book with the matching ISBN
        isbnRef.whereEqualTo(" bookIsbn", isbn).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    // Retrieve all book information from the document
                    String bookAuthor = document.getString(" bookAuthor");
                    String bookDescription = document.getString(" bookDescription");
                    String bookIsbn = document.getString(" bookIsbn");
                    int bookLength = document.getLong(" bookLength").intValue(); // might crash if null
                    String bookGenre = document.getString("bookGenre");
                    String bookName = document.getString("bookName");
                    int bookPrice = document.getLong("bookPrice").intValue(); // might crash if null

                    // Load book image from Firebase Storage
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageRef = storage.getReference().child("images/" + isbn + ".jpg");
                    final long ONE_MEGABYTE = 2048 * 2048;

                    // Download the image and set it in the ImageView
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.setImageBitmap(bitmap);
                    });

                    // Set text fields with the book's data
                    title.setText(bookName);
                    bookisbn.setText("the book isbn: " + bookIsbn);
                    author.setText(bookGenre + ",written by: " + bookAuthor);
                    description.setText("book description:" + bookDescription);
                    length.setText("the book's length: " + bookLength + "");
                    price.setText("the book's price: " + bookPrice + " ");
                }
            }
        });

    }
}
