package com.example.naveen.music.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.naveen.music.R;
import com.example.naveen.music.interfaces.Callback;
import com.example.naveen.music.models.Song;

import java.util.ArrayList;

public class AlbumViewAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private final LayoutInflater songInf;
    private final ArrayList<Song> songs;
    private Callback com;

    public AlbumViewAdapter(Activity a, ArrayList<Song> songArrayList) {
        songs = songArrayList;
        Context context = a.getBaseContext();
        songInf = LayoutInflater.from(context);
    }

    public void setCallback(Callback com) {
        this.com = com;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER;
        else
            return ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM) {
            view = songInf.inflate(R.layout.album_song, parent, false);
            return new AlbumViewHolder(view);
        } else {
            view = songInf.inflate(R.layout.no_of_tracks, parent, false);
            return new TrackNoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof AlbumViewHolder) {
            AlbumViewHolder holder = (AlbumViewHolder) holder1;
            holder.title.setText(songs.get(position - 1).getTitle());
            holder.artist.setText(songs.get(position - 1).getArtist());
            holder.view.setTag(songs.get(position-1).getKey());
            holder.view.setOnClickListener(this);
        } else {
            TrackNoHolder holder = (TrackNoHolder) holder1;
            String title = songs.size() + (songs.size() == 1 ? " Track" : " Tracks");
            holder.toolbar.setTitle(title);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size() + 1;
    }

    @Override
    public void onClick(View v) {
        com.setResponse(Integer.parseInt(v.getTag().toString()));
    }
}

class AlbumViewHolder extends RecyclerView.ViewHolder {

    final TextView title;
    final TextView artist;
    final View view;

    public AlbumViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        title = (TextView) itemView.findViewById(R.id.title);
        artist = (TextView) itemView.findViewById(R.id.artist);
    }
}

class TrackNoHolder extends RecyclerView.ViewHolder {
    final Toolbar toolbar;

    public TrackNoHolder(View itemView) {
        super(itemView);
        toolbar = (Toolbar) itemView.findViewById(R.id.no_of_tracks);
    }
}