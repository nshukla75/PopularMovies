package com.example.nitu.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }
    private boolean DEBUG = true;

    public static byte[] urlToImageBLOB(String url) throws IOException {

        HttpEntity entity = null;
        DefaultHttpClient mHttpClient = new DefaultHttpClient();
        HttpGet mHttpGet = new HttpGet(url);
        HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
        if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            entity = mHttpResponse.getEntity();
        }
        return EntityUtils.toByteArray(entity);
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_RESULTS = "results";

        final String OWM_ID = "id";
        final String OWM_ORIGINAL_TITLE = "original_title";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_POPULARITY = "popularity";
        final String OWM_VOTE_AVERAGE = "vote_average";

        try {
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String movieId = movie.getString("id");
                String movieTitle = movie.getString("original_title");
                String moviePosterPath = "http://image.tmdb.org/t/p/w185" + movie.getString("poster_path");
                String movieOverview = movie.getString("overview");
                String movieVoteAverage = movie.getString("vote_average");
                String movieReleaseDate = movie.getString("release_date").substring(0, 4);
                String movieVoteCount=movie.getString("vote_count");
                String moviePopularity=movie.getString("popularity");
                byte[] movieImage = null;
                try {
                    movieImage = urlToImageBLOB(moviePosterPath);
                }
                catch (java.io.IOException e){
                    movieImage = null;
                }
                Log.e("trying to put image--",moviePosterPath + i);
                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, moviePopularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieVoteAverage);
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieTitle);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieOverview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movieImage);
                cVVector.add(movieValues);
            }
            Log.e("Moive JSON","Data into Content Values...............");
            int inserted = 0;
            // add to database
            if(cVVector.size()>0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,cvArray);
            }

            Log.e(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

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
        String apiKeyStr = "7537b743615a000671a98c32d354df39";
        try {
            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM = "sort_by";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(APIKEY_PARAM, apiKeyStr)
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
            getMovieDataFromJson(movieJsonStr);
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
