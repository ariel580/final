package lev.ariel.arielbookstore;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    ImageView logoIv;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
        logoIv = findViewById(R.id.imageViewLogo);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.my_anim);
        animation.setDuration(5000);
        logoIv.setAnimation(animation);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.swoosh);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 5000);



    }
}