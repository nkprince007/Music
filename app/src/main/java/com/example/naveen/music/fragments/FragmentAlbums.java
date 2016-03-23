package com.example.naveen.music.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naveen.music.R;
import com.example.naveen.music.activities.MainActivity;
import com.example.naveen.music.adapters.AlbumAdapter;
import com.example.naveen.music.models.Album;
import com.example.naveen.music.models.Song;
import com.example.naveen.music.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentAlbums extends Fragment {

    private Context context;
    private MainActivity activity;
    private View rootView;
    private ArrayList<Song> songList;
    private final ArrayList<Album> albums = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        context = activity.getBaseContext();
        rootView = activity.rootView;
        songList = (ArrayList<Song>) MainActivity.songList.clone();
        setPositions();
        createAlbums();
        initialize();
    }

    private void setPositions() {
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getAlbum().compareToIgnoreCase(b.getAlbum());
            }
        });
    }

    private void createAlbums() {
        String prevAlbum = "";
        for (int i = 0; i < songList.size(); i++) {
            Song song = songList.get(i);
            if (!prevAlbum.equals(song.getAlbum())) {
                String album = prevAlbum = song.getAlbum();
                albums.add(Album.getInstance(songList, album));
            }
        }
        Log.d("album", "No. of albums: " + albums.size());
    }

    private void initialize() {
        RecyclerView albumView = (RecyclerView) rootView.findViewById(R.id.album_list);
        albumView.setLayoutManager(new GridLayoutManager(context, 2));
        AlbumAdapter adapter = new AlbumAdapter(activity, albums);
        albumView.setAdapter(adapter);
        Logger.m("adapter is set");
    }
}
