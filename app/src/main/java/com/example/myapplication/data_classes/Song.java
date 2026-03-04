package com.example.myapplication.data_classes;

public class Song {
    private long id;
    private String name;

    private String albumArtPath;


    public Song(long new_id, String new_name, String newAlbumArtPath) {
        id = new_id;
        name = new_name;
        albumArtPath = newAlbumArtPath;
    }
    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbumPicture() {
        return name;
    }
}
