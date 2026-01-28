package com.example.flex_music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.example.flex_music.R;
import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<String> {

    public AlbumAdapter(Context context, ArrayList<String> albums) {
        super(context, 0, albums);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_album, parent, false);
        }

        String album = getItem(position);
        TextView albumNameTextView = convertView.findViewById(R.id.albumName);
        albumNameTextView.setText(album);

        return convertView;
    }
}
