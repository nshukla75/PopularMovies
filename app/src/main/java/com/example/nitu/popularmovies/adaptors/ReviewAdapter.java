package com.example.nitu.popularmovies.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.ReviewFragment;

/**
 * Created by nitus on 10/9/2015.
 */
public class ReviewAdapter extends CursorAdapter {
    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private final Context mContext;

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view =  LayoutInflater.from(context).inflate(R.layout.list_item_review,parent,false);
        final ViewHolder holder = new ViewHolder();
        holder.author = (TextView) view.findViewById(R.id.author);
        holder.content = (TextView) view.findViewById(R.id.content);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder)view.getTag();
        holder.author.setText(cursor.getString(ReviewFragment.ReviewQuery.COL_REVIEW_AUTHOR));
        holder.content.setText(cursor.getString(ReviewFragment.ReviewQuery.COL_REVIEW_CONTENT));
        Log.e("Review Adaptor",cursor.getString(ReviewFragment.ReviewQuery.COL_REVIEW_AUTHOR));
    }
    static class ViewHolder {
        TextView author;
        TextView content;
    }
}
