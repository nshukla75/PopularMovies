package com.example.nitu.popularmovies.fetchtasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.data.MovieContract;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
 * Created by nitus on 10/17/2015.
 */
public class FetchMinuteTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMinuteTask.class.getSimpleName();
    private final Context mContext;

    public FetchMinuteTask(Context context) {
        mContext = context;
    }
    private boolean DEBUG = true;

    private void getMinuteDataFromJson(String movieJsonStr)
            throws JSONException {

        final String OWM_ID = "id";
        final String OWM_RUNTIME = "runtime";

        try {
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            if (forecastJson != null) {
                String movieId = forecastJson.getString("id");
                String movieRuntime = forecastJson.getString("runtime");

                ContentValues updateValues = new ContentValues();
                updateValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movieId);
                updateValues.put(MovieContract.MovieEntry.COLUMN_MINUTE, movieRuntime);

            // add to database
            int count = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "= ?", new String[] { movieId});
            Log.e(LOG_TAG, "FetchMinuteTask Complete. " + count + " Updated");
            }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String movieQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String format = "json";
        try {
            final String BASE_MINUTE_MOVIE_URL = AppConstants.BASE_MINUTE_URL + movieQuery + "?";
            Uri builtUri = Uri.parse(BASE_MINUTE_MOVIE_URL).buildUpon()
                    .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
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
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            Log.e("Do In Background","GOT JSON Here .........");
            getMinuteDataFromJson(movieJsonStr);
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
