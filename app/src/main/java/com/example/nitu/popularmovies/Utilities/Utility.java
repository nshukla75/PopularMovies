package com.example.nitu.popularmovies.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.model.MovieData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by nitus on 10/17/2015.
 */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new NaturalDeserializer()).create();
    public static String getPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default)));
    }
    public static String getYear(String date){
        String year = "";
        if(date != null && date.length() > 0 && date.indexOf("-") > 0) {
            year = date.substring(0, date.indexOf("-"));
        }else{
            year = date;
        }
        return year;
    }
    public static void makeMenuItemInvisible(Menu menu, int... ids) {
        for (int id : ids) {
            MenuItem mi = menu.findItem(id);
            if (mi != null) mi.setVisible(false);
        }
    }
    public static Gson getGson() {
        return gson;
    }
    public static String readStreamToString(InputStream is) {
        BufferedReader bis = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bis.readLine()) != null) sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static List<MovieData> convertJsonMapToMovieList(Object resultsObj) {
        List<Map<String, Object>> results = (List<Map<String, Object>>) resultsObj;
        Set<MovieData> movies = new HashSet<>();
        for (Map<String, Object> m : results) {
            MovieData movie = new MovieData();
            for (Map.Entry<String, Object> e : m.entrySet())
                switch (e.getKey()) {
                    case "adult":
                        movie.adult = (Boolean) e.getValue();
                        break;
                    case "backdrop_path":
                        movie.backdrop_path = (String) e.getValue();
                        break;
                    case "genre_ids":
                        movie.genre_ids = (ArrayList<Double>) e.getValue();
                        break;
                    case "id":
                        movie.id = ((Double) e.getValue()).longValue();
                        break;
                    case "original_language":
                        movie.original_language = (String) e.getValue();
                        break;
                    case "original_title":
                        movie.original_title = (String) e.getValue();
                        break;
                    case "overview":
                        movie.overview = (String) e.getValue();
                        break;
                    case "release_date":
                        movie.release_date = (String) e.getValue();
                        break;
                    case "poster_path":
                        movie.poster_path = (String) e.getValue();
                        break;
                    case "popularity":
                        movie.popularity = (Double) e.getValue();
                        break;
                    case "title":
                        movie.title = (String) e.getValue();
                        break;
                    case "video":
                        movie.video = (Boolean) e.getValue();
                        break;
                    case "vote_average":
                        movie.vote_average = (Double) e.getValue();
                        break;
                    case "vote_count":
                        movie.vote_count = ((Double) e.getValue()).intValue();
                        break;
                }
            movies.add(movie);
        }
        return new ArrayList<>(movies);
    }

    public static List<MovieData> covertMapToMovieDataList(Map<String, Serializable> map) {
        List<MovieData> movies = null;
        Double page, total_pages, total_results;
        for (Map.Entry<String, Serializable> entry : map.entrySet())
            switch (entry.getKey()) {
                case "page":
                    page = (Double) entry.getValue();
                    break;
                case "results":
                    movies = convertJsonMapToMovieList((ArrayList) entry.getValue());
                    break;
                case "total_pages":
                    total_pages = (Double) entry.getValue();
                    break;
                case "total_results":
                    total_results = (Double) entry.getValue();
                    break;
                default:
                    Log.d(LOG_TAG, "Key/Val did not match predefined set: " + entry.getKey());
            }
        return movies;
    }

    public static  Uri determineUri(String sortBy) {
        if (sortBy.equals("vote_average.desc"))
            return MovieContract.RatingEntry.buildUri();
        else if (sortBy.equals("favourite"))
            return MovieContract.MovieEntry.buildUriUnionFavorite();
        else if (sortBy.equals("popularity.desc"))
            return MovieContract.PopularEntry.buildUri();
        else
            throw new UnsupportedOperationException("Sort not identified: " + sortBy);
    }
}

