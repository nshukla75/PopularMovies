package com.example.nitu.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter mMovieAdapter;

    public MainActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMovieAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMovieAdapter = new MovieAdapter(getActivity());
            mMovieAdapter.onRestoreInstanceState(savedInstanceState);
        }
        else {
            updateMovie();
            mMovieAdapter = new MovieAdapter(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView listView = (GridView) rootView.findViewById(R.id.gridview_movie);
        listView.setAdapter(mMovieAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieData movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString("sort_order","popularity.desc");
        Toast.makeText(getActivity(), "Loading Please Wait..", Toast.LENGTH_LONG).show();
        if (NetworkUtils.getInstance(getContext()).isOnline())
            movieTask.execute(sortBy);
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Network is not available");
            builder.setPositiveButton(R.string.action_exit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
            builder.create();
            builder.show();
        }
    }

    public class FetchMovieTask extends AsyncTask<String,Void,List<MovieData>>
    {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected List<MovieData> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            // Please Enter the key below
            //String apiKeyStr ="[YOUR API KEY]";
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

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
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
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException j) {
                Log.e(LOG_TAG, "JSON Error", j);
            }
            return null;
        }

        private List<MovieData> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {
            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieArray = movieJson.getJSONArray("results");
                List<MovieData> movies = new ArrayList<MovieData>();
                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    String movieId = movie.getString("id");
                    String movieTitle = movie.getString("original_title");
                    String moviePosterPath = "http://image.tmdb.org/t/p/w185" + movie.getString("poster_path");
                    String movieOverview = movie.getString("overview");
                    String movieVoteAverage = movie.getString("vote_average");
                    String movieReleaseDate = movie.getString("release_date").substring(0,4);
                    String movieVoteCount=movie.getString("vote_count");

                    MovieData movieData = new MovieData();
                    movieData.setMovie_id(movieId);
                    movieData.setTitle(movieTitle);
                    movieData.setPoster_path(moviePosterPath);
                    movieData.setOverview(movieOverview);
                    movieData.setVote_average(movieVoteAverage);
                    movieData.setRelease_date(movieReleaseDate);
                    movieData.setVote_count(movieVoteCount);
                    movies.add(movieData);
                }
                return movies;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Json Parsing code end

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieData> result) {
            if (result != null){
                mMovieAdapter.replace(result);
            }
        }
    }

}
