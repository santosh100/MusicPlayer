package com.santosh.sahu.mymusicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final ClickListener listener) {
        this.clickListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {   return true;    }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && clickListener != null && gestureDetector.onTouchEvent(e))
            clickListener.onItemClick(childView, recyclerView.getChildLayoutPosition(childView));

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {  }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {   }

}
