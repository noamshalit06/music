package com.example.myapplication;

import static java.lang.Thread.sleep;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data_classes.Song;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class MediaPlayerActivity extends AppCompatActivity {

    static final String  EXTRA_MESSAGE_SONGS_LIST = "media_player_songs_list";

    static final String  EXTRA_MESSAGE_SONG_INDEX = "media_player_songs_index";

    static final String  EXTRA_MESSAGE_SONG_TIME = "media_player_song_time";

    MediaPlayerService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_media_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        }


    private void showArt() {
        ImageView myImageView = (ImageView)findViewById(R.id.albumArt);
        Bitmap bm = mService.getSong().getAlbumPicture(this);
        if (bm != null) {
            myImageView.setImageBitmap(bm);
        }
    }


    private class UpdateThread implements Runnable {

        private Context context;
        public UpdateThread(Context context) {
            this.context = context;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    SharedPreferencesUtil.editSharedPreferences(context, System.currentTimeMillis(),
                            mService.getSongIndex(), mService.getTimeInPlayingSong());
                    updateProgressBar(false);


                    ImageView myImageView = (ImageView) findViewById(R.id.albumArt);
                    Bitmap bm = mService.getSong().getAlbumPicture(context);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bm != null) {
                                myImageView.setImageBitmap(bm);
                            }
                        }
                    });
                } catch (NullPointerException | IllegalStateException e) {
                    break;
                }
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        ArrayList<Song> songs = (ArrayList<Song>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE_SONGS_LIST);
        long song_index = intent.getLongExtra(MainActivity.EXTRA_MESSAGE_SONG_INDEX, 0);
        long song_time = intent.getLongExtra(MainActivity.EXTRA_MESSAGE_SONG_TIME, 0);

        Intent send_intent = new Intent(this, MediaPlayerService.class);
        send_intent.putExtra(EXTRA_MESSAGE_SONGS_LIST, (Serializable) songs);
        send_intent.putExtra(EXTRA_MESSAGE_SONG_INDEX, song_index);
        send_intent.putExtra(EXTRA_MESSAGE_SONG_TIME, song_time);

        bindService(send_intent, connection, Context.BIND_AUTO_CREATE);

        Context activity = this;
        Thread thread = new Thread(new UpdateThread(activity));
        thread.start();
    }


    public void onButtonPlayClick(View v) {
        if (mBound) {
            VibrationUtil vibrationUtil = new VibrationUtil(this);
            PermissionsUtil.RequestVibratePermissions(this, vibrationUtil::vibrate);
            mService.playSong();
        }
    }

    public void onButtonPauseClick(View v) {
        if (mBound) {
            updateProgressBar(true);
        }
    }

    public void updateProgressBar(boolean stop) {
        long time = 0;
        if (stop) {
            time = mService.pauseSong();
        }
        else {
            time = mService.getTimeInPausedOrPlayingSong();
        }
        long songDuration = mService.getSong().getDuration();
        ProgressBar songProgressBar = (ProgressBar) findViewById(R.id.songProgressBar);
        songProgressBar.setProgress((int)((time * 1000) / songDuration));
    }

    public void onButtonNextClick(View v) {
        if (mBound) {
            mService.nextSong();
            ProgressBar songProgressBar = (ProgressBar) findViewById(R.id.songProgressBar);
            songProgressBar.setProgress(0);
        }
    }

    public void onButtonPrevClick(View v) {
        if (mBound) {
            ProgressBar songProgressBar = (ProgressBar) findViewById(R.id.songProgressBar);
            songProgressBar.setProgress(0);
            mService.prevSong();
        }
    }


    private final ServiceConnection  connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        mBound = false;
    }
}


