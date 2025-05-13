package lev.ariel.arielbookstore;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

// This activity serves as a splash screen shown when the app is first launched
public class MainActivity extends AppCompatActivity {

    ImageView logoIv;   // Reference to the logo image view
    Context context;    // Context for the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout of the splash screen
        setContentView(R.layout.activity_main);
        context = this;

        // Connect the ImageView with the logo in the layout
        logoIv = findViewById(R.id.imageViewLogo);

        // Load the animation from resources and apply it to the logo
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.my_anim);
        animation.setDuration(3000); // Set animation duration to 3 seconds
        logoIv.setAnimation(animation); // Apply the animation to the logo

        // Play a sound effect using MediaPlayer
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.swoosh);
        mediaPlayer.start();

        // After a 5-second delay, move to the HomeActivity (main screen)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 3000); // Delay in milliseconds
    }
}
