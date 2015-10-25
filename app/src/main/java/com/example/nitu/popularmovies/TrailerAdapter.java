package com.example.nitu.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.nitu.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitus on 10/9/2015.
 */
public class TrailerAdapter extends CursorAdapter {
    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private final Context mContext;

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.list_item_trailer,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv=(TextView)view;
       /* tv.setText(cursor.getString(DetailActivityFragment.TrailerQuery.COL_TRAILER_KEY));*/

    }
}
