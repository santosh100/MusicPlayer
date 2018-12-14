package com.santosh.sahu.mymusicplayer;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int RES=21;

    public RecyclerView RECYCLER_VIEW;
    public static RecyclerAdapter mAdapter;

    ArrayList<Songs> songsArrayList;

    static TextView textView;

    static Button buttonPause;
    static Button buttonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        textView = findViewById(R.id.tvSongName);
        buttonPause = findViewById(R.id.bPause);
        buttonPlay = findViewById(R.id.bPlay);
        RECYCLER_VIEW = findViewById(R.id.recyclerView);



        ask(Manifest.permission.READ_EXTERNAL_STORAGE,RES);

    }


    private void set_listeners() {
        RECYCLER_VIEW.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), RECYCLER_VIEW, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playControl();
                PlayerConstants.CURRENT_SONG_NUMBER = position;
                PlayerConstants.CURRENT_SONG_ID = PlayerConstants.SONGS_LIST.get(position).getSongId();

                PlayerConstants.PAUSED = false;

                for (Songs s : PlayerConstants.SONGS_LIST) {
                    if (s.getSongId() == PlayerConstants.CURRENT_SONG_ID) {
                        textView.setText(s.getSongName());
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }));
    }

    public void playControl() {
        boolean isServiceRunning = isServiceRunning(SongService.class.getName());
        if (!isServiceRunning) {
            Intent i = new Intent(this.getApplicationContext(),SongService.class);
            startService(i);
        }
        else
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0,"play"));

    }

    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void upateUI() {

        for (Songs s : PlayerConstants.SONGS_LIST) {
            if (s.getSongId() == PlayerConstants.CURRENT_SONG_ID) {
                textView.setText(s.getSongName());
                break;
            }
        }

        if(PlayerConstants.PAUSED) {
          //  Log.d("BUTTON","PAUSED");
            buttonPause.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
        } else {
          //  Log.d("BUTTON","PLAYED");
            buttonPlay.setVisibility(View.GONE);
            buttonPause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void myExit(){

        boolean isServiceRunning = isServiceRunning(SongService.class.getName());
        if(isServiceRunning) {
            Intent i = new Intent(this.getApplicationContext(),SongService.class);
            stopService(i);
        }
        PlayerConstants.PAUSED=true;
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {

            new AlertDialog.Builder(this)
                    .setTitle("Stop Playing")
                    .setMessage("Are you sure you want to stop enjoying music?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myExit();   // this is called when you pressed yes
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

        public ArrayList<Songs> listOfSongs(){
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String sortOrder = "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC";
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, sortOrder);
            ArrayList<Songs> listSongs = new ArrayList<>();
            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Songs songData = new Songs();
                    Long _id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    songData.setPath(path);
                    songData.setSongId(_id);
                    songData.setSongName(title);
                    listSongs.add(songData);
                }
            }cursor.close();
            Log.d("SIZE", "SIZE: " + listSongs.size());
            return listSongs;
        }

        private void ask(final String permission,final Integer result_code) {
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {permission},result_code);
            }
            else
                init();
    }

    private void init() {

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPause.setVisibility(View.VISIBLE);
                buttonPlay.setVisibility(View.GONE);
                playControl();
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlay.setVisibility(View.VISIBLE);
                buttonPause.setVisibility(View.GONE);
                PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0,"stop"));
            }
        });

        songsArrayList = new ArrayList<>();
        songsArrayList = listOfSongs();
        PlayerConstants.SONGS_LIST = songsArrayList;

        try {
            textView.setText(PlayerConstants.SONGS_LIST.get(0).getSongName());
        }catch (Exception e) { Log.d("TEXT_VIE","ERROR"); }


        set_listeners();

        mAdapter = new RecyclerAdapter(songsArrayList,this);
        RECYCLER_VIEW.setAdapter(mAdapter);
        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RES:if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    init();
            else {
                Toast.makeText(this,"PERMISSION DENIED",Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
            default:Toast.makeText(this,"SWITCH DEFAULT",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        upateUI();
        super.onResume();
    }




}
