package com.example.nitu.popularmovies.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.model.MovieData;
import com.squareup.picasso.Picasso;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Created by nitus on 10/9/2015.
 */
public class GridViewAdapter extends CursorAdapter {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final String sImgSize;
    private final String sImgUrl;

    private final Context mContext;

    public GridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        sImgUrl = context.getString(R.string.tmdb_image_base_url);
        sImgSize = context.getString(R.string.grid_image_size);
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
        byte[] bMovieObj = cursor.getBlob(1);
        MovieData movie = SerializationUtils.deserialize(bMovieObj);
        String load = String.format(sImgUrl, sImgSize, movie.poster_path);
            Picasso.with(context)
                    .load(load)
                    .error(R.drawable.abc_btn_rating_star_off_mtrl_alpha)
                    .resize(550,775)
                    .into(holder.imgMovie);
    }

    static class ViewHolder {
        ImageView imgMovie;
        public ViewHolder(View view) {
            imgMovie = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
        }

    }

}








