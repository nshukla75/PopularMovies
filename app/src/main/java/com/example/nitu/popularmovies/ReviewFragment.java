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
import com.example.nitu.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();
    private ReviewAdapter mReviewAdapter;
    private ListView listViewReview;
    public interface ReviewQuery {
        static final int REVIEW_LOADER = 2;
        static final String[] REVIEW_COLUMNS = {
                MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
                MovieContract.ReviewEntry.COLUMN_MOV_KEY,
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
        private String movieStr;

    public ReviewFragment() {
        Log.e(LOG_TAG, "In new Review " + movieStr);
    }

    public static ReviewFragment newInstance(String movieStr) {
        ReviewFragment frag = new ReviewFragment();
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
        Log.e(LOG_TAG, "In create Review" + movieStr);
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
        Log.e(LOG_TAG,"going to load view" + movieStr);
        View rootView = inflater.inflate(R.layout.review_movie, container, false);
        listViewReview = (ListView) rootView.findViewById(R.id.listView_movie);
        if (mReviewAdapter.getCount() > 0) listViewReview.setAdapter(mReviewAdapter);
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
            movieStr = (getArguments().getString("movieStr"));
        }
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewMovie(movieStr);
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
        cursor.setNotificationUri(getContext().getContentResolver(), MovieContract.ReviewEntry.buildReviewMovie(movieStr));

        if (null == mReviewAdapter)
            mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
        //listViewReview = (ListView) getActivity().findViewById(R.id.listView_movie);
        if (mReviewAdapter.getCursor() != cursor)
            mReviewAdapter.swapCursor(cursor);
        if (listViewReview.getAdapter() != mReviewAdapter)
            listViewReview.setAdapter(mReviewAdapter);
        if (mReviewAdapter.getCursor() != cursor)
            mReviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG, "In loader reset  Review");
        //mReviewAdapter.swapCursor(null);

    }
}
