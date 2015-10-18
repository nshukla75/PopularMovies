package com.example.nitu.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import android.widget.Toast;

import com.example.nitu.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final int MOVIE_LOADER=0;
    private MovieAdapter mMovieAdapter;
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
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
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POPULARITY = 2;
    static final int COL_MOVIE_VOTE_AVERAGE = 3;
    static final int COL_MOVIE_FAVOURITE = 4;
    static final int COL_MOVIE_ORIGINAL_TITLE = 5;
    static final int COL_MOVIE_OVERVIEW = 6;
    static final int COL_MOVIE_RELEASE_DATE = 7;
    static final int COL_MOVIE_POSTER = 8;

    public MainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView listView = (GridView) rootView.findViewById(R.id.gridview_movie);
        listView.setAdapter(mMovieAdapter);
        Log.e("Create View", "in Create View...............");
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMovie(cursor.getString(COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onPreferenceChanged( ) {
        updateMovie();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
       /* SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString("sort_order","popularity.desc");*/
        String sortBy=Utility.getPreferences(getActivity());
        Toast.makeText(getActivity(), "Getting data for" + sortBy, Toast.LENGTH_LONG).show();
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
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);

    }
}
