package com.example.myapplication;

public class Song {
    private long id;
    private String name;

    public Song(long new_id, String new_name) {
        id = new_id;
        name = new_name;
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
