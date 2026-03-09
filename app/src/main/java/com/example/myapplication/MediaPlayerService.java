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
import android.widget.ImageView;

import com.example.myapplication.data_classes.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class MediaPlayerService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static MediaPlayer mediaPlayer = null;
    private long current_song_index;

    private long song_time;

    enum State {
        NON_PLAYING,
        PLAYING,
        PAUSED,
        PREPARING
    }

    private static State state = State.NON_PLAYING;
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
        long song_index = intent.getLongExtra(MediaPlayerActivity.EXTRA_MESSAGE_SONG_INDEX, 0);
        song_time = intent.getLongExtra(MediaPlayerActivity.EXTRA_MESSAGE_SONG_TIME, 0);
        current_song_index = song_index;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
        }
        return binder;
    }


    private void setMediaPlayer() {
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
                Log.d("mediaPlayer", "song" + Long.toString(current_song_index) + " completed");

                state = State.NON_PLAYING;
                if (current_song_index < songs.size() - 1) {
                    current_song_index += 1;
                }
                else {
                    current_song_index = 0;
                }
                playSong();
            }
        });
        mediaPlayer.reset();
    }

    public void playSong() {
        if (state == State.PLAYING) {
            return;
        }
        else if (state == State.PAUSED) {
            state = State.PLAYING;
            mediaPlayer.start();
        }
        else
        {
            Uri contentUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs.get((int)current_song_index).getID());
            setMediaPlayer();
            try {
                mediaPlayer.setDataSource(getApplicationContext(), contentUri);
            } catch (IOException | IllegalStateException e) {
                Log.e("mediaPlayer exception setDataSource", e.toString());
                throw new RuntimeException(e);
            }
            state = State.PREPARING;
            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }

    }


    public Song getSong() {
        return songs.get((int)current_song_index);
    }

    public void flipLikeSong() {
        Song song = getSong();
        song.flipLike();
        songs.set((int)current_song_index, song);
    }



    public long getTimeInPlayingSong() {
        if (state == State.PLAYING) {
            return mediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public long getTimeInPausedOrPlayingSong() {
        if (mediaPlayer.getCurrentPosition() == 0 && song_time > 0) {
            return song_time;
        }
        song_time = 0;
        return mediaPlayer.getCurrentPosition();
    }
    public long getSongIndex() {
        if (state == State.PLAYING) {
            return current_song_index;
        }
        return -1;
    }
    public int pauseSong() {
        if (state == State.PLAYING) {
            int time = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            state = State.PAUSED;
            return time;
        }
        return 0;
    }

    public void nextSong() {
        if (state == State.PLAYING) {
            song_time = 0;
            mediaPlayer.pause();
        }
        state = State.NON_PLAYING;
        if (current_song_index < songs.size() - 1) {
            current_song_index += 1;
            Log.d("next_song", Long.toString(current_song_index));
        }
        else {
            current_song_index = 0;
        }
        playSong();
    }

    public void prevSong() {
        if (state == State.PLAYING) {
            song_time = 0;
            mediaPlayer.pause();
        }
        state = State.NON_PLAYING;
        if (current_song_index > 0) {
            current_song_index -= 1;
            Log.d("prev_song", Long.toString(current_song_index));

        }
        else {
            current_song_index = songs.size() - 1;
        }
        playSong();
    }


    public void onPrepared(MediaPlayer player) {
        if (song_time != 0) {
            mediaPlayer.seekTo((int)song_time);
            song_time = 0;
        }
        state = State.PLAYING;
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
        state = State.NON_PLAYING;
    }
}