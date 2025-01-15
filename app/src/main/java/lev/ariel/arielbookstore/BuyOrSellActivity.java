package lev.ariel.arielbookstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BuyOrSellActivity extends AppCompatActivity {
    Button buybtn,sellbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_or_sell);

        buybtn = findViewById(R.id.buttonbuy);
        sellbtn = findViewById(R.id.buttonsell);
        buybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyOrSellActivity.this,BookListActivity.class);
                startActivity(intent);
            }
        });

        };
    }
