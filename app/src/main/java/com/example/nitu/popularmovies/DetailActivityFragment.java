package com.example.nitu.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.data.MovieProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    public interface MovieQuery {
        static final int DETAIL_LOADER = 0;
        static final String[] MOVIE_COLUMNS = {
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
        static final int COL_MOVIEID = 0;
        static final int COL_MOVIE_KEY = 1;
        static final int COL_MOVIE_POPULARITY = 2;
        static final int COL_MOVIE_VOTE_AVERAGE = 3;
        static final int COL_MOVIE_FAVOURITE = 4;
        static final int COL_MOVIE_ORIGINAL_TITLE = 5;
        static final int COL_MOVIE_OVERVIEW = 6;
        static final int COL_MOVIE_RELEASE_DATE = 7;
        static final int COL_MOVIE_POSTER = 8;
    }
    private String movieStr;
    public ToggleButton btnToggle;
    public DetailActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        if (savedInstanceState != null) {
            getLoaderManager().restartLoader(MovieQuery.DETAIL_LOADER, null, this);
         }
        else {
            getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, null, this);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Create View", "in Create View...............");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        btnToggle = (ToggleButton)rootView.findViewById(R.id.chkState);

        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getActivity(), "toggle button checked is '"+ isChecked, Toast.LENGTH_SHORT).show();
                    updateFavourite(1,movieStr);
                }else{
                    Toast.makeText(getActivity(), "toggle button checked is  '"+ isChecked, Toast.LENGTH_SHORT).show();
                    updateFavourite(0, movieStr);
                }
            }
        });

        return rootView;
    }
    public void updateFavourite(int chkFavourite, String movieKey){
        ContentValues updateValues = new ContentValues();
        updateValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, chkFavourite);
        getContext().getContentResolver().update(getActivity().getIntent().getData(), updateValues, MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "=" + movieKey, null);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MovieQuery.MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        data.setNotificationUri(getContext().getContentResolver(), getActivity().getIntent().getData());
        if (null == mMovieAdapter)
            mMovieAdapter = new MovieAdapter(getActivity(),null,0);
        if (mMovieAdapter.getCursor() != data)
            mMovieAdapter.swapCursor(data);
        if (!data.moveToFirst()) {
            return;
        }
        movieStr = data.getString(MovieQuery.COL_MOVIE_KEY);
        ((TextView) getView().findViewById(R.id.title_text)).setText(data.getString(MovieQuery.COL_MOVIE_ORIGINAL_TITLE));

        ImageView imageView = (ImageView) getView().findViewById(R.id.imageView);
        byte[] bb = Utility.getImage(data);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));

        String mMovieVoteAverage = data.getString(MovieQuery.COL_MOVIE_VOTE_AVERAGE);

        ((TextView) getView().findViewById(R.id.voteaverage_text))
                .setText(mMovieVoteAverage + "/10");
        Float f = Float.parseFloat(mMovieVoteAverage);
        ((RatingBar) getView().findViewById(R.id.ratingBar)).setRating(f);

        ((TextView) getView().findViewById(R.id.release_text))
                .setText(data.getString(MovieQuery.COL_MOVIE_RELEASE_DATE));

        btnToggle = (ToggleButton)getView().findViewById(R.id.chkState);
        if (data.getInt(MovieQuery.COL_MOVIE_FAVOURITE)!= 0)
            btnToggle.setChecked(true);
        else
            btnToggle.setChecked(false);

        ((TextView) getView().findViewById(R.id.overview_text))
                .setText(data.getString(MovieQuery.COL_MOVIE_OVERVIEW));
       Log.e(LOG_TAG, "going to start Review Fragment");
        if (movieStr != null) {
            FragmentManager childFragMan = getChildFragmentManager();
            FragmentTransaction childFragTrans = childFragMan.beginTransaction();
            ReviewFragment mReviewFragment = ReviewFragment.newInstance(movieStr);
            if (childFragMan.findFragmentById(R.id.review_parent)==null) {
                childFragTrans.add(R.id.review_parent, mReviewFragment);
                childFragTrans.addToBackStack("Review");
                childFragTrans.commit();
            }

            FragmentManager trailerFragMan = getChildFragmentManager();
            FragmentTransaction trailerFragTrans = trailerFragMan.beginTransaction();
            TrailerFragment mTrailerFragment = TrailerFragment.newInstance(movieStr);
            if (trailerFragMan.findFragmentById(R.id.trailer_parent)==null) {
                trailerFragTrans.add(R.id.trailer_parent, mTrailerFragment);
                trailerFragTrans.addToBackStack("Trailer");
                trailerFragTrans.commit();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "In onLoadReset");
    }
}
