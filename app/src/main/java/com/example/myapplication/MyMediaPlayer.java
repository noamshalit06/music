package com.example.myapplication;

import static android.content.Intent.getIntent;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data_classes.Song;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MyMediaPlayer extends AppCompatActivity {

    static final String  EXTRA_MESSAGE_SONGS_LIST = "media_player_songs_list";

    static final String  EXTRA_MESSAGE_SONG_INDEX = "media_player_songs_index";

    MyMediaPlayerService mService;
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



    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        ArrayList<Song> songs = (ArrayList<Song>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE_SONGS_LIST);
        int song_index = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_SONG_INDEX, 0);

        Intent send_intent = new Intent(this, MyMediaPlayerService.class);
        send_intent.putExtra(EXTRA_MESSAGE_SONGS_LIST, (Serializable) songs);
        send_intent.putExtra(EXTRA_MESSAGE_SONG_INDEX, song_index);
        bindService(send_intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void onButtonPlayClick(View v) {
        if (mBound) {
            mService.playSong();
        }
    }

//    public void onButtonPauseClick(View v) {
//        if (mBound) {
//            int num = mService.pauseSong();
//        }
//    }
//
//    public void onButtonNextClick(View v) {
//        if (mBound) {
//            int num = mService.nextSong();
//        }
//    }
//
//    public void onButtonPrevClick(View v) {
//        if (mBound) {
//            int num = mService.prevSong();
//        }
//    }


    private final ServiceConnection  connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            MyMediaPlayerService.LocalBinder binder = (MyMediaPlayerService.LocalBinder) service;
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


