package lev.ariel.arielbookstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// This activity allows the user to choose between buying and selling books
public class BuyOrSellActivity extends AppCompatActivity {

    // Declare buttons for buying and selling
    Button buybtn, sellbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.activity_buy_or_sell);

        // Connect the buttons from the layout
        buybtn = findViewById(R.id.buttonbuy);
        sellbtn = findViewById(R.id.buttonsell);

        // When the user clicks the "Buy" button → go to BookListActivity
        buybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyOrSellActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        // When the user clicks the "Sell" button → go to SellPageActivity
        sellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyOrSellActivity.this, SellPageActivity.class);
                startActivity(intent);
            }
        });
    }
}
