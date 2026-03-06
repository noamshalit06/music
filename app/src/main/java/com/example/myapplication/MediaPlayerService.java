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
import java.util.ArrayList;
import java.util.Objects;


public class MediaPlayerService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static MediaPlayer mediaPlayer = null;
    private long current_song_index;

    private static String state = "Non-playing";
    ArrayList<Song> songs = new ArrayList<Song>();

    // Binder given to clients.
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        songs = (ArrayList<Song>) intent.getSerializableExtra(MediaPlayerActivity.EXTRA_MESSAGE_SONGS_LIST);
        int song_index = intent.getIntExtra(MediaPlayerActivity.EXTRA_MESSAGE_SONG_INDEX, 0);
        current_song_index = song_index;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
        }
        return binder;
    }


    public void playSong() {
        if (Objects.equals(state, "playing")) {
            return;
        }
        else if (Objects.equals(state, "paused")) {
            state = "playing";
            mediaPlayer.start();
        }
        else
        {
            Uri contentUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs.get((int)current_song_index).getID());
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
                    state = "Non-playing";
                    if (current_song_index < songs.size() - 1) {
                        current_song_index += 1;
                    }
                    else {
                        current_song_index = 0;
                    }
                    playSong();
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
            state = "preparing";
            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }

    }


    public Song getSong() {
        return songs.get((int)current_song_index);
    }
    public int pauseSong() {
        if (state.equals("playing")) {
            int time = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            state = "paused";
            return time;
        }
        return 0;
    }

    public void nextSong() {
        if (state.equals("playing")) {
            mediaPlayer.pause();
        }
        state = "Non-playing";
        if (current_song_index < songs.size() - 1) {
            current_song_index += 1;
        }
        else {
            current_song_index = 0;
        }
        playSong();
    }

    public void prevSong() {
        if (state.equals("playing")) {
            mediaPlayer.pause();
        }
        state = "Non-playing";
        if (current_song_index > 0) {
            current_song_index -= 1;
        }
        else {
            current_song_index = songs.size() - 1;
        }
        playSong();
    }


    public void onPrepared(MediaPlayer player) {
        state = "playing";
        mediaPlayer.start();
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
        state = "Non-playing";
    }
}