package com.example.nitu.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nitu.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
 /*   private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;*/

    private String mMovie;

    public DetailActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            getLoaderManager().restartLoader(MovieQuery.DETAIL_LOADER, null, this);
           /* getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, null, this);*/
         }
        else {
            getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, null, this);
            /*mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
            mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
            getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, null, this);
            getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, null, this);*/
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_detail, container, false);
        Log.e("Create View", "in Create View...............");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        /*ListView reviewlistView=(ListView)rootView.findViewById(R.id.Listview_movieReview);
        reviewlistView.setAdapter(mReviewAdapter);
        Log.e("Create Review View", "in Create View...............");

        ListView trailerlistView = (ListView) rootView.findViewById(R.id.Listview_movieTrailer);
        trailerlistView.setAdapter(mTrailerAdapter);
        Log.e("Create Trailer View", "in Create View...............");*/
        return rootView;
    }

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, null, this);
        if((mReviewAdapter.getCount()==0)) {

            FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity());
            if (NetworkUtils.getInstance(getContext()).isOnline()) {
                Log.e("onActivityCreated", "getting data for Trailer and Review ");
                fetchReviewTask.execute(mMovie);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Network is not Available");
                builder.setPositiveButton(R.string.action_exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.create();
                builder.show();
            }
        }
        if((mTrailerAdapter.getCount()==0)) {

            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity());
            if (NetworkUtils.getInstance(getContext()).isOnline()) {
                Log.e("onActivityCreated", "getting data for Trailer and Review ");
                fetchTrailerTask.execute(mMovie);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Network is not Available");
                builder.setPositiveButton(R.string.action_exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.create();
                builder.show();
            }
        }
        Log.e("onActivityCreated", "Loading data for Review ");
        if (getLoaderManager().getLoader(ReviewQuery.REVIEW_LOADER)!= null)
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
        else
            getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, null, this);

        if (getLoaderManager().getLoader(TrailerQuery.TRAILER_LOADER)!= null)
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, null, this);
        else
            getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*switch (id) {
            case MovieQuery.DETAIL_LOADER:*/
                Log.v(LOG_TAG, "In onCreateLoader");
                Intent intent = getActivity().getIntent();
                if (intent == null) {
                    return null;
                }
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        MovieQuery.MOVIE_COLUMNS,
                        MovieQuery.SELECTION,
                        null,
                        null
                );
               /* if (mMovie == null) {
                    return null;
                }
                Uri movieUri = MovieContract.MovieEntry.buildMovie(mMovie);
                return new CursorLoader(
                        getActivity(),
                        movieUri,
                        MovieQuery.MOVIE_COLUMNS,
                        MovieQuery.SELECTION,
                        null,
                        null
                );
            case TrailerQuery.TRAILER_LOADER:
                Log.v(LOG_TAG, "In onCreate Trailer Loader");
                if (mMovie == null) {
                    return null;
                }
                Uri trailerUri = MovieContract.TrailerEntry.buildTrailerMovie(mMovie);
                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TrailerQuery.TRAILER_COLUMNS,
                        TrailerQuery.SELECTION,
                        null,
                        null
                );
            case ReviewQuery.REVIEW_LOADER:
                Log.v(LOG_TAG, "In onCreate Review Loader");
                if (mMovie == null) {
                    return null;
                }
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewMovie(mMovie);
                return new CursorLoader(
                        getActivity(),
                        reviewUri,
                        ReviewQuery.REVIEW_COLUMNS,
                        ReviewQuery.SELECTION,
                        null,
                        null
                );
            }
        return null;*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*switch (loader.getId()) {
            case MovieQuery.DETAIL_LOADER:*/
                Log.v(LOG_TAG, "In onLoadFinished");
                if (!data.moveToFirst()) {
                    return;
                }
                mMovie = data.getString(MovieQuery.COL_MOVIE_ID);
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

                ((TextView) getView().findViewById(R.id.overview_text))
                        .setText(data.getString(MovieQuery.COL_MOVIE_OVERVIEW));
               /* break;
            case TrailerQuery.TRAILER_LOADER:
                Log.v(LOG_TAG, "In onLoadFinished Trailer");
                data.setNotificationUri(getContext().getContentResolver(), MovieContract.TrailerEntry.buildTrailerMovie(mMovie));
                if (null == mTrailerAdapter)
                    mTrailerAdapter = new TrailerAdapter(getActivity(),null,0);
                ListView trailerlistView = (ListView) getActivity().findViewById(R.id.Listview_movieTrailer);
                if (trailerlistView.getAdapter() != mTrailerAdapter)
                    trailerlistView.setAdapter(mTrailerAdapter);
                //if (mReviewAdapter.getCount() < data.getCount())
                if (mTrailerAdapter.getCursor() != data)
                    mTrailerAdapter.swapCursor(data);
                break;
            case ReviewQuery.REVIEW_LOADER:
                Log.v(LOG_TAG, "In onLoadFinished Review");
                data.setNotificationUri(getContext().getContentResolver(), MovieContract.ReviewEntry.buildReviewMovie(mMovie));
                if (null == mReviewAdapter)
                    mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
                ListView reviewlistView = (ListView) getActivity().findViewById(R.id.Listview_movieReview);
                if (reviewlistView.getAdapter() != mReviewAdapter)
                    reviewlistView.setAdapter(mReviewAdapter);
                //if (mReviewAdapter.getCount() < data.getCount())
                if (mReviewAdapter.getCursor() != data)
                    mReviewAdapter.swapCursor(data);
                break;
            }*/
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       /* switch (loader.getId()) {
            case MovieQuery.DETAIL_LOADER:
                Log.v(LOG_TAG, "In onLoadReset");
                break;
            case TrailerQuery.TRAILER_LOADER:
                mTrailerAdapter.swapCursor(null);
                break;
            case ReviewQuery.REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
        }*/
    }

    public interface MovieQuery {
         static final int DETAIL_LOADER = 0;
         static final Uri CONTENT_URI= MovieContract.MovieEntry.CONTENT_URI;
         static final String SELECTION=null;
         static final String SORT_ORDER = MovieContract.MovieEntry._ID;
         static final String[] MOVIE_COLUMNS = {
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
        static final int COL_MOVIEID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_MOVIE_POPULARITY = 2;
        static final int COL_MOVIE_VOTE_AVERAGE = 3;
        static final int COL_MOVIE_FAVOURITE = 4;
        static final int COL_MOVIE_ORIGINAL_TITLE = 5;
        static final int COL_MOVIE_OVERVIEW = 6;
        static final int COL_MOVIE_RELEASE_DATE = 7;
        static final int COL_MOVIE_POSTER = 8;
    }

    /*public interface TrailerQuery {
        static final int TRAILER_LOADER = 1;
        static final Uri CONTENT_URI= MovieContract.TrailerEntry.CONTENT_URI;
        static final String SELECTION = null;
        static final String SORT_ORDER = MovieContract.TrailerEntry._ID;
        static final String[] TRAILER_COLUMNS = {
                MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
                MovieContract.TrailerEntry.COLUMN_MOV_KEY,
                MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
                MovieContract.TrailerEntry.COLUMN_KEY,
                MovieContract.TrailerEntry.COLUMN_SIZE
        };
        static final int COL_TRAILERID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_TRAILER_ID = 2;
        static final int COL_TRAILER_KEY = 3;
        static final int COL_TRAILER_SIZE=4;
    }

    public interface ReviewQuery {
        static final int REVIEW_LOADER = 2;
        static final Uri CONTENT_URI= MovieContract.ReviewEntry.CONTENT_URI;
        static final String SELECTION=null;
        static final String SORT_ORDER = MovieContract.ReviewEntry._ID;
        static final String[] REVIEW_COLUMNS = {
                MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
                MovieContract.ReviewEntry.COLUMN_MOV_KEY,
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                MovieContract.ReviewEntry.COLUMN_CONTENT
        };
        static final int COL_REVIEWID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_REVIEW_ID = 2;
        static final int COL_REVIEW_AUTHOR = 3;
        static final int COL_REVIEW_CONTENT=4;
    }*/
}
