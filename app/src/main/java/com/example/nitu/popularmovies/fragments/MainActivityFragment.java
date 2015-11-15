package com.example.nitu.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import android.widget.ListView;
import android.widget.Toast;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.NetworkUtils;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.GridViewAdapter;
import com.example.nitu.popularmovies.application.PopMovieApp;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.fetchtasks.FetchMovieTask;
import com.example.nitu.popularmovies.model.MovieData;


import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static volatile PopMovieApp.State appState;
    final int MOVIE_LOADER=0;
    private GridViewAdapter mMovieAdapter;
    private String msortBy;
    private MainActivityFragment mThis;
    private int mCurCheckPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MINUTE
    };

    static final int COL_MOVIEID = 0;
    static final int COL_MOVIE_KEY = 1;//135397
    static final int COL_MOVIE_POPULARITY = 2;
    static final int COL_MOVIE_VOTE_AVERAGE = 3;
    static final int COL_MOVIE_FAVOURITE = 4;
    static final int COL_MOVIE_ORIGINAL_TITLE = 5;
    static final int COL_MOVIE_OVERVIEW = 6;
    static final int COL_MOVIE_RELEASE_DATE = 7;
    static final int COL_MOVIE_POSTER_PATH = 8;
    static final int COL_MOVIE_MINUTE = 9;
    GridView listView;
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieData movieData);
    }
    public MainActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurCheckPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mCurCheckPosition);
        }
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        msortBy= Utility.getPreferences(getActivity());
        mCurCheckPosition=0;
        Bundle b = new Bundle();
        b.putString("sort", msortBy);
        appState = ((PopMovieApp) getActivity().getApplication()).STATE;
        super.onCreate(savedInstanceState);
        mMovieAdapter=new GridViewAdapter(getActivity(),null,0);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt(SELECTED_KEY,0);
            appState.setIsRefreshGrid(false);
            getLoaderManager().restartLoader(MOVIE_LOADER, b, this);
            //if (appState.getTwoPane()) performListViewClick(mCurCheckPosition);
        }
        else {
           /* updateMovie();
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);*/
        }
        setHasOptionsMenu(true);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        updateMovie();
        appState.setIsRefreshGrid(false);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferences(getActivity());
        Bundle b = new Bundle();
        b.putString("sort", sortBy);
        if (sortBy != null && !sortBy.equals(msortBy)) {
            updateMovie();
            mCurCheckPosition = 0;
            appState.setIsRefreshGrid(false);
            getLoaderManager().restartLoader(MOVIE_LOADER, b, this);
        }
        msortBy = sortBy;
        super.onResume();
    }

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
    }*/
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.fragment_main, container, false);
       listView = (GridView) rootView.findViewById(R.id.gridview_movie);
       if (mMovieAdapter.getCount()>0)
           listView.setAdapter(mMovieAdapter);
       //else Toast.makeText(getActivity(), "No Movies for " + Utility.getPreferences(getActivity()), Toast.LENGTH_LONG).show();
       createGridItemClickCallbacks();
       //mThis = this;
       if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
           mCurCheckPosition = savedInstanceState.getInt(SELECTED_KEY);
       }

       return rootView;
   }

    private void createGridItemClickCallbacks() {
        //Grid view click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCurCheckPosition = position;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    //updateMovieMinute(cursor.getString(COL_MOVIE_KEY));

                    MovieData movieObj = new MovieData();
                    movieObj.id = cursor.getLong(COL_MOVIE_KEY);
                    movieObj.original_title = cursor.getString(COL_MOVIE_ORIGINAL_TITLE);
                    movieObj.overview = cursor.getString(COL_MOVIE_OVERVIEW);
                    movieObj.popularity = cursor.getDouble(COL_MOVIE_POPULARITY);
                    movieObj.vote_average = cursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
                    movieObj.release_date = cursor.getString(COL_MOVIE_RELEASE_DATE);
                    movieObj.poster_path = cursor.getString(COL_MOVIE_POSTER_PATH);
                    movieObj.favourite = cursor.getInt(COL_MOVIE_FAVOURITE);
                    movieObj.minutes = cursor.getInt(COL_MOVIE_MINUTE);

                    Bundle bundle = new Bundle();
                    bundle.putLong("movie_id_key", cursor.getLong(COL_MOVIE_KEY));

                    ((Callback) getActivity()).onItemSelected(movieObj);
                } else
                    Toast.makeText(getActivity(), "No Movie Selected!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String sortBy=Utility.getPreferences(getActivity());
        Toast.makeText(getActivity(), "Getting data for" + sortBy, Toast.LENGTH_LONG).show();

        if (NetworkUtils.getInstance(getContext()).isOnline())
            movieTask.execute(sortBy);
       /* else {
            Toast.makeText(getActivity(), "No Network connection ", Toast.LENGTH_LONG).show();
        }*/

    }

    private void getLiveDataAndCallLoader() {
        String sortBy = Utility.getPreferences(getActivity());
        if (!(sortBy.equals("favourite"))){
            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
            BufferedReader reader = null;
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.popularmovie);
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        String movieJsonStr = buffer.toString();
                        movieTask.getMovieDataFromJson(movieJsonStr);
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String sortBy = Utility.getPreferences(getActivity());
        Uri movieUri;
        if (sortBy.equals("vote_average.desc"))
            movieUri = MovieContract.MovieEntry.buildTopratedMovie();
        else if (sortBy.equals("favourite"))
            movieUri=MovieContract.MovieEntry.buildFavouriteMovie();
        else
            movieUri = MovieContract.MovieEntry.buildPopularMovie();
        Toast.makeText(getActivity(), "Loading data for" + sortBy, Toast.LENGTH_LONG).show();
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.setNotificationUri(getContext().getContentResolver(), MovieContract.MovieEntry.CONTENT_URI);
        String sortBy = Utility.getPreferences(getActivity());
        if (null == mMovieAdapter)
            mMovieAdapter = new GridViewAdapter(getActivity(), null, 0);
        //gv is a GridView
        if (listView.getAdapter() != mMovieAdapter)
            listView.setAdapter(mMovieAdapter);
        if (mMovieAdapter.getCursor() != cursor)
            mMovieAdapter.swapCursor(cursor);
        if (!cursor.moveToFirst()) {
            if (sortBy.equals("favourite"))
                Toast.makeText(getActivity(), "No Movie in your Favourite selection", Toast.LENGTH_LONG).show();
            else
                getLiveDataAndCallLoader();
        } else {
            if (mCurCheckPosition != GridView.INVALID_POSITION)
                listView.smoothScrollToPosition(mCurCheckPosition);

            if ((appState.getTwoPane())&& (mCurCheckPosition==0)&&(!appState.getIsRefreshGrid())){
                final int WHAT = 1;
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == WHAT)
                            listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, listView.getItemIdAtPosition(0));
                    }
                };
                handler.sendEmptyMessage(WHAT);
                appState.setIsRefreshGrid(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }


}
