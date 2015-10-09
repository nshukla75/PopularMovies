package com.example.nitu.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitus on 10/9/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Context context;
    //private final List<String> urls = new ArrayList<>();
    private final List<MovieData> movies = new ArrayList<>();

    public MovieAdapter(Context context) {
        this.context = context;
        //Collections.addAll(urls, moviePosterPath);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(context);
        }
        ImageView imageView = (ImageView) convertView;
        MovieData movie = getItem(position);
        String url =movie.getPoster_path();
        Log.v(LOG_TAG, " URL " + url);
        Picasso.with(context).load(url).into(imageView);
        return convertView;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public MovieData getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replace(List<MovieData> moviesData) {
        //this.urls.clear();
        this.movies.clear();
        //this.urls.addAll(urls);
        this.movies.addAll(moviesData);
        notifyDataSetChanged();
    }
}
