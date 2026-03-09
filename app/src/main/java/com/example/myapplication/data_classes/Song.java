package com.example.myapplication.data_classes;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.Serializable;

public class Song implements Serializable {

    private final static String ALBUM_ART_URI = "content://media/external/audio/albumart";
    private final long id;
    private final String name;

    private final long duration;
    private final long albumArt;


    public Song(long new_id, String new_name, long duration, long albumId) {
        this.id = new_id;
        this.name = new_name;
        this.duration = duration;
        this.albumArt = albumId;
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


    private Bitmap getAlbumArt(Long album_id, Context context)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse(ALBUM_ART_URI);

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }
    public Bitmap getAlbumPicture(Context context) {
        return getAlbumArt(this.albumArt, context);
    }

}
