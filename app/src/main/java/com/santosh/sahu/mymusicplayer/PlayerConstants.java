package com.santosh.sahu.mymusicplayer;

import android.os.Handler;

import java.util.ArrayList;

public class PlayerConstants {

    public static ArrayList<Songs> SONGS_LIST = new ArrayList<>();
    public static Handler SONG_CHANGE_HANDLER;
    public static Handler PLAY_PAUSE_HANDLER;
    public static long CURRENT_SONG_ID = 0;
    public static int CURRENT_SONG_NUMBER = -1;
    public static boolean PAUSED = true;
}
