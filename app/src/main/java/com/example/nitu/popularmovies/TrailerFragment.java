package com.example.nitu.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nitu.popularmovies.adaptors.ReviewAdapter;
import com.example.nitu.popularmovies.adaptors.TrailerAdapter;
import com.example.nitu.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = TrailerFragment.class.getSimpleName();
    private TrailerAdapter mTrailerAdapter;
    private ListView listViewTrailer;
    public interface TrailerQuery {
        static final int TRAILER_LOADER = 1;
        static final String[] TRAILER_COLUMNS = {
                MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
                MovieContract.TrailerEntry.COLUMN_MOV_KEY,
                MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
                MovieContract.TrailerEntry.COLUMN_KEY,
                MovieContract.TrailerEntry.COLUMN_SIZE
        };
        static final int COL_TRAILERID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_TRAILER_ID = 2;
        static final int COL_TRAILER_KEY = 3;
        static final int COL_TRAILER_SIZE=4;
    }
    private String movieStr;

    public TrailerFragment() {
        Log.e(LOG_TAG, "In new Trailer " + movieStr);
    }

    public static TrailerFragment newInstance(String movieStr) {
        TrailerFragment frag = new TrailerFragment();
        Bundle args=new Bundle();
        args.putString("movieStr", movieStr);
        frag.setArguments(args);
        Log.e(LOG_TAG, "In new instance Review " + movieStr);
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "In create trailer" + movieStr);
        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        if (savedInstanceState != null) {
            Log.e(LOG_TAG,"In create restart loader");
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, null, this);
        } else {
            Log.e(LOG_TAG,"In create init loader");
            getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, null, this);
        }
        Log.e(LOG_TAG,"out create trailer");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_TAG,"going to load view" + movieStr);
        View rootView = inflater.inflate(R.layout.trailer_movie, container, false);
        listViewTrailer = (ListView) rootView.findViewById(R.id.listView_trailer);
        if (mTrailerAdapter.getCount() > 0) listViewTrailer.setAdapter(mTrailerAdapter);
        return rootView;
    }

    /*@Override
    public void onResume() {
        Log.e(LOG_TAG,"In Resume Review");
        super.onResume();
        if ((movieId != 0) && (movieKey !=null)) {
            updateReview();
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
        }
        Log.e(LOG_TAG,"out resume Review");
    }*/


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        Log.v(LOG_TAG, "In onCreate trailer Loader");
        if (getArguments()!=null) {
            movieStr = (getArguments().getString("movieStr"));
        }
        Uri reviewUri = MovieContract.TrailerEntry.buildTrailerMovie(movieStr);
        return new CursorLoader(
                getActivity(),
                reviewUri,
                TrailerQuery.TRAILER_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG,"In trailer load finish loader");
        cursor.setNotificationUri(getContext().getContentResolver(), MovieContract.TrailerEntry.buildTrailerMovie(movieStr));

        if (null == mTrailerAdapter)
            mTrailerAdapter = new TrailerAdapter(getActivity(),null,0);
        //listViewTrailer = (ListView) getActivity().findViewById(R.id.listView_trailer);
        if (listViewTrailer.getAdapter() != mTrailerAdapter)
            listViewTrailer.setAdapter(mTrailerAdapter);
        if (mTrailerAdapter.getCursor() != cursor)
            mTrailerAdapter.swapCursor(cursor);
        Log.e(LOG_TAG,"out trailer load finish loader"+ cursor.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG, "In loader reset  trailer");
        //mTrailerAdapter.swapCursor(null);

    }
}
