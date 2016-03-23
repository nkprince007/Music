package com.example.naveen.music.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naveen.music.R;
import com.example.naveen.music.activities.AlbumActivity;
import com.example.naveen.music.activities.MainActivity;
import com.example.naveen.music.models.Album;
import com.example.naveen.music.models.Song;
import com.example.naveen.music.utils.BitmapWorkerTask;
import com.example.naveen.music.utils.Logger;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder> implements View.OnClickListener {

    private static final String SEND_ALBUM = "send_album";
    private static final String SEND_SONG_LIST = "song_list";
    private static final int KEY = R.id.action_end ;
    private final Activity activity;
    private final Context context;
    private final ArrayList<Song> songList;
    private final ArrayList<Album> albumList;
    private final LayoutInflater albumInf;
    private int lastPosition = -1;

    @SuppressWarnings("unchecked")
    public AlbumAdapter(Activity activity, ArrayList<Album> albumList) {
        this.activity = activity;
        context = this.activity.getApplicationContext();
        this.albumList = albumList;
        albumInf = LayoutInflater.from(context);
        songList = (ArrayList<Song>) MainActivity.songList.clone();
    }

    @Override
    public void onViewDetachedFromWindow(AlbumHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.rootView.clearAnimation();
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = albumInf.inflate(R.layout.album, parent, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, int position) {
        holder.cancelTask();
        holder.rootView.setTag(position);
        holder.albumName.setText(albumList.get(position).getAlbumTitle());
        holder.artistName.setText(albumList.get(position).getArtistName());
        holder.setBitmap(context, albumList.get(position).getAlbumArtUri());
        Animation animation = AnimationUtils.loadAnimation(holder.rootView.getContext(),
                ((position > lastPosition) ? R.anim.slide_in_right : R.anim.slide_in_left));
        holder.rootView.setTag(KEY,holder.albumArt);
        holder.rootView.startAnimation(animation);
        holder.rootView.setOnClickListener(this);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, AlbumActivity.class);
        Album album = albumList.get((int) v.getTag());
        intent.putExtra(SEND_ALBUM, album);
        ArrayList<Song> songArrayList = parseList(album.getSongsInAlbum());
        intent.putParcelableArrayListExtra(SEND_SONG_LIST, songArrayList);
        if (Build.VERSION.SDK_INT >= 21) {
            View view = (View) v.getTag(KEY);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,view, view.getTransitionName());
            activity.startActivity(intent, optionsCompat.toBundle());
        } else {
            activity.startActivity(intent);
        }
        Log.d("nav", "INTENT CALLED");
    }

    private ArrayList<Song> parseList(ArrayList<String> songsInAlbum) {
        ArrayList<Song> songArrayList = new ArrayList<>();
        for (int i = 0; i < songsInAlbum.size(); i++) {
            songArrayList.add(songList.get(Integer.parseInt(songsInAlbum.get(i))));
        }
        return songArrayList;
    }
}

class AlbumHolder extends RecyclerView.ViewHolder {

    final TextView albumName;
    final TextView artistName;
    final View rootView;
    final ImageView albumArt;
    private BitmapWorkerTask task;

    public AlbumHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        Logger.m(rootView.getId() + "");
        albumName = (TextView) rootView.findViewById(R.id.album_name);
        artistName = (TextView) rootView.findViewById(R.id.artist_name);
        albumArt = (ImageView) rootView.findViewById(R.id.album_art);
    }

    public void setBitmap(Context context, Uri uri) {
        task = new BitmapWorkerTask(albumArt);
        int res = context.getResources().getDisplayMetrics().widthPixels;
        task.execute(context, uri, res);
    }

    public void cancelTask() {
        if (task != null) {
            task.cancel(true);
        }
    }
}