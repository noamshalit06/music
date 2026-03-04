package com.example.myapplication;
import com.example.myapplication.data_classes.Song;
import com.example.myapplication.MyMediaPlayer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private static ArrayList<Song> songs = new ArrayList<Song>();
    public static String EXTRA_MESSAGE = "Songs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PermissionsUtil.RequestReadingAudioPermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionsUtil.RequestReadingAudioPermissions(this);
    }

    public void readPermissionsGranted() {
        songs = makeListsOfSongs();
        createSongsButtons(songs);
    }

    private ArrayList<Song> makeListsOfSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();

        Cursor audioCursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ? ",
                new String[]{"%Music%"},
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC");


        if (audioCursor != null && audioCursor.moveToFirst()) {
            int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int displayNameColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int albumIdColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                long id = audioCursor.getLong(idColumn);
                String displayName = audioCursor.getString(displayNameColumn);
                long albumId = audioCursor.getLong(albumIdColumn);
                Log.d(displayName, Long.toString(id));

                Cursor albumCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID+ "=" + albumId,
                        null,
                        null);
                if (albumCursor.moveToFirst()) {
                    int albumArtID = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                    String albumArtPath = albumCursor.getString(albumArtID);
                    if (albumArtPath == null) {
                        songs.add(new Song(id, displayName));

                    }
                    else {
                        songs.add(new Song(id, displayName, albumArtPath));
                        Log.d("albumPath", albumArtPath);
                    }
                }
                else
                {
                    songs.add(new Song(id, displayName));
//                    Log.d("albumPath", "error");
                }

            } while (audioCursor.moveToNext());
        }
        return songs;
    }

    private void createSongsButtons(ArrayList<Song> songs)
    {
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
        for (int i = 0; i < songs.size(); i++) {
            Song currentSong = songs.get(i);
            Button b = new Button(this);
            b.setText(currentSong.getName());
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            b.setId(i);
            b.setOnClickListener(new onClickListener());
            ll.addView(b);
        }
    }


    private class onClickListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("button click", Long.toString(v.getId()));
            Intent intent = new Intent(MainActivity.this, MyMediaPlayer.class);
            ArrayList<Song> passed_songs = new ArrayList<Song>();
            for (int i = v.getId(); i < songs.size(); i++) {
                passed_songs.add(songs.get(i));
            }
            intent.putExtra(EXTRA_MESSAGE, (Serializable) passed_songs);
            startActivity(intent);
        }
    }
}