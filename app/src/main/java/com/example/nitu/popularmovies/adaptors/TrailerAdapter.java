package com.example.nitu.popularmovies.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.TrailerFragment;
import com.example.nitu.popularmovies.data.MovieContract;

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
       TextView tv=(TextView)view.findViewById(R.id.list_item_trailer_textview);
       tv.setText(cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY)));
    }
}
