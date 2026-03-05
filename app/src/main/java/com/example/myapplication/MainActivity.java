package com.example.myapplication;
import com.example.myapplication.data_classes.Song;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private static ArrayList<Song> songs = new ArrayList<Song>();
    public static String EXTRA_MESSAGE_SONGS_LIST = "Songs";

    public static String EXTRA_MESSAGE_SONG_INDEX = "song_index";


    public Void readPermissionsGranted() {
        songs = makeListsOfSongs();
        createSongsButtons(songs);
        return null;
    }


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
        PermissionsUtil.RequestReadingAudioPermissions(this, this::readPermissionsGranted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionsUtil.RequestReadingAudioPermissions(this, this::readPermissionsGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtil.handlePermissionsResult(this, requestCode, permissions, grantResults, this::readPermissionsGranted);
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
            Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
            intent.putExtra(EXTRA_MESSAGE_SONGS_LIST, (Serializable) songs);
            intent.putExtra(EXTRA_MESSAGE_SONG_INDEX, v.getId());

            startActivity(intent);
        }
    }
}