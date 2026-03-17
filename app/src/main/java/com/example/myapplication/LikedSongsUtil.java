package com.example.myapplication;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LikedSongsUtil {

    private static final String FILENAME = "liked_songs";

    public static ArrayList<String> getLikedSongs(Context context) {

        String liked_songs = EncryptedFileUtil.readData(context, FILENAME);
        ArrayList<String> listOfLikedSongs = new ArrayList<String>();
        Scanner s = new Scanner(liked_songs);
        while (s.hasNextLine()) {
            listOfLikedSongs.add(s.nextLine());
        }
        s.close();
        return listOfLikedSongs;
    }

    private static void setLikedSongs(Context context, ArrayList<String> listOfLikedSongs) {
        String liked_songs = String.join("\n", listOfLikedSongs);
        EncryptedFileUtil.writeData(context, liked_songs, FILENAME);
    }

    public static void AddLikedSong(Context context, String likedSong) {
        ArrayList<String> listOfLikedSongs = getLikedSongs(context);
        listOfLikedSongs.add(likedSong);
        setLikedSongs(context, listOfLikedSongs);
    }

    public static void removeLikedSong(Context context, String likedSong) {
        ArrayList<String> listOfLikedSongs = getLikedSongs(context);
        listOfLikedSongs.remove(likedSong);
        setLikedSongs(context, listOfLikedSongs);
    }
}
