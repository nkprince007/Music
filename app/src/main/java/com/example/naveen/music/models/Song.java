package com.example.naveen.music.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    private long id;
    private String title;
    private String artist;
    private String album;
    private Uri albumArtUri;
    private long key = -1;
    private String absolutePath;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public Song(long id, String title, String artist, String album, Uri uri, String data) {
        setId(id);
        setTitle(title);
        setArtist(artist);
        setAlbum(album);
        setAlbumArtUri(uri);
        setAbsolutePath(data);
    }

    private Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        albumArtUri = in.readParcelable(Uri.class.getClassLoader());
        key = in.readLong();
        absolutePath = in.readString();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    private void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getAlbum() {
        return album;
    }

    private void setAlbum(String album) {
        this.album = album;
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    private void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    private void setAlbumArtUri(Uri albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeParcelable(albumArtUri, flags);
        dest.writeLong(key);
        dest.writeString(absolutePath);
    }
}
