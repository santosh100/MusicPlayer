package com.santosh.sahu.mymusicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Songs> mSongsList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context context;

    public RecyclerAdapter(ArrayList<Songs> listOfSongs, Context context) {
        mSongsList = listOfSongs;
        mPref = context.getSharedPreferences("song", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        this.context = context;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView song_name, artist_name, duration;
        LinearLayout row;

        private ViewHolder(View view) {
            super(view);
            song_name = view.findViewById(R.id.song_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // set the view's size, margins, paddings and layout parameters
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_songs,parent,false);

        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.song_name.setText(mSongsList.get(position).getSongName());
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

}
