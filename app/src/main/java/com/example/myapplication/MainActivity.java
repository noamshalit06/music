package com.example.myapplication;
import com.example.myapplication.data_classes.Song;


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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{

    private static final int READ_MEDIA_AUDIO_PERMISSION = 1;
    private static ArrayList<Song> songs = new ArrayList<Song>();

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
        RequestReadingAudioPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestReadingAudioPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_MEDIA_AUDIO_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissions", "Got READ_MEDIA_AUDIO permission");
                    songs = makeListsOfSongs();
                    createSongsButtons(songs);
                }
        }
    }
    private void RequestReadingAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "has READ_MEDIA_AUDIO permission");
            songs = makeListsOfSongs();
            createSongsButtons(songs);
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_MEDIA_AUDIO)) {
            showPermissionExplanationDialog();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_MEDIA_AUDIO },
                    READ_MEDIA_AUDIO_PERMISSION);
            Log.d("permissions", "request READ_MEDIA_AUDIO permission");
        }
    }

    private void showPermissionExplanationDialog() {
        Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs audio access")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                                READ_MEDIA_AUDIO_PERMISSION);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
            Intent intent = new Intent(this, MediaPlayer.class);
            intent.putExtra("songs", songs);
            startActivity(intent);
        }
    }
}