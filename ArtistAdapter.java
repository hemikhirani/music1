package com.example.flex_music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.example.flex_music.R;
import java.util.ArrayList;

public class ArtistAdapter extends ArrayAdapter<String> {

    public ArtistAdapter(Context context, ArrayList<String> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        String artist = getItem(position);
        TextView artistNameTextView = convertView.findViewById(R.id.artistName);

        if (artist != null) {
            artistNameTextView.setText(artist);
        }

        return convertView;
    }
}
