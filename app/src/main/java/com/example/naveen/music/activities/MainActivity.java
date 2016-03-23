package com.example.naveen.music.activities;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.MediaController;

import com.example.naveen.music.R;
import com.example.naveen.music.adapters.ViewPagerAdapter;
import com.example.naveen.music.models.Song;
import com.example.naveen.music.services.MusicService;
import com.example.naveen.music.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static android.provider.MediaStore.Audio.Media.ALBUM;
import static android.provider.MediaStore.Audio.Media.ALBUM_ID;
import static android.provider.MediaStore.Audio.Media.ARTIST;
import static android.provider.MediaStore.Audio.Media.DATA;
import static android.provider.MediaStore.Audio.Media.DATE_MODIFIED;
import static android.provider.MediaStore.Audio.Media.DURATION;
import static android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Audio.Media.IS_MUSIC;
import static android.provider.MediaStore.Audio.Media.SIZE;
import static android.provider.MediaStore.Audio.Media.TITLE;
import static android.provider.MediaStore.Audio.Media.TRACK;
import static android.provider.MediaStore.Audio.Media.YEAR;
import static android.provider.MediaStore.Audio.Media._ID;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public static ArrayList<Song> songList;
    public static MusicService musicSrv;
    private final Random random = new Random();
    public View rootView;
    private Animation animUpFromBottom;
    private MediaController controller;
    private Intent playIntent;
    private boolean musicBound = false;
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    private boolean paused = false;
    private boolean playbackPaused = false;

    /// methods from activity are overridden below

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        //putting up an extradition :-) transition
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.BASE);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        songList = new ArrayList<>();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
//        editText = (EditText) findViewById(R.id.editText);
        initSongList();
        setController();
        animUpFromBottom = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        unbindService(musicConnection);
        musicSrv = null;
        super.onDestroy();
        Logger.m("music service null");
    }

    /// methods overridden from activity end here

    //this method adjusts the MediaController according to the playback of the song
    private void setController() {
        controller = new MediaController(this);
        controller.setAnimation(animUpFromBottom);
        controller.animate();
        controller.setPrevNextListeners(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playNext();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPrev();
                    }
                });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.BASE));
    }

    //methods implemented from the interface MediaController.MediaPlayerControl begin here
    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosition();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        return musicSrv != null && musicBound && musicSrv.isPng();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }


    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    // methods implemented from MediaController.MediaPlaybackControl end here


    //this method is called whenever user clicks on the view of a song
    public void songPicked(View view) {
        musicSrv.setSong((Long) view.getTag());
        musicSrv.setController(controller);
        musicSrv.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                musicSrv.setSong(random.nextInt(songList.size()));
                musicSrv.setController(controller);
                musicSrv.playSong();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                MainActivity.this.finish();
                break;
            case R.id.action_search:
//                songView.removeAllViews();
//                beginQuery();
//                if (!searchHelper) item.setTitle("BACK");
//                else item.setTitle("SEARCH");
//                searchHelper = !searchHelper;
                Snackbar.make(rootView, "Action not yet implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //method is called whenever user clicks on next button in the controller widget
    //or when the song gets finished during playback
    private void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
    }

    //method is called whenever user clicks on previous button in the controller widget
    private void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isPlaying()) {
            finish();
            super.onBackPressed();
        }
    }

    private void initSongList() {
        final Uri uri = EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                _ID,
                ARTIST,
                TITLE,
                ALBUM,
                DATA,
                ALBUM_ID,
                DURATION,
                SIZE,
                DATE_MODIFIED,
                YEAR,
                TRACK,
        };
        final String where = IS_MUSIC + "=1";
        final Cursor musicCursor = getContentResolver().query(uri, cursor_cols, where, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(TITLE);
            int idColumn = musicCursor.getColumnIndex(_ID);
            int albumColumn = musicCursor.getColumnIndex(ALBUM);
            int artistColumn = musicCursor.getColumnIndex(ARTIST);
            int dataColumn = musicCursor.getColumnIndex(DATA);
            int albumIdColumn = musicCursor.getColumnIndex(ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(DURATION);
            int yearColumn = musicCursor.getColumnIndex(YEAR);
            int trackColumn = musicCursor.getColumnIndex(TRACK);
            int sizeColumn = musicCursor.getColumnIndex(SIZE);
            int dateModifiedColumn = musicCursor.getColumnIndex(DATE_MODIFIED);
            do {
                long id = musicCursor.getLong(idColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                String title = musicCursor.getString(titleColumn);
                String data = musicCursor.getString(dataColumn);
                String year = musicCursor.getString(yearColumn);
                String track = musicCursor.getString(trackColumn);
                int duration = musicCursor.getInt(durationColumn);
                long dateModified = musicCursor.getLong(dateModifiedColumn);
                long albumId = musicCursor.getLong(albumIdColumn);
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                Logger.m(title + " duration:" + duration);
                if (artist.equals("<unknown>"))
                    artist = "Unknown Artist";
                songList.add(new Song(id, title, artist, album, albumArtUri, data));
            } while (musicCursor.moveToNext());
            musicCursor.close();
            String notification = songList.size() + " songs found on the device.";
            Snackbar.make(rootView, notification, Snackbar.LENGTH_LONG).show();
            Collections.sort(songList, new Comparator<Song>() {
                @Override
                public int compare(Song lhs, Song rhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
            for (int i = 0; i < songList.size(); i++) {
                songList.get(i).setKey(i);
            }
            Log.d("nav", notification);
        }
//        initEditText();
    }

    //    private void initEditText() {
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus && v.getVisibility() == View.VISIBLE) {
//                    imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
//                } else {
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                    searchHelper = true;
//                }
//            }
//        });
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!searchList.isEmpty())
//                    searchList.clear();
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int i = 0;
//                for (Song currentSong : songList) {
//                    if (currentSong.getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
//                        searchList.add(currentSong);
//                        adapter.notifyItemInserted(i++);
//                    }
//                }
//                if (searchList.isEmpty()) {
//                    Song song = new Song(-1, "No matching suggestions found", "", "", null);
//                    searchList.add(song);
//                }
//            }
//        });
//    }

    //yet to provide implementation to this method to handle search operation
//    private void beginQuery() {
//        if (searchHelper && imm.isAcceptingText()) {
//            editText.setVisibility(View.GONE);
//            editText.clearFocus();
//            editText.setText("");
//            return;
//        }
//        editText.setVisibility(View.VISIBLE);
//        editText.requestFocus();
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isPlaying() || playbackPaused) {
            controller.show(1500);
        }
        return super.dispatchTouchEvent(ev);
    }
}
