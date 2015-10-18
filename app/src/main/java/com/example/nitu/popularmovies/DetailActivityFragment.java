package com.example.nitu.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nitu.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;

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

    public DetailActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
       /* Intent intent = getActivity().getIntent();
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(intent.EXTRA_TEXT))
        {
            MovieData movie = (MovieData)intent.getParcelableExtra(intent.EXTRA_TEXT);
            String mMovieTitle = movie.getTitle();
            ((TextView)rootView.findViewById(R.id.title_text)).setText(mMovieTitle);
            String url =movie.getPoster_path();
            if (Patterns.WEB_URL.matcher(url).matches())
                Picasso.with(getActivity()).load(url).into((ImageView) rootView.findViewById(R.id.imageView));
            String mMovieVoteAverage = movie.getVote_average();
            ((TextView)rootView.findViewById(R.id.voteaverage_text))
                    .setText(mMovieVoteAverage +"/10");
            Float f= Float.parseFloat(mMovieVoteAverage);
            ((RatingBar)rootView.findViewById(R.id.ratingBar)).setRating(f);
            String mMovieReleaseDate = movie.getRelease_date();
            ((TextView)rootView.findViewById(R.id.release_text))
                    .setText(mMovieReleaseDate);
            String mMovieOverview = movie.getOverview();
            ((TextView)rootView.findViewById(R.id.overview_text))
                    .setText(mMovieOverview);
        }
        return rootView;*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        ((TextView)getView().findViewById(R.id.title_text)).setText(data.getString(COL_MOVIE_ORIGINAL_TITLE));

        ImageView imageView = (ImageView) getView().findViewById(R.id.imageView);
        byte[] bb= Utility.getImage(data);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));

        String mMovieVoteAverage = data.getString(COL_MOVIE_VOTE_AVERAGE);

        ((TextView)getView().findViewById(R.id.voteaverage_text))
                .setText(mMovieVoteAverage + "/10");
        Float f= Float.parseFloat(mMovieVoteAverage);
        ((RatingBar)getView().findViewById(R.id.ratingBar)).setRating(f);

        ((TextView)getView().findViewById(R.id.release_text))
                .setText(Utility.formatDate(data.getLong(COL_MOVIE_RELEASE_DATE)));

        ((TextView)getView().findViewById(R.id.overview_text))
                .setText(data.getString(COL_MOVIE_OVERVIEW));

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        //if (mShareActionProvider != null) {
        //    mShareActionProvider.setShareIntent(createShareForecastIntent());
        //}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
