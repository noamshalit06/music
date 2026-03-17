package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("media_player_prefs", MODE_PRIVATE);
    }

    public static long getSongIndexTimeStamp(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        long song_index_insert_timestamp_default_value = context.getResources().getInteger(R.integer.song_index_insert_timestamp_default_value);
        return sharedPref.getLong(context.getString(R.string.song_index_insert_timestamp), song_index_insert_timestamp_default_value);
    }

    public static long getSongIndexNumber(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        long song_index_number_default_value = context.getResources().getInteger(R.integer.song_index_number_default_value);
        return sharedPref.getLong(context.getString(R.string.song_index_number), song_index_number_default_value);
    }

    public static long getTimeInSong(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        long time_in_song_default_value = context.getResources().getInteger(R.integer.time_in_song_default_value);
        return sharedPref.getLong(context.getString(R.string.time_in_song), time_in_song_default_value);
    }

    public static void editSharedPreferences(Context context, long song_index_insert_timestamp,
                                             long song_index_number, long time_in_song) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.song_index_insert_timestamp),song_index_insert_timestamp);
        editor.putLong(context.getString(R.string.song_index_number), song_index_number);
        editor.putLong(context.getString(R.string.time_in_song), time_in_song);
        editor.apply();
    }






}
