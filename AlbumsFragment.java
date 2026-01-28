package com.example.flex_music.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.flex_music.R;
import com.example.flex_music.Adapter.AlbumAdapter;
import com.example.flex_music.music_player;
import java.util.ArrayList;

public class AlbumsFragment extends Fragment {

    private String artistName; // Store selected artist
    private ListView albumListView;
    private ArrayList<String> albumList;
    private AlbumAdapter albumAdapter;

    public static AlbumsFragment newInstance(String artist) {
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putString("artist_name", artist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistName = getArguments().getString("artist_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumListView = view.findViewById(R.id.albumListView);
        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(requireContext(), albumList);
        albumListView.setAdapter(albumAdapter);

        if (artistName != null) {
            loadAlbums();
        }

        // 🎵 Click on an album to play music
        albumListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedAlbum = albumList.get(position);
            ArrayList<String> songPaths = getSongsFromAlbum(selectedAlbum);

            if (!songPaths.isEmpty()) {
                Intent intent = new Intent(requireContext(), music_player.class);
                intent.putStringArrayListExtra("song_list", songPaths);
                intent.putExtra("current_song_index", 0);
                startActivity(intent);
            }
        });

        return view;
    }

    // ✅ Method to update artist and refresh album list
    public void updateArtist(String artist) {
        this.artistName = artist;
        albumList.clear();
        loadAlbums();
    }

    private void loadAlbums() {
        albumList.clear();
        if (artistName == null) return; // Prevent crashes

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.ALBUM};
        String selection = MediaStore.Audio.Media.ARTIST + "=?";
        String[] selectionArgs = {artistName};

        Cursor cursor = requireContext().getContentResolver().query(uri, projection, selection, selectionArgs, MediaStore.Audio.Media.ALBUM + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                if (album != null && !albumList.contains(album)) {
                    albumList.add(album);
                }
            }
            cursor.close();
        }
        albumAdapter.notifyDataSetChanged();
    }

    // 🎵 Fetch songs from the selected album
    private ArrayList<String> getSongsFromAlbum(String albumName) {
        ArrayList<String> songList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.ALBUM + "=?";
        String[] selectionArgs = {albumName};

        Cursor cursor = requireContext().getContentResolver().query(uri, projection, selection, selectionArgs, MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String songPath = cursor.getString(0);
                songList.add(songPath);
            }
            cursor.close();
        }

        return songList;
    }
}
