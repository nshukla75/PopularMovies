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
 * Created by nitus on 10/17/2015.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private final Context mContext;

    public FetchTrailerTask(Context context) {
        mContext = context;
    }
    private boolean DEBUG = true;

    private void getTrailerDataFromJson(String trailerJsonStr,String movieStr)
            throws JSONException {
        final String OWM_RESULTS = "results";
        try {
            JSONObject forecastJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = forecastJson.getJSONArray(OWM_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject movie = trailerArray.getJSONObject(i);
                String trailerKey = movie.getString("key");
                String site=movie.getString("site");
                if ((trailerKey !=null)&&(site.equals("YouTube"))){
                    String trailerId = movie.getString("id");
                    String trailerSize = movie.getString("size");
                    ContentValues trailerValues = new ContentValues();
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOV_KEY, movieStr);
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailerId);
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, trailerKey);
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, trailerSize);
                    cVVector.add(trailerValues);
                }
            }
            Log.e("Trailer JSON","Data into Content Values...............");
            int inserted = 0;
            // add to database
            if(cVVector.size()>0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,cvArray);
            }
            Log.e(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");

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
        //long movieRowId= Long.parseLong(params[1]);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.


        // Will contain the raw JSON response as a string.
        String trailerJsonStr = null;

        String format = "json";
        try {
            //trailer: http://api.themoviedb.org/3/movie/135397/videos?&api_key=7537b743615a000671a98c32d354df39
            final String TRAILER_BASE_URL = AppConstants.MOVIE_REVIEWS_TRAILER_BASE_URL +"/" + movieStr+"/videos?";
            Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                    .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());
            trailerJsonStr = getJsonfromURL(url);
            Log.e("Do In Background", "GOT Trailer JSON Here .........");
            getTrailerDataFromJson(trailerJsonStr, movieStr);

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
