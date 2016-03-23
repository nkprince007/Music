package com.example.naveen.music.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naveen.music.R;
import com.example.naveen.music.activities.MainActivity;
import com.example.naveen.music.adapters.SongAdapter;
import com.example.naveen.music.extras.recycler_view_extras.DividerItemDecoration;
import com.example.naveen.music.models.Song;

import java.util.ArrayList;

public class FragmentSongs extends Fragment {

    private ArrayList<Song> songList;
    private Context context;
    private MainActivity activity;
    private View rootView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        context = activity.getBaseContext();
        rootView = activity.rootView;
        initialize();
    }

    @SuppressWarnings("unchecked")
    private void initialize() {
        songList = (ArrayList<Song>) MainActivity.songList.clone();
        RecyclerView songView = (RecyclerView) rootView.findViewById(R.id.song_list);
        songView.addItemDecoration(new DividerItemDecoration(context));
        songView.setLayoutManager(new LinearLayoutManager(context));
        registerForContextMenu(songView);
        SongAdapter adapter = new SongAdapter(activity, songList);
        songView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

}
