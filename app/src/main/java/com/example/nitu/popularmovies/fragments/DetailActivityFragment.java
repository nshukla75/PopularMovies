package com.example.nitu.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.adaptors.ReviewAdapter;
import com.example.nitu.popularmovies.adaptors.TrailerAdapter;
import com.example.nitu.popularmovies.data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    public interface OnDataPass {
        public void DataPass(String data);
    }
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

    private long movieRowId;
    public String movieStr;
    private String title;
    public ToggleButton btnToggle;
    private ListView listViewTrailer;
    private ListView listViewReview;
    private View rootView;

    public DetailActivityFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        mTrailerAdapter=new TrailerAdapter(getActivity(),null,0);
        mReviewAdapter=new ReviewAdapter(getActivity(),null,0);
        if (savedInstanceState != null) {
            getLoaderManager().restartLoader(MovieQuery.DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, null, this);
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
         }
        else {
            //getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(Context a) {
        super.onAttach(a);

        try {
            dataPasser = (OnDataPass) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString() + " must implement onDataPass");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Create View", "in Create View...............");
        Bundle arguments = getActivity().getIntent().getExtras();
        movieStr = arguments.getString("movieKey");
        if (movieStr == null) { return null; }
        getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, null, this);


        rootView = (View)inflater.inflate(R.layout.fragment_detail, container, false);

        getActivity().setTitle(title);
        btnToggle = (ToggleButton) rootView.findViewById(R.id.chkState);
        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateFavourite(1, movieStr);
                    Toast.makeText(getActivity(), title + "is added to your Favourite List", Toast.LENGTH_SHORT).show();
                } else {
                    updateFavourite(0, movieStr);
                    Toast.makeText(getActivity(), title + "is removed from your Favourite List", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.e(LOG_TAG, "going to load view" + movieStr);
        //LinearLayout trailerLayout = (LinearLayout)getActivity().findViewById(R.id.trailer_parent);
        //View trailerView = inflater.inflate(R.layout.trailer_movie, container, false);
        listViewTrailer = (ListView) rootView.findViewById(R.id.listView_trailer);
        //if (mTrailerAdapter.getCount() > 0) {
            listViewTrailer.setAdapter(mTrailerAdapter);
            //String tempURL=AppConstants.MOVIE_YOUTUBE_URL + data.getString(TrailerQuery.COL_TRAILER_KEY);
            //passData(tempURL);
            listViewTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        startVideoOnBrowser(cursor.getString(TrailerQuery.COL_TRAILER_KEY));
                        //startVideoOnApp(cursor.getString(COL_TRAILER_KEY));
                    }
                }
            });
        //}


        Log.e(LOG_TAG,"going to load view" + movieStr);
        //LinearLayout reviewLayout = (LinearLayout)getActivity().findViewById(R.id.review_parent);
        //View reviewView = inflater.inflate(R.layout.review_movie, container, false);
        listViewReview = (ListView) rootView.findViewById(R.id.listView_review);
        //if (mReviewAdapter.getCount() > 0)
            listViewReview.setAdapter(mReviewAdapter);
        /*listViewTrailer.addHeaderView(detailView);
        listViewTrailer.addFooterView(reviewView);*/
        /*rootView.addView(detailLayout,1);
        rootView.addView(trailerLayout,2);
        rootView.addView(reviewLayout,3);*/
        Log.e(LOG_TAG,"trailer Count"+  mTrailerAdapter.getCount());
        Log.e(LOG_TAG, "review Count" + mReviewAdapter.getCount());
        return rootView;
    }

    private void startVideoOnBrowser(String videoID) {
        // default youtube app
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.MOVIE_YOUTUBE_URL + videoID));
        startActivity(intent);
    }

    public void updateFavourite(int chkFavourite, String movieKey){
        Cursor movieCursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        ContentValues updateValues = new ContentValues();
        updateValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, chkFavourite);
        updateValues.put(MovieContract.MovieEntry._ID, movieRowId);
        int count = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?", new String[] { Long.toString(movieRowId)});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        /*Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }*/
        Bundle arguments = getActivity().getIntent().getExtras();
        movieStr = arguments.getString("movieKey");
        if (movieStr ==null) { return null; }
        switch (id) {
            case 0:
                Uri detailUri = MovieContract.MovieEntry.buildMovie(movieStr);
                CursorLoader movieCursorLoader = new CursorLoader(
                        getActivity(),
                        detailUri,
                        MovieQuery.MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
                return movieCursorLoader;
            //break;
            case 1:
                Uri trailerUri = MovieContract.MovieEntry.buildTrailerMovie(movieStr);
                CursorLoader trailerCursorLoader= new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TrailerQuery.TRAILER_COLUMNS,
                        null,
                        null,
                        null
                );
                return trailerCursorLoader;
            //break;
            case 2:
                Uri reviewUri = MovieContract.MovieEntry.buildReviewMovie(movieStr);
                CursorLoader reviewCursorLoader= new CursorLoader(getActivity(),
                        reviewUri,
                        ReviewQuery.REVIEW_COLUMNS,
                        null,
                        null,
                        null
                );
                return reviewCursorLoader;
            //break;
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case 0:
                data.setNotificationUri(getContext().getContentResolver(), MovieContract.MovieEntry.buildMovie(movieStr));
                mMovieAdapter.swapCursor(data);
                Log.v(LOG_TAG, "In onLoadFinished");
                if (!data.moveToFirst()) {
                    return;
                }
                movieStr = data.getString(MovieQuery.COL_MOVIE_KEY);
                movieRowId=data.getLong(MovieQuery.COL_MOVIEID);
                title=data.getString(MovieQuery.COL_MOVIE_ORIGINAL_TITLE);
                ((TextView) rootView.findViewById(R.id.title_text)).setText(title);

                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                byte[] bb = Utility.getImage(data);
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));

                String mMovieVoteAverage = data.getString(MovieQuery.COL_MOVIE_VOTE_AVERAGE);

                ((TextView) rootView.findViewById(R.id.voteaverage_text))
                        .setText(mMovieVoteAverage + "/10");
                Float f = Float.parseFloat(mMovieVoteAverage);
                ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(f);

                ((TextView) rootView.findViewById(R.id.release_text))
                        .setText(data.getString(MovieQuery.COL_MOVIE_RELEASE_DATE));

                btnToggle = (ToggleButton)rootView.findViewById(R.id.chkState);
                if (data.getInt(MovieQuery.COL_MOVIE_FAVOURITE)!= 0)
                    btnToggle.setChecked(true);
                else
                    btnToggle.setChecked(false);

                ((TextView) rootView.findViewById(R.id.overview_text))
                        .setText(data.getString(MovieQuery.COL_MOVIE_OVERVIEW));
               Log.e(LOG_TAG, "going to start Review Fragment");
                break;
            case 1:
                Log.e(LOG_TAG,"In trailer load finish loader");
                data.setNotificationUri(getContext().getContentResolver(), MovieContract.MovieEntry.buildTrailerMovie(movieStr));

                if (null == mTrailerAdapter)
                    mTrailerAdapter = new TrailerAdapter(getActivity(),null,0);
                if (mTrailerAdapter.getCursor() != data)
                    mTrailerAdapter.swapCursor(data);
                if (listViewTrailer.getAdapter() != mTrailerAdapter)
                    listViewTrailer.setAdapter(mTrailerAdapter);
                if (data.moveToFirst()) {
                    String tempURL=AppConstants.MOVIE_YOUTUBE_URL + data.getString(TrailerQuery.COL_TRAILER_KEY);
                    dataPasser.DataPass(tempURL);
                }
                Log.e(LOG_TAG,"out trailer load finish loader"+ data.getCount());
                break;
            case 2:
                Log.e(LOG_TAG,"In Review load finish loader Review");
                data.setNotificationUri(getContext().getContentResolver(), MovieContract.MovieEntry.buildReviewMovie(movieStr));

                if (null == mReviewAdapter)
                    mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
                if (mReviewAdapter.getCursor() != data)
                    mReviewAdapter.swapCursor(data);
                if (listViewReview.getAdapter() != mReviewAdapter)
                    listViewReview.setAdapter(mReviewAdapter);

                Log.e(LOG_TAG,"out Review load finish loader Review"+ data.getCount());
                break;


        /*if (movieStr != null) {
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
            }*/
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "In onLoadReset");
    }
}
