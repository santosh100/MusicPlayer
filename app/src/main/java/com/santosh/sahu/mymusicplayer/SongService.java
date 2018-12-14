package com.santosh.sahu.mymusicplayer;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SongService extends Service implements AudioManager.OnAudioFocusChangeListener{

        private MediaPlayer mp;
        AudioManager audioManager;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            mp = new MediaPlayer();
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(PlayerConstants.CURRENT_SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1))
                        PlayerConstants.CURRENT_SONG_NUMBER++;
                    else
                        PlayerConstants.CURRENT_SONG_NUMBER = 0;

                    PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(PlayerConstants.CURRENT_SONG_NUMBER).getSongId();
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }
            });

            super.onCreate();
        }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d("AUDIO","1");

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                if(mp != null)
                    PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0,"stop"));
                Log.d("AUDIO","1");
            break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(mp!=null)
                    PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0,"pause"));
                Log.d("AUDIO","2");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mp != null)
                    mp.pause();
                Toast.makeText(this,"DUCK",Toast.LENGTH_SHORT).show();
                Log.d("AUDIO","3");
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                if(mp != null)
                mp.start();
                Log.d("AUDIO","4");
                break;
        }
    }

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            String songPath = "";

         try {
             for (Songs s : PlayerConstants.SONGS_LIST) {
                 if (s.getSongId() == PlayerConstants.CURRENT_SONG_ID) {
                     songPath = s.getPath();
                     break;
                 }
             }

                playSong(songPath);
                mp.start();
                PlayerConstants.PAUSED = false;
             try {
                 MainActivity.upateUI();
             } catch (Exception e) { Log.d("MAIN_ACTIVITY","Play_pause");   }


             // newNotification();

                PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        String songPath = "";

                        if(mp == null)
                            return false;
                        for (Songs s : PlayerConstants.SONGS_LIST) {
                            if (s.getSongId() == PlayerConstants.CURRENT_SONG_ID) {
                                songPath = s.getPath();
                                break;
                            }
                        }
                   //     newNotification();
                        try{
                            playSong(songPath);
                            mp.start();
                        }catch(Exception e){
                            Log.d("SONG_CHANGE_HANDLER :","ERROR :", e);
                            //e.printStackTrace();
                        }

                        try {
                            MainActivity.upateUI();

                        } catch (Exception e) { Log.d("MAIN_ACTIVITY","Play_pause");   }

                        return false;
                    }
                });


             PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Handler.Callback() {
                 @Override
                 public boolean handleMessage(Message msg) {
                     String message = (String) msg.obj;
                     if (mp == null) {
                        mp = new MediaPlayer();

                        // return false;
                     }
                     if (message.equalsIgnoreCase("play")) {
                         String songPath = "";

                         for (Songs s : PlayerConstants.SONGS_LIST) {
                             if (s.getSongId() == PlayerConstants.CURRENT_SONG_ID) {
                                 songPath = s.getPath();
                                 break;
                             }
                         }

                         Log.d("PLAY_PAUSE_HANDLER ", "getdata:" + songPath);
                         playSong(songPath);
                         mp.start();
                         PlayerConstants.PAUSED = false;

                     } else if (message.equalsIgnoreCase("pause")) {
                         mp.pause();
                         PlayerConstants.PAUSED = true;

                     } else if (message.equalsIgnoreCase("stop")) {
                         mp.stop();
                         mp = null;
                         PlayerConstants.PAUSED = true;

                     } else if (message.equalsIgnoreCase("exit")) {
                         mp.pause();
                         //PlayerConstants.PAUSED = true;
                         Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();

                         try {
                             MainActivity mainActivity = new MainActivity();
                             //mainActivity.MA_LL_MAIN_ACTIVITY.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                             mainActivity.finish();
                         } catch (Exception e) {
                             Log.d("PLAY_PAUSE_HANDLER : ", "EXIT : ", e);
                         }

                         stopSelf();
                         System.exit(0);
                     }

                     try {
                         MainActivity.upateUI();

                     } catch (Exception e) { Log.d("MAIN_ACTIVITY","Play_pause");   }

                     return false;
                 }
             });


         } catch (Exception e) {
                e.printStackTrace(); }

            return START_NOT_STICKY;
        }


        @Override
        public void onDestroy() {
            if(mp != null){
                mp.stop();
                mp = null;
            }
            super.onDestroy();
        }

        private void playSong(String songPath) {
            try {
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                mp.reset();
                mp.setDataSource(songPath);
                mp.prepare();
                //mp.start();
                //handler.postDelayed(run,50);
            } catch (IOException e) {
                Log.d("SERVICE","PLAY_SONG");
                e.printStackTrace();
            }
        }

    }

