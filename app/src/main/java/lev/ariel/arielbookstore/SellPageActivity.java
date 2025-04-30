package lev.ariel.arielbookstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

// This activity allows the user to sell a book by entering details and uploading a photo
public class SellPageActivity extends AppCompatActivity {

    // UI components for entering book details
    EditText AuthorName, BookDsc, Isbn, Length, Genre, Image, Name, Price;
    Button btn, setImage;
    FirebaseFirestore db;

    // For handling photo capture and upload
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri photoUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_page);

        // Connect EditText fields with the layout
        AuthorName = findViewById(R.id.editTextbookauthor);
        BookDsc = findViewById(R.id.editTextTextBoookdescription);
        Isbn = findViewById(R.id.editTextbookisbn);
        Length = findViewById(R.id.editTextbooklength);
        Genre = findViewById(R.id.editTextbookgenre);
        Name = findViewById(R.id.editTextbookname);
        Price = findViewById(R.id.editTextbookprice);

        // Initialize Firestore database
        db = FirebaseFirestore.getInstance();

        // Set up camera launcher to take a picture and upload it
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean success) {
                        if (success) {
                            // Upload the captured photo to Firebase Storage
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            String fileName = Isbn.getText().toString() + ".jpg"; // use ISBN as image name
                            StorageReference fileRef = storageRef.child("images/" + fileName);

                            // Upload file
                            fileRef.putFile(photoUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getBaseContext(), "Photo uploaded successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getBaseContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        // Handle "Submit" button click to save book data in Firestore
        btn = findViewById(R.id.buttonback);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input from user
                String author = AuthorName.getText().toString();
                String description = BookDsc.getText().toString();
                String isbn = Isbn.getText().toString();
                int length = Integer.parseInt(Length.getText().toString());
                String genre = Genre.getText().toString();
                String name = Name.getText().toString();
                int price = Integer.parseInt(Price.getText().toString());

                // Save the book data as a Firestore document
                Map<String, Object> bookData = new HashMap<>();
                bookData.put(" bookAuthor", author);
                bookData.put(" bookDescription", description);
                bookData.put(" bookIsbn", isbn);
                bookData.put(" bookLength", length);
                bookData.put("bookGenre", genre);
                bookData.put("bookName", name);
                bookData.put("bookPrice", price);
                bookData.put("timestamp", FieldValue.serverTimestamp());

                db.collection("Book").add(bookData);

                Toast.makeText(SellPageActivity.this, "Your book is added", Toast.LENGTH_SHORT).show();

                // Go back to the book list screen
                Intent intent = new Intent(SellPageActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        // Handle "Set Picture" button click to open the camera
        setImage = findViewById(R.id.buttonSetPicture);
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(); // Launch camera to take picture
            }
        });
    }

    // This method opens the camera to take a picture and save it as a temporary file
    public void openCamera() {
        // Create a file in the app's private storage to store the photo
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");

        // Use FileProvider to safely share the file with the camera app
        photoUri = FileProvider.getUriForFile(this, "lev.ariel.arielbookstore.fileprovider", photoFile);

        // Launch the camera app with the URI to save the photo
        cameraLauncher.launch(photoUri);
    }
}
