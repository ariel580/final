package lev.ariel.arielbookstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritesUtils {

    // Loads and displays the user's favorite books in a dialog
    public static void showFavoritesDialog(Context context) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(favSnapshots -> {
                    List<String> favoriteIds = new ArrayList<>();
                    for (DocumentSnapshot doc : favSnapshots) {
                        String isbn = doc.getString(" bookIsbn");
                        favoriteIds.add(isbn);
                    }

                    if (favoriteIds.isEmpty()) {
                        Toast.makeText(context, "No favorites yet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Split into chunks of 10 to comply with Firestore 'whereIn' limits
                    List<List<String>> chunks = chunkList(favoriteIds, 10);
                    List<DocumentSnapshot> favoriteBooks = new ArrayList<>();
                    int[] chunksLoaded = {0};

                    for (List<String> chunk : chunks) {
                        db.collection("Book")
                                .whereIn(" bookIsbn", chunk)
                                .get()
                                .addOnSuccessListener(bookSnapshots -> {
                                    for (DocumentSnapshot bookDoc : bookSnapshots) {
                                        favoriteBooks.add(bookDoc);
                                    }
                                    chunksLoaded[0]++;
                                    if (chunksLoaded[0] == chunks.size()) {
                                        // Show the dialog once all chunks are loaded
                                        showFavoritesDialogWithBooks(context, favoriteBooks);
                                    }
                                });
                    }
                });
    }

    // Displays an alert dialog listing favorite book titles
    private static void showFavoritesDialogWithBooks(Context context, List<DocumentSnapshot> docs) {
        if (docs.isEmpty()) {
            Toast.makeText(context, "No matching books found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract book titles for display
        List<String> bookTitles = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            String title = doc.getString("bookName");
            bookTitles.add(title != null ? title : "Untitled Book");
        }

        // Show book titles in a selection dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Your Favorite Books")
                .setItems(bookTitles.toArray(new String[0]), (dialogInterface, which) -> {
                    DocumentSnapshot selectedDoc = docs.get(which);
                    String isbn = selectedDoc.getString(" bookIsbn");

                    if (isbn != null) {
                        // Launch BookPageActivity with the selected book's ISBN
                        Intent intent = new Intent(context, BookPageActivity.class);
                        intent.putExtra("book_isbn", isbn);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "This book has no ISBN.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }

    // Splits a list into smaller chunks of specified size
    private static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }
}
