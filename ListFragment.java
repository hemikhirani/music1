package com.example.flex_music.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flex_music.FavoritesActivity;
import com.example.flex_music.R;
import com.example.flex_music.RecentlyPlayedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private TextView textViewRecentlyPlayedCount;
    private TextView textViewRecentlyAddedCount;
    private TextView textViewFavoritesCount;
    private LinearLayout favoritesLayout, recentPlayLayout;

    private SharedPreferences prefs;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Initialize Gson
        gson = new Gson();

        // Null-safe context
        Context context = getContext();
        if (context == null) return view;

        // Bind views

        favoritesLayout = view.findViewById(R.id.favoritesLayout);
        recentPlayLayout = view.findViewById(R.id.recent_play);

        // Safe preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Update counts with try-catch
        try {
            updateCounts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Click listeners
        favoritesLayout.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
            }
        });

        recentPlayLayout.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), RecentlyPlayedActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void updateCounts() {
        int recentlyPlayed = getListSize("recently_played");
        int recentlyAdded = getListSize("recently_added");
        int favorites = getListSize("favorites");

        textViewRecentlyPlayedCount.setText(recentlyPlayed + " songs");
        textViewRecentlyAddedCount.setText(recentlyAdded + " songs");
        textViewFavoritesCount.setText(favorites + " songs");
    }

    private int getListSize(String key) {
        String json = prefs.getString(key, "");
        if (json == null || json.isEmpty()) return 0;

        try {
            ArrayList<SongsFragment.Song> list = gson.fromJson(json, new TypeToken<ArrayList<SongsFragment.Song>>() {}.getType());
            return list != null ? list.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
