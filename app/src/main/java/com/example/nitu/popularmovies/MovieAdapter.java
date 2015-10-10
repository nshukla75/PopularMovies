package com.example.nitu.popularmovies;

import android.content.Context;
import android.os.Bundle;
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
    private static final String KEY_ADAPTER_STATE = "MovieAdapter.KEY_ADAPTER_STATE";
    private final Context context;
    private final List<MovieData> movies = new ArrayList<>();

    public MovieAdapter(Context context) {
        this.context = context;
    }

    public void onSaveInstanceState(Bundle outState) {
             outState.putParcelableArrayList(KEY_ADAPTER_STATE, getAllItems());
           }

    public ArrayList<MovieData> getAllItems(){
        ArrayList<MovieData> objects = new ArrayList<MovieData>(getCount());
        for (int i = 0; i < getCount(); i++) {
            objects.add(getItem(i));
        }
        return objects;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_ADAPTER_STATE)) {
            ArrayList<MovieData> objects = savedInstanceState
                    .getParcelableArrayList(KEY_ADAPTER_STATE);
            setItems(objects);
        }
    }

    public void setItems(ArrayList<MovieData> items){
        this.movies.clear();
        this.movies.addAll(items);
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
        this.movies.clear();
        this.movies.addAll(moviesData);
        notifyDataSetChanged();
    }
}
