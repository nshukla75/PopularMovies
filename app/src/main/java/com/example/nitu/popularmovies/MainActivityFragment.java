package com.example.nitu.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import android.widget.Toast;

import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.fetchtasks.FetchMovieTask;
import com.example.nitu.popularmovies.fetchtasks.FetchReviewTask;
import com.example.nitu.popularmovies.fetchtasks.FetchTrailerTask;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    final int MOVIE_LOADER=0;
    private MovieAdapter mMovieAdapter;
    private String msortBy;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIEID = 0;
    static final int COL_MOVIE_KEY = 1;//135397
    static final int COL_MOVIE_POPULARITY = 2;
    static final int COL_MOVIE_VOTE_AVERAGE = 3;
    static final int COL_MOVIE_FAVOURITE = 4;
    static final int COL_MOVIE_ORIGINAL_TITLE = 5;
    static final int COL_MOVIE_OVERVIEW = 6;
    static final int COL_MOVIE_RELEASE_DATE = 7;
    static final int COL_MOVIE_POSTER = 8;

    GridView listView;
    public MainActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mMovieAdapter.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        msortBy= Utility.getPreferences(getActivity());
        super.onCreate(savedInstanceState);
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        if (savedInstanceState != null) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
        else {
            updateMovie();
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (GridView) rootView.findViewById(R.id.gridview_movie);
        if (mMovieAdapter.getCount()>0) listView.setAdapter(mMovieAdapter);
        Log.e("Create View", "in Create View...............");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    updateReview(cursor.getString(COL_MOVIE_KEY));
                    updateTrailer(cursor.getString(COL_MOVIE_KEY));
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMovie(cursor.getString(COL_MOVIE_KEY)));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    private void updateReview(String movieKey){
        Log.e(LOG_TAG,"In update Review");
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity());
        if (NetworkUtils.getInstance(getContext()).isOnline()) {
            Log.e("In update Review", "getting data for Review ");
            Log.e(LOG_TAG,"going to fetch review data for "+ movieKey);
            fetchReviewTask.execute(movieKey);
        } else {
            Toast.makeText(getActivity(),"Network is not Available",Toast.LENGTH_LONG).show();
        }
        Log.e(LOG_TAG, "OUT update Review");
    }

    private void updateTrailer(String movieKey){
        Log.e(LOG_TAG,"In update Trailer");
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity());
        if (NetworkUtils.getInstance(getContext()).isOnline()) {
            Log.e("In update Trailer", "getting data for Trailer ");
            Log.e(LOG_TAG,"going to fetch trailer data for "+ movieKey);
            fetchTrailerTask.execute(movieKey);
        } else {
            Toast.makeText(getActivity(), "Network is not Available", Toast.LENGTH_LONG).show();
        }
        Log.e(LOG_TAG, "OUT update Trailer");
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferences(getActivity());
        if (sortBy != null && !sortBy.equals(msortBy)) {
            updateMovie();
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
        msortBy = sortBy;
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String sortBy=Utility.getPreferences(getActivity());
        Toast.makeText(getActivity(), "Getting data for" + sortBy, Toast.LENGTH_LONG).show();
        if (NetworkUtils.getInstance(getContext()).isOnline())
            movieTask.execute(sortBy);
        else {
            Toast.makeText(getActivity(), "No Network connection" + sortBy, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String sortBy = Utility.getPreferences(getActivity());
        Uri movieUri;
        if (sortBy.equals("vote_average.desc"))
            movieUri = MovieContract.MovieEntry.buildTopratedMovie();
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
        if (null == mMovieAdapter)
            mMovieAdapter = new MovieAdapter(getActivity(),null,0);
        //gv is a GridView
        //listView = (GridView) getActivity().findViewById(R.id.gridview_movie);
        if (listView.getAdapter() != mMovieAdapter)
            listView.setAdapter(mMovieAdapter);
        if (mMovieAdapter.getCursor() != cursor)
            mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //mMovieAdapter.swapCursor(null);

    }
}
