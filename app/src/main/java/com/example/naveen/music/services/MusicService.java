package com.example.naveen.music.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.naveen.music.R;
import com.example.naveen.music.utils.Logger;
import com.example.naveen.music.activities.MainActivity;
import com.example.naveen.music.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private static final int NOTIFY_ID = 1;
    private static String status = "Shuffle off";
    private final IBinder musicBind = new MusicBinder();
    private MediaController controller;
    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosition;
    private String songTitle = "";
    private boolean shuffle = false;
    private Random rand;
    private String artistName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        rand = new Random();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void setShuffle() {
        if (shuffle) {
            status = "Shuffle off";
            shuffle = false;
        } else {
            shuffle = true;
            status = "Shuffle on";
        }
        playNext();
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    private void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //   if (player.getCurrentPosition() == 0) {
        mp.reset();
        Logger.m("onCompletion Called");
        playNext();
        //   }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setOngoing(true)
                .setTicker("Now playing:" + songTitle)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentTitle(songTitle)
                .setContentInfo(status)
                .setContentText(artistName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MAX);
            Notification not = builder.build();
            startForeground(NOTIFY_ID, not);
        }
        if (controller != null)
            controller.show(3000);
    }

// --Commented out by Inspection START (12/23/2015 7:25 PM):
//    public MediaController getController() {
//        return this.controller;
//    }
// --Commented out by Inspection STOP (12/23/2015 7:25 PM)

    public void setController(MediaController controller) {
        this.controller = controller;
    }

    public void setSong(long songIndex) {
        songPosition = (int) songIndex;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void playSong() {
        player.reset();
        Song playSong = songs.get(songPosition);
        artistName = playSong.getArtist();
        songTitle = playSong.getTitle();
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
            Logger.m(trackUri.getPath());
        } catch (IOException e) {
            Log.e("MUSIC SERVICE", "Error Setting Data Source", e);
        }
        player.prepareAsync();
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int position) {
        player.seekTo(position);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        songPosition--;
        if (songPosition == 0)
            songPosition = songs.size() - 1;
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = songPosition;
            while (newSong == songPosition) {
                newSong = rand.nextInt(songs.size());
            }
            songPosition = newSong;
        } else {
            songPosition++;
            if (songPosition == songs.size())
                songPosition = 0;
        }
        playSong();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

}
