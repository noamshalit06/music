package com.example.myapplication.data_classes;

import java.io.Serializable;

public class Song implements Serializable {
    private final long id;
    private final String name;

    private final String albumArtPath;


    public Song(long new_id, String new_name, String newAlbumArtPath) {
        this.id = new_id;
        this.name = new_name;
        this.albumArtPath = newAlbumArtPath;
    }

    public Song(long new_id, String new_name) {
        id = new_id;
        name = new_name;
        albumArtPath = "";
    }
    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbumPicture() {
        return albumArtPath;
    }

}
