package com.example.naveen.music.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.naveen.music.R;
import com.example.naveen.music.adapters.AlbumViewAdapter;
import com.example.naveen.music.interfaces.Callback;
import com.example.naveen.music.models.Album;
import com.example.naveen.music.models.Song;
import com.example.naveen.music.utils.BitmapWorkerTask;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity implements Callback {

    private static final String GET_ALBUM = "send_album";
    private static final String GET_SONG_LIST = "song_list";
    private Callback frag;
    private Album currAlbum;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start the playback by clicking on songs for now", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final ImageView imageView = (ImageView) findViewById(R.id.album_art_collapsible);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    try {
                        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                        int width = getResources().getDisplayMetrics().widthPixels;
                        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                        task.execute(getBaseContext(), currAlbum.getAlbumArtUri(), width, toolbarLayout, fab);
                    } catch (NullPointerException ignored) {
                        ignored.printStackTrace();
                    }
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }

        initialize();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(currAlbum.getAlbumTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        int width = getResources().getDisplayMetrics().widthPixels;
        task.execute(getBaseContext(), currAlbum.getAlbumArtUri(), width);
    }

    private void initialize() {
        currAlbum = getIntent().getParcelableExtra(GET_ALBUM);
        songList = getIntent().getParcelableArrayListExtra(GET_SONG_LIST);
        for (int i = 0; i < songList.size(); i++) {
            Log.d("nav", songList.get(i).getTitle() + " retrieved from parcel");
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tracks_in_album);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext()));
        AlbumViewAdapter adapter = new AlbumViewAdapter(this, songList);
        adapter.setCallback(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setResponse(int position) {
        MainActivity.musicSrv.setSong(position);
        MainActivity.musicSrv.playSong();
    }
}
