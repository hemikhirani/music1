package com.example.flex_music.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flex_music.R;
import com.example.flex_music.fragments.SongsFragment;
import com.example.flex_music.music_player;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private ArrayList<SongsFragment.Song> songList;
    private Context context;

    public SongAdapter(ArrayList<SongsFragment.Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongsFragment.Song song = songList.get(position);
        holder.textTitle.setText(song.getTitle());

        holder.itemView.setOnClickListener(v -> {
            // Play song using intent
            Intent intent = new Intent(context, music_player.class);
            intent.putExtra("position", position);
            intent.putExtra("songs", songList); // Make sure SongsFragment.Song implements Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textSongTitle);
        }
    }
}
