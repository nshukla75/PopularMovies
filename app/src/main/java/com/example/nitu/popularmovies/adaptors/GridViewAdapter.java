package com.example.nitu.popularmovies.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by nitus on 10/9/2015.
 */
public class GridViewAdapter extends CursorAdapter {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    //private static final String KEY_ADAPTER_STATE = "MovieAdapter.KEY_ADAPTER_STATE";
    private final Context mContext;

    public GridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.grid_item_movie,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder)view.getTag();
        byte[] bb= Utility.getImage(cursor);
        if (bb!=null) {
            holder.imgMovie.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));
            Log.e("image to grid", "Length-----" + bb.length);
        }
        else
            Picasso.with(context)
                    .load(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)))
                    .error(R.drawable.abc_btn_rating_star_off_mtrl_alpha)
                    .into(holder.imgMovie);
        holder.txtMovie.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
    }

    static class ViewHolder {
        ImageView imgMovie;
        TextView txtMovie;
        public ViewHolder(View view) {
            imgMovie = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
            txtMovie = (TextView) view.findViewById(R.id.txt_title);
        }

    }

}








