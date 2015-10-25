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


import com.example.nitu.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();
    private ReviewAdapter mReviewAdapter;
    public interface ReviewQuery {
        static final int REVIEW_LOADER = 2;
        static final Uri CONTENT_URI= MovieContract.ReviewEntry.CONTENT_URI;
        static final String SELECTION=null;
        static final String SORT_ORDER = MovieContract.ReviewEntry._ID;
        static final String[] REVIEW_COLUMNS = {
                MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
                MovieContract.ReviewEntry.COLUMN_MOV_ID,
                MovieContract.ReviewEntry.COLUMN_REVIEW_KEY,
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                MovieContract.ReviewEntry.COLUMN_CONTENT
        };
        static final int COL_REVIEWID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_REVIEW_KEY = 2;
        static final int COL_REVIEW_AUTHOR = 3;
        static final int COL_REVIEW_CONTENT=4;
    }
        private long movieId =0;

    public ReviewFragment() {
        Log.e(LOG_TAG, "In new Review " + Long.toString(movieId));
    }

    public static ReviewFragment newInstance(long movieId) {
        ReviewFragment frag = new ReviewFragment();
        Bundle args=new Bundle();
        args.putLong("movieId", movieId);
        frag.setArguments(args);
        Log.e(LOG_TAG, "In new instance Review " + Long.toString(movieId));
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "In create Review" + Long.toString(movieId));
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        if (savedInstanceState != null) {
            Log.e(LOG_TAG,"In create restart loader");
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
        } else {
            Log.e(LOG_TAG,"In create init loader");
            getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, null, this);
        }
        Log.e(LOG_TAG,"out create Review");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_TAG,"going to load view" + Long.toString(movieId));
        View rootView = inflater.inflate(R.layout.review_movie, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_movie);
        if (mReviewAdapter.getCount() > 0) listView.setAdapter(mReviewAdapter);
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
        Log.v(LOG_TAG, "In onCreate Review Loader");
        if (getArguments()!=null) {
            movieId = (getArguments().getLong("movieId"));
        }
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewMovie(Long.toString(movieId));
        return new CursorLoader(
                getActivity(),
                reviewUri,
                ReviewQuery.REVIEW_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG,"In load finish loader Review");
        cursor.setNotificationUri(getContext().getContentResolver(), MovieContract.ReviewEntry.buildReviewMovie(Long.toString(movieId)));

        if (null == mReviewAdapter)
            mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
        ListView listView = (ListView) getActivity().findViewById(R.id.listView_movie);
        if (listView.getAdapter() != mReviewAdapter)
            listView.setAdapter(mReviewAdapter);
        if (mReviewAdapter.getCursor() != cursor)
            mReviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG, "In loader reset  Review");
        //mReviewAdapter.swapCursor(null);

    }
}
