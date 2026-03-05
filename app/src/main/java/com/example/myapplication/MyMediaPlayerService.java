package com.example.myapplication;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication.data_classes.Song;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


public class MyMediaPlayerService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    static MediaPlayer mediaPlayer = null;
    private long current_song_id;

    ArrayList<Song> songs = new ArrayList<Song>();

    // Binder given to clients.
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyMediaPlayerService getService() {
            return MyMediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        songs = (ArrayList<Song>) intent.getSerializableExtra(MyMediaPlayer.EXTRA_MESSAGE_SONGS_LIST);
        int song_index = intent.getIntExtra(MyMediaPlayer.EXTRA_MESSAGE_SONG_INDEX, 0);
        current_song_id = song_index;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
        }
        return binder;
    }


    public void playSong() {
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs.get((int)current_song_id).getID());
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
                if (current_song_id < songs.size() - 1) {
                    current_song_id += 1;
                    playSong();
                }
                else {
                    current_song_id = 0;
                }
                Log.d("mediaPlayer", "completed");
            }
        });
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), contentUri);
        } catch (IOException | IllegalStateException e) {
            Log.e("mediaPlayer exception setDataSource", e.toString());
            throw new RuntimeException(e);
        }
        mediaPlayer.prepareAsync(); // prepare async to not block main thread
    }

    public void onPrepared(MediaPlayer player) {
        mediaPlayer.start();
    }
    public void initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("error", Integer.toString(what) + Integer.toString(extra));
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}