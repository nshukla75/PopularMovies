package com.example.nitu.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitu.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitus on 10/9/2015.
 */
public class MovieAdapter extends CursorAdapter {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    //private static final String KEY_ADAPTER_STATE = "MovieAdapter.KEY_ADAPTER_STATE";
    private final Context mContext;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    private byte[] getImage(Cursor cursor) {
        // get row indices for our cursor
        byte[] bb = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        return bb;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.grid_item_movie,parent,false);
        return view;
    }

     @Override
    public void bindView(View view, Context context, Cursor cursor) {
         ImageView imageView = (ImageView) view;
         byte[] bb=getImage(cursor);
         imageView.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));
         Log.e("image to grid", "Length-----" + bb.length);

     }
}








