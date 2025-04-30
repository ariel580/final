package lev.ariel.arielbookstore;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

// This class defines a Service that plays background music
public class MyService extends Service {

    private MediaPlayer mediaPlayer; // MediaPlayer object to control audio
    Context context; // Context reference

    // Empty constructor
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // Create and prepare the media player with a background audio resource
        mediaPlayer = MediaPlayer.create(context, R.raw.back);
        // Set the music to loop indefinitely
        mediaPlayer.setLooping(true);
    }

    // This method handles commands sent to the service (play, pause, stop)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the action sent with the Intent (play / pause / stop)
        String action = intent.getAction();

        if ("play".equals(action)) {
            // If the action is "play" and the music isn't already playing, start it
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } else if ("pause".equals(action)) {
            // If the action is "pause" and the music is playing, pause it
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else if ("stop".equals(action)) {
            // If the action is "stop", stop the media player
            mediaPlayer.stop();
        }

        // If the service is killed by the system, it will be restarted with the last intent
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Called when the service is destroyed
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This service is not designed for binding, so return null
        return null;
    }
}
