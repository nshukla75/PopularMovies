package com.example.nitu.popularmovies.fetchtasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by nitus on 10/20/2015.
 */
public class FetchReviewTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    private final Context mContext;

    public FetchReviewTask(Context context) {
        mContext = context;
    }
    private boolean DEBUG = true;

    private void getReviewDataFromJson(String reviewJsonStr, String movieStr)
            throws JSONException {
        final String OWM_RESULTS = "results";
        try {
            JSONObject forecastJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = forecastJson.getJSONArray(OWM_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject movie = reviewArray.getJSONObject(i);
                String reviewId = movie.getString("id");
                String reviewAuthor = movie.getString("author");
                String reviewContent = movie.getString("content");

                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOV_KEY, movieStr);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_KEY, reviewId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reviewAuthor);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reviewContent);
                cVVector.add(reviewValues);
            }
            Log.e("Review JSON","Data into Content Values...............");
            int inserted = 0;
            // add to database
            if(cVVector.size()>0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, movieStr +"FetchReviewTask Complete "+ inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private String getJsonfromURL(URL url)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Log.v(LOG_TAG,"Getting Data from :   " + url);
        try {
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
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }
        catch (java.io.IOException e){
            Log.e(LOG_TAG, "Error ", e);
        }finally {
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
        return null;
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String movieStr = params[0];

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        String format = "json";
        try {
            //reviews: http://api.themoviedb.org/3/movie/135397/reviews?&api_key=7537b743615a000671a98c32d354df39
            final String REVIEW_BASE_URL = AppConstants.MOVIE_REVIEWS_TRAILER_BASE_URL +"/" + movieStr+"/reviews?";
            Uri builtUri = Uri.parse(REVIEW_BASE_URL).buildUpon()
                    .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());
            reviewJsonStr = getJsonfromURL(url);
            Log.e(LOG_TAG,"Do In Background GOT Review JSON Here .........");
            getReviewDataFromJson(reviewJsonStr,movieStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
}

