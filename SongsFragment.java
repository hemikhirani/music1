package com.example.flex_music.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flex_music.R;
import com.example.flex_music.music_player;
import com.example.flex_music.utils.Searchable;

import java.io.Serializable;
import java.util.ArrayList;

public class SongsFragment extends Fragment implements Searchable {

    private static final int REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private ArrayList<Song> allSongs = new ArrayList<>();
    private SongsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongsAdapter(allSongs);
        recyclerView.setAdapter(adapter);

        checkPermission();
        return view;
    }

    private void checkPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_AUDIO :
                Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, REQUEST_CODE);
        } else {
            loadSongs();
        }
    }

    private void loadSongs() {
        allSongs.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        try (Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(0);
                    String path = cursor.getString(1);
                    allSongs.add(new Song(title, path));
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading songs", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        adapter.updateList(new ArrayList<>(allSongs));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSearchQuery(String query) {
        ArrayList<Song> filteredList = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(song);
            }
        }
        adapter.updateList(filteredList);
    }

    // ✅ Song data model
    public static class Song implements Serializable {
        private String title;
        private String path;
        private long lastPlayedTime;

        public Song(String title, String path) {
            this.title = title;
            this.path = path;
            this.lastPlayedTime = 0;
        }

        public String getTitle() {
            return title;
        }

        public String getPath() {
            return path;
        }

        public long getLastPlayedTime() {
            return lastPlayedTime;
        }

        public void setLastPlayedTime(long lastPlayedTime) {
            this.lastPlayedTime = lastPlayedTime;
        }

        public String getFileName() {
            if (path != null) {
                return path.substring(path.lastIndexOf("/") + 1);
            }
            return title;
        }
    }

    // ✅ Adapter for RecyclerView
    private class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
        private ArrayList<Song> songs;

        SongsAdapter(ArrayList<Song> songs) {
            this.songs = songs;
        }

        void updateList(ArrayList<Song> newSongs) {
            this.songs = newSongs;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(songs.get(position));
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            android.widget.TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
                itemView.setOnClickListener(this);
            }

            void bind(Song song) {
                textView.setText(song.getTitle());
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(requireContext(), music_player.class);
                    intent.putExtra("songList", songs);
                    intent.putExtra("songIndex", pos);
                    startActivity(intent);
                }
            }
        }
    }
}
