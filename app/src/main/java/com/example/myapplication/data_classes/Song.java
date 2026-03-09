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
    private boolean isLiked;

    public Song(long new_id, String new_name, long duration, long albumId, boolean isLiked) {
        this.id = new_id;
        this.name = new_name;
        this.duration = duration;
        this.albumArt = albumId;
        this.isLiked = isLiked;
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

    public boolean IsLiked() {
        return isLiked;
    }

    public void flipLike() {
        this.isLiked = !this.isLiked;
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
            else {
                Log.e("GetAlbumArtError", "album art doesn't exist");
            }
        } catch (Exception e) {
            Log.e("GetAlbumArtError", e.toString());
        }
        return bm;
    }
    public Bitmap getAlbumPicture(Context context) {
        return getAlbumArt(this.albumArt, context);
    }

}
