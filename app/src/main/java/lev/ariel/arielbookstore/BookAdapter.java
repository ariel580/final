package lev.ariel.arielbookstore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

// Adapter for displaying Book objects in a RecyclerView
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    // List of Book objects to display
    private List<Book> booksList;

    // Constructor: receives a list of books
    public BookAdapter(List<Book> booksList) {
        this.booksList = booksList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single book item
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.one_book, parent, false);
        // Return a new ViewHolder that holds this view
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get an instance of Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Get the current book object from the list
        Book book = this.booksList.get(position);

        // Get the ISBN of the book to use as image file name
        String bookIsbn = book.getBookIsbn();

        // Build a reference to the image in Firebase Storage
        StorageReference imageRef = storage.getReference().child("images/" + bookIsbn + ".jpg");

        // Define max size for image download (2MB)
        final long TWO_MEGABYTE = 2048 * 2048;

        // Download the image and set it into the ImageView
        imageRef.getBytes(TWO_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.bookImage.setImageBitmap(bitmap);
        });

        // Set the book name, author, and genre text
        holder.bookName.setText(book.getBookName());
        holder.bookAuthor.setText(book.getBookAuthor());
        holder.bookGenre.setText(book.getBookGenre());

        // Set listener for "More Info" button to open the book's detail page
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open OneBookPage activity with the ISBN
                Intent intent = new Intent("android.intent.action.ONEBOOKPAGE");
                intent.putExtra("book_isbn", bookIsbn);
                v.getContext().startActivity(intent);
            }
        });
    }

    // Return the number of books in the list
    @Override
    public int getItemCount() {
        return this.booksList.size();
    }

    // ViewHolder class holds all views for a single book item
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public TextView bookName;
        public TextView bookAuthor;
        public TextView bookGenre;
        public Button moreInfo;

        // Constructor: link each view in the layout to a variable
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.imageViewBookImage);  // Book cover image
            bookName = itemView.findViewById(R.id.textViewBookName);     // Book title
            bookAuthor = itemView.findViewById(R.id.textViewBookAuthor); // Author name
            bookGenre = itemView.findViewById(R.id.textViewBookGenre);   // Genre label
            moreInfo = itemView.findViewById(R.id.buttonInfo);           // "More Info" button
        }
    }
}
