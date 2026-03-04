package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
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

    private static final int READ_MEDIA_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "onCreate");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        askForPermissions();
        ArrayList<Song> songs = makeListsOfSongs();
        createSongsButtons(songs);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case READ_MEDIA_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }  else {
                    // shut down app
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            /// performAction(...);
            Log.d("permission", "has");

        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_MEDIA_AUDIO)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.

            ///showInContextUI(...)
            Log.d("permission", "show");
        }
        else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_MEDIA_AUDIO },
                    READ_MEDIA_AUDIO);
            Log.d("permission", "request");

        }
    }


    private ArrayList<Song> makeListsOfSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();

        Cursor audioCursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ? ",
                new String[]{"%Music%"},
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC");

        if (audioCursor != null && audioCursor.moveToFirst()) {
            Log.d("first", "success");
            int idColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int displayNameColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);

            do {
                long id = audioCursor.getLong(idColumn);
                String displayName = audioCursor.getString(displayNameColumn);

                songs.add(new Song(id, displayName));

                Log.d("my first file", "my first file");
                Log.d(displayName, Long.toString(id));
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
            b.setId((int)(currentSong.getID()));
            b.setOnClickListener(new onClickListener());
            ll.addView(b);
        }
    }


    private static class onClickListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("button click", Long.toString(v.getId()));
        }
    }
}