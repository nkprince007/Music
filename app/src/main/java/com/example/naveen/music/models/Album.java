package com.example.naveen.music.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    private ArrayList<String> songsInAlbum = new ArrayList<>();
    private String albumTitle;
    private Uri albumArtUri;
    private int noOfTracks = 0;
    private String artistName;

    private Album() {
    }

    private Album(Parcel in) {
        albumTitle = in.readString();
        albumArtUri = in.readParcelable(Uri.class.getClassLoader());
        noOfTracks = in.readInt();
        artistName = in.readString();
        String str[] = new String[noOfTracks];
        in.readStringArray(str);
        songsInAlbum.clear();
        Collections.addAll(songsInAlbum, str);
        Log.d("parcel", songsInAlbum.size() + " after retrieving");
    }

    public static Album getInstance(ArrayList<Song> songs, String albumTitle) {
        Album album = new Album();
        if (albumTitle != null && !albumTitle.equals("") && !songs.isEmpty()) {
            for (int i = 0; i < songs.size(); i++) {
                Song currSong = songs.get(i);
                if (currSong.getAlbum().equals(albumTitle)) {
                    if (album.albumTitle == null) {
                        album.setAlbumTitle(albumTitle);
                        album.setAlbumArtUri(currSong.getAlbumArtUri());
                        album.setArtistName(currSong.getArtist());
                    }
                    album.noOfTracks++;
                    album.songsInAlbum.add(Long.toString(currSong.getKey()));
                }
            }
        }
        Log.d("album", "album: " + album.albumTitle + " albumArtUri: " + album.albumArtUri
                + " no of tracks: " + album.noOfTracks);
        return album;
    }

    public ArrayList<String> getSongsInAlbum() {
        return songsInAlbum;
    }

    public void setSongsInAlbum(ArrayList<String> songsInAlbum) {
        this.songsInAlbum = songsInAlbum;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    private void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    private void setAlbumArtUri(Uri albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public int getNoOfTracks() {
        return noOfTracks;
    }

    public void setNoOfTracks(int noOfTracks) {
        this.noOfTracks = noOfTracks;
    }

    public String getArtistName() {
        return artistName;
    }

    private void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumTitle);
        dest.writeParcelable(albumArtUri, flags);
        dest.writeInt(noOfTracks);
        dest.writeString(artistName);
        Log.d("parcel", songsInAlbum.size() + " before parcelling");
        dest.writeStringArray(songsInAlbum.toArray(new String[1]));
    }
}
