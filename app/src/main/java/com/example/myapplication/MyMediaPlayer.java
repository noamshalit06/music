package com.example.myapplication;

import static android.content.Intent.getIntent;

import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data_classes.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MyMediaPlayer extends AppCompatActivity {

    private long current_song_id;

    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ArrayList<Song> songs = (ArrayList<Song>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE_SONGS_LIST);
        int song_index = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_SONG_INDEX, 0);
        current_song_id = songs.get(song_index).getID();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_media_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PermissionsUtil.RequestReadingAudioPermissions(this, this::playSong);
        }

    public Void playSong() {
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("mediaPlayer", "completed");
            }
        });
        try {
            mediaPlayer.setDataSource(getApplicationContext(), contentUri);
        } catch (IOException | IllegalStateException e) {
            Log.e("mediaPlayer exception setDataSource", e.toString());
            throw new RuntimeException(e);
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException | IllegalStateException e) {
            Log.e("mediaPlayer exception prepare", e.toString());
            throw new RuntimeException(e);
        }
        mediaPlayer.start();
        return null;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        PermissionsUtil.RequestReadingAudioPermissions(this, this::playSong);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
//        PermissionsUtil.RequestReadingAudioPermissions(this, this::playSong);
    }
}

