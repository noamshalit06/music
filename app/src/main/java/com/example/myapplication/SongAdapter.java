package com.example.myapplication;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data_classes.Song;

import java.io.Serializable;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private List<Song> songs;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songName;

        private ImageView albumArt;

        private ImageView heart;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            songName = (TextView) view.findViewById(R.id.songName);
            albumArt = itemView.findViewById(R.id.albumArtMain);
            heart = itemView.findViewById(R.id.heart);
        }

        public ImageView getAlbumArt() {
            return albumArt;
        }

        public ImageView getHeart() {
            return heart;
        }

        public TextView getSongName() {
            return songName;
        }
    }

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.song_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getAlbumArt().setImageBitmap(songs.get(position).getAlbumPicture(viewHolder.itemView.getContext()));
        viewHolder.getSongName().setText(songs.get(position).getName());
        if (songs.get(position).IsLiked()) {
            viewHolder.getHeart().setImageResource(R.drawable.red_heart);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            Log.d("button click", Integer.toString(position));
            Intent intent = new Intent(viewHolder.itemView.getContext(), MediaPlayerActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE_SONGS_LIST, (Serializable) songs);
            intent.putExtra(MainActivity.EXTRA_MESSAGE_SONG_INDEX, (long)position);
            viewHolder.itemView.getContext().startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return songs.size();
    }
}
