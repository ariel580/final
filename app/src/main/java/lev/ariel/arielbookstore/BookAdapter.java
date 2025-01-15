package lev.ariel.arielbookstore;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>
{
    private List<Book> booksList;

    public BookAdapter(List<Book> booksList) {
        this.booksList = booksList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.one_book,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Book book = this.booksList.get(position);
        holder.bookImage.setImageResource(book.getBookImage());
        holder.bookName.setText(book.getBookName());
        holder.bookAuthor.setText(book.getBookAuthor());
        holder.bookGenre.setText(book.getBookGenre().toString());
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return this.booksList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView bookImage;
        public TextView bookName;
        public TextView bookAuthor;
        public TextView bookGenre;
        public Button moreInfo;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            bookImage = itemView.findViewById(R.id.imageViewBookImage);
            bookName = itemView.findViewById(R.id.textViewBookName);
            bookAuthor = itemView.findViewById(R.id.textViewBookAuthor);
            bookGenre = itemView.findViewById(R.id.textViewBookGenre);
            moreInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}
