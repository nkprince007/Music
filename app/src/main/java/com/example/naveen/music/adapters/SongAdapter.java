package com.example.naveen.music.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naveen.music.R;
import com.example.naveen.music.activities.SongActivity;
import com.example.naveen.music.models.Song;
import com.example.naveen.music.utils.BitmapWorkerTask;
import com.example.naveen.music.utils.Logger;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final ArrayList<Song> songs;
    private final LayoutInflater songInf;
    private final Context context;
    private final Activity activity;
    private int position = -1;

    public SongAdapter(Activity activity, ArrayList<Song> theSongs) {
        songs = theSongs;
        context = activity.getBaseContext();
        this.activity = activity;
        songInf = LayoutInflater.from(context);
        populateHeaders();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder1) {
        if (holder1 instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) holder1;
            holder.view.clearAnimation();
            holder.cancelTask();
            super.onViewDetachedFromWindow(holder1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (songs.get(position).getAlbum() == null)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = songInf.inflate(R.layout.song, parent, false);
            position++;
            return new ItemHolder(view, position);
        } else {
            View view = songInf.inflate(R.layout.header_song, parent, false);
            position++;
            return new HeaderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) holder1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.view.setBackground(context.getDrawable(R.drawable.ripple));
            }
            holder.cancelTask();
            holder.songView.setText(songs.get(position).getTitle());
            String artist = songs.get(position).getArtist();
            if (artist.equals("")) holder.artistView.setVisibility(View.GONE);
            else {
                holder.artistView.setVisibility(View.VISIBLE);
                holder.artistView.setText(artist);
            }
            holder.setBitmap(context, songs.get(position).getAlbumArtUri());
            holder.view.setTag(songs.get(position).getKey()); //holds the key to playback of the song

//            Animation animation = AnimationUtils.loadAnimation(holder.view.getContext(),
//                    ((position > lastPosition) ? R.anim.slide_in_right : R.anim.slide_in_left));
//            holder.view.startAnimation(animation);

            holder.albumArtView.setOnClickListener(this);
            holder.albumArtView.setTag(songs.get(position).getAlbumArtUri());
        } else if (holder1 instanceof HeaderHolder) {
            HeaderHolder holder = (HeaderHolder) holder1;
            holder.songView.setText(songs.get(position).getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, SongActivity.class);
        intent.putExtra("uri", v.getTag().toString());
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, v.getTransitionName());
            activity.startActivity(intent, optionsCompat.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private void populateHeaders() {
        char prevC = '0';
        int count = 0;
        for (int i = 0; i < songs.size(); i++) {
            Song currSong = songs.get(i);
            char c = currSong.getTitle().charAt(0);
            if (Character.isLetter(c)) {
                if (prevC == c) continue;
                count++;
                songs.add(i, new Song(count, c + "", null));
            } else {
                c = '#';
                if (prevC == c) continue;
                count++;
                songs.add(i, new Song(count, c + "", null));
            }
            prevC = c;
        }
        Logger.m("Total no.of headers: " + count);
    }

//    @Override
//    public boolean onLongClick(View v) {
//        Uri uri = Uri.parse(songs.get(Integer.parseInt(v.getTag().toString())).getAbsolutePath());
//        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        intent.setType("audio/*");
//        ArrayList<Uri> uris = new ArrayList<>();
//        uris.add(uri);
//        intent.putExtra(Intent.EXTRA_STREAM,uris);
//        Intent chooser = Intent.createChooser(intent, "Share via...");
//        activity.startActivity(chooser);
//        return true;
//    }
}

class ItemHolder extends RecyclerView.ViewHolder {

    final TextView songView;
    final TextView artistView;
    final ImageView albumArtView;
    final View view;
    private BitmapWorkerTask task;

    public ItemHolder(View itemView, int position) {
        super(itemView);
        view = itemView;
        view.setTag(position);
        albumArtView = (ImageView) itemView.findViewById(R.id.imageView);
        songView = (TextView) itemView.findViewById(R.id.song_title);
        songView.setSelected(true);
        artistView = (TextView) itemView.findViewById(R.id.song_artist);
        artistView.setSelected(true);
    }

    public void setBitmap(Context context, Uri albumArtUri) {
        task = new BitmapWorkerTask(albumArtView);
        task.execute(context, albumArtUri);
    }

    public void cancelTask() {
        if (task != null)
            task.cancel(true);
    }
}

class HeaderHolder extends RecyclerView.ViewHolder {

    // --Commented out by Inspection (12/23/2015 7:26 PM):private final View view;
    final TextView songView;
    // --Commented out by Inspection (12/23/2015 7:26 PM):private long count=0;

    public HeaderHolder(View itemView) {
        super(itemView);
//        view = itemView;
        songView = (TextView) itemView.findViewById(R.id.textHeader);
    }

}
