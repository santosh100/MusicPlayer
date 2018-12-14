package com.santosh.sahu.mymusicplayer;

public class Songs {

    long songId;
    String songName;
    String path;

    public long getSongId() {
        return songId;
    }

    public String getSongName() {
        return songName;
    }

    public String getPath() {
        return path;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
