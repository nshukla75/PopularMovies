package com.example.nitu.popularmovies.fetchtasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nitu.popularmovies.BuildConfig;
import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.model.MovieData;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by nitus on 10/17/2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public void getMovieDataFromJson(String movieJsonStr,String sort)
            throws JSONException {
        List<MovieData> mMovieList = new ArrayList<>();
        Map<String, Serializable> map = Utility.getGson().fromJson(movieJsonStr, LinkedTreeMap.class);
        mMovieList = Utility.covertMapToMovieDataList(map);
        ContentValues[] movie_ids = new ContentValues[mMovieList.size()];
        ContentValues[] cvs = new ContentValues[mMovieList.size()];
        int i = 0;

        for (MovieData obj : mMovieList) {
            long movie_id = obj.id;
            byte[] blob = SerializationUtils.serialize(obj);
            ContentValues movieCv = new ContentValues();
            ContentValues idCv = new ContentValues();
            movieCv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
            movieCv.put(MovieContract.MovieEntry.COLUMN_MOVIE_BLOB, blob);
            idCv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
            cvs[i] = movieCv;
            movie_ids[i++] = idCv;
        }
        int inserted = 0;
        // add Movie to database
        if(cvs.length>0) {
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.buildUri(), cvs);
            Log.v(LOG_TAG, "Insert Movie Complete. " + inserted + " Inserted");
        }
        if (("vote_average.desc").equals(sort) ||
                ("popularity.desc").equals(sort)) {
            Uri uri = Utility.determineUri(sort);
            mContext.getContentResolver().delete(uri, null, null);
            mContext.getContentResolver().bulkInsert(uri, movie_ids);
        }
            Log.d(LOG_TAG, String.format("Just inserted movies %s", Arrays.toString(cvs)));

    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String sort = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String format = "json";
        try {
            Uri builtUri = Uri.parse(AppConstants.BASE_URL).buildUpon()
                    .appendQueryParameter(AppConstants.SORT_BY, sort)
                    .appendQueryParameter(AppConstants.API_KEY, BuildConfig.MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG,"Getting Data from :   " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            Log.v("Do In Background","GOT JSON Here .........");
            getMovieDataFromJson(movieJsonStr,sort);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
}
