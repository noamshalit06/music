package com.example.myapplication.data_classes;

import java.io.Serializable;

public class Song implements Serializable {
    private final long id;
    private final String name;

    private final long duration;
    private final String albumArtPath;


    public Song(long new_id, String new_name, long duration, String newAlbumArtPath) {
        this.id = new_id;
        this.name = new_name;
        this.duration = duration;
        this.albumArtPath = newAlbumArtPath;
    }

    public Song(long newId, String newName, long newDuration) {
        id = newId;
        name = newName;
        duration = newDuration;
        albumArtPath = "";
    }
    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }


    public String getAlbumPicture() {
        return albumArtPath;
    }

}
