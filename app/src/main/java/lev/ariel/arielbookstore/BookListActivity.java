package lev.ariel.arielbookstore;

import android.os.Bundle;
import android.widget.Adapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    RecyclerView bookListRv;
    BookAdapter adapter;

    List<Book> bookList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_list);

        bookListRv = findViewById(R.id.recyclerViewBookList);
        bookList = new ArrayList<>();
        bookList.add(new Book(R.drawable.the_hunger_game,"123-456","Book 1","Author 1","book description",Genre.comedy, 453,123.99f));
        bookList.add(new Book(R.drawable.the_hunger_game,"666-333","Book 2","Author 2","book description",Genre.comedy, 147,87));
        bookList.add(new Book(R.drawable.the_hunger_game,"786-676","Book 3","Author 3","book description",Genre.science_fiction, 141,450));
        bookList.add(new Book(R.drawable.the_hunger_game,"268-453","Book 4","Author 4","book description",Genre.comedy, 87,453.99f));
        bookList.add(new Book(R.drawable.the_hunger_game,"346-237","Book 5","Author 5","book description",Genre.romance, 900,97.99f));
        bookList.add(new Book(R.drawable.the_hunger_game,"456-906","Book 6","Author 6","book description",Genre.horror, 10,154.99f));

        adapter = new BookAdapter(bookList);
        bookListRv.setHasFixedSize(true);
        bookListRv.setLayoutManager(new LinearLayoutManager(this));
        bookListRv.setAdapter(adapter);


    }
}