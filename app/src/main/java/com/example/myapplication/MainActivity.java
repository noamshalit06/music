package com.example.myapplication;
import com.example.myapplication.data_classes.Song;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
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

import java.io.FileDescriptor;
import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private static ArrayList<Song> songs = new ArrayList<Song>();
    public static String EXTRA_MESSAGE_SONGS_LIST = "Songs";

    public static String EXTRA_MESSAGE_SONG_INDEX = "song_index";

    public static String EXTRA_MESSAGE_SONG_TIME = "song_time";

    public static String state;


    public Void readPermissionsGrantedOnCreate() {
        songs = makeListsOfSongs();
        createSongsButtons(songs);

        SharedPreferences sharedPref = this.getSharedPreferences("media_player_prefs", MODE_PRIVATE);

        long song_index_insert_timestamp_default_value = getResources().getInteger(R.integer.song_index_insert_timestamp_default_value);
        long song_index_insert_timestamp = sharedPref.getLong(getString(R.string.song_index_insert_timestamp), song_index_insert_timestamp_default_value);


        long song_index_number_default_value = getResources().getInteger(R.integer.song_index_number_default_value);
        long song_index_number = sharedPref.getLong(getString(R.string.song_index_number), song_index_number_default_value);

        long time_in_song_default_value = getResources().getInteger(R.integer.time_in_song_default_value);
        long time_in_song = sharedPref.getLong(getString(R.string.time_in_song), time_in_song_default_value);

        if (song_index_insert_timestamp != 0 && time_in_song > 0 && song_index_number >= 0 && song_index_insert_timestamp + 500 < System.currentTimeMillis() ) {
            Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
            intent.putExtra(EXTRA_MESSAGE_SONGS_LIST, (Serializable) songs);
            intent.putExtra(EXTRA_MESSAGE_SONG_INDEX, song_index_number);
            intent.putExtra(EXTRA_MESSAGE_SONG_TIME, time_in_song);

            startActivity(intent);
        }
        return null;
    }

    public Void readPermissionsGranted() {
        songs = makeListsOfSongs();
        createSongsButtons(songs);
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        state = "create";
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PermissionsUtil.RequestReadingAudioPermissions(this, this::readPermissionsGrantedOnCreate);
    }

    @Override
    protected void onResume() {
        state = "resume";
        super.onResume();
        PermissionsUtil.RequestReadingAudioPermissions(this, this::readPermissionsGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (state.equals("create")) {
            PermissionsUtil.handlePermissionsResult(this, requestCode, permissions, grantResults, this::readPermissionsGrantedOnCreate);
        }
        else if (state.equals("resume")) {
            PermissionsUtil.handlePermissionsResult(this, requestCode, permissions, grantResults, this::readPermissionsGranted);
        }
    }


    private Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = this.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        Log.d("bm", bm.toString());
        return bm;
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
            int durationColumn = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);


            do {
                long id = audioCursor.getLong(idColumn);
                String displayName = audioCursor.getString(displayNameColumn);
                long albumId = audioCursor.getLong(albumIdColumn);
                long duration = audioCursor.getLong(durationColumn);
                
                songs.add(new Song(id, displayName, duration, albumId));


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
            intent.putExtra(EXTRA_MESSAGE_SONG_INDEX, (long)v.getId());

            startActivity(intent);
        }
    }
}