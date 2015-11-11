package com.example.nitu.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.Utilities.NetworkUtils;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.adaptors.ReviewAdapter;
import com.example.nitu.popularmovies.adaptors.TrailerAdapter;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.data.MovieProvider;
import com.example.nitu.popularmovies.fetchtasks.FetchReviewTask;
import com.example.nitu.popularmovies.fetchtasks.FetchTrailerTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

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
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_MINUTE
        };
        static final int COL_MOVIEID = 0;
        static final int COL_MOVIE_KEY = 1;
        static final int COL_MOVIE_POPULARITY = 2;
        static final int COL_MOVIE_VOTE_AVERAGE = 3;
        static final int COL_MOVIE_FAVOURITE = 4;
        static final int COL_MOVIE_ORIGINAL_TITLE = 5;
        static final int COL_MOVIE_OVERVIEW = 6;
        static final int COL_MOVIE_RELEASE_DATE = 7;
        static final int COL_MOVIE_POSTERPATH = 8;
        static final int COL_MOVIE_RUNTIME = 9;
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

    private static final UriMatcher sUriMatcher = MovieProvider.buildUriMatcher();
    private final AtomicBoolean trailerDataModified = new AtomicBoolean();
    private final AtomicBoolean reviewDataModified = new AtomicBoolean();
    private final AtomicBoolean movieDetailsModified = new AtomicBoolean();

    private long movieRowId;
    public String movieStr;
    private String title;
    private String YouTubeFirstTrilerURL;

    public ToggleButton btnToggle;
    private View rootView;
    private ListView listViewTrailer;
    private ListView listViewReview;
    private Menu mMenu;
    private MenuItem shareMenuItem;
    private ShareActionProvider mShareActionProvider;



    public DetailActivityFragment() {setHasOptionsMenu(true);}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("movieKey", movieStr);
        outState.putString("ShareYoutubeLinkKey", YouTubeFirstTrilerURL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        mTrailerAdapter=new TrailerAdapter(getActivity(),null,0);
        mReviewAdapter=new ReviewAdapter(getActivity(),null,0);
        trailerDataModified.set(false);
        reviewDataModified.set(false);
        if (savedInstanceState != null) { synchronized (movieStr) {
            movieStr = savedInstanceState.getString("movieKey");
            YouTubeFirstTrilerURL = savedInstanceState.getString("ShareYoutubeLinkKey");
            if (YouTubeFirstTrilerURL != null)
                if (mShareActionProvider != null) {
                    mMenu.findItem(R.id.action_share).setVisible(true);
                } else Log.e(LOG_TAG, "mShareActionProvider not set");
            try {
                movieStr.notifyAll();
            } catch (IllegalMonitorStateException x) {
            }
            }
            getLoaderManager().restartLoader(MovieQuery.DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, null, this);
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, null, this);
         }
        else {
            runFragment();
        }
    }

    private void runFragment() {
        Bundle arguments = getActivity().getIntent().getExtras();
        movieStr = arguments.getString("movieKey");
        if (movieStr != null) {
            updateReview(movieStr);
            updateTrailer(movieStr);
            Bundle b = new Bundle();
            b.putString("movieKey", movieStr);
            getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, b, this);
            getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, b, this);
            getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, b, this);
        }
    }

    private void updateReview(String movieKey){
        Log.e(LOG_TAG, "In update Review");
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity());
        if (NetworkUtils.getInstance(getContext()).isOnline()) {
            Log.e("In update Review", "getting data for Review ");
            Log.e(LOG_TAG, "going to fetch review data for " + movieKey);
            fetchReviewTask.execute(movieKey);
        } else {
            Toast.makeText(getActivity(),"Reviews are not Available",Toast.LENGTH_LONG).show();
        }
        Log.e(LOG_TAG, "OUT update Review");
    }

    private void updateTrailer(String movieKey){
        Log.e(LOG_TAG,"In update Trailer");
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity());
        if (NetworkUtils.getInstance(getContext()).isOnline()) {
            Log.e("In update Trailer", "getting data for Trailer ");
            Log.e(LOG_TAG, "going to fetch trailer data for " + movieKey);
            fetchTrailerTask.execute(movieKey);
        } else {
            Toast.makeText(getActivity(), "Trailers are not Available", Toast.LENGTH_LONG).show();
        }
        Log.e(LOG_TAG, "OUT update Trailer");
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        inflater.inflate(R.menu.menu_detail, menu);
        shareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        shareMenuItem.setVisible(false);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        Utility.makeMenuItemInvisible(mMenu, R.id.action_settings);
        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Trailer of Movie - "+ title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, YouTubeFirstTrilerURL);
                startActivity(Intent.createChooser(shareIntent, "Reciever's Address"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Create View", "in Create View...............");

        if (movieStr == null) { return null; }
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
        View trailerView = (View)inflater.inflate(R.layout.trailer_movie, container, false);
        listViewTrailer = (ListView) trailerView.findViewById(R.id.listView_trailer);
        //if (mTrailerAdapter.getCount() > 0) {
            listViewTrailer.setAdapter(mTrailerAdapter);
            listViewTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        startVideoOnBrowser(cursor.getString(TrailerQuery.COL_TRAILER_KEY));
                    }
                }
            });
        //}

        final int adapterCount = mTrailerAdapter.getCount();
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.trailer_linear);
        for (int i = 0; i < adapterCount; i++) {
            View item = mTrailerAdapter.getView(i, null, null);
            ll.addView(item);
        }

        Log.e(LOG_TAG,"going to load view" + movieStr);
        View reviewView = inflater.inflate(R.layout.review_movie, container, false);
        listViewReview = (ListView) reviewView.findViewById(R.id.listView_review);
        //if (mReviewAdapter.getCount() > 0)
            listViewReview.setAdapter(mReviewAdapter);
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
        int count = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?", new String[]{Long.toString(movieRowId)});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        movieStr = args.getString("movieKey");
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
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Uri uri = ((CursorLoader) loader).getUri();
        if (!data.moveToFirst()) data = null;
        int match = sUriMatcher.match(uri);
        switch (match){
            case MovieProvider.MOVIE_WITH_KEY:
                mMovieAdapter.swapCursor(data);
                Log.v(LOG_TAG, "In onLoadFinished Movie");
                if (!movieDetailsModified.get()) {
                    if (data == null || !data.moveToFirst()) {
                        Toast.makeText(getContext(), "No Data Loaded. Please go back and refresh", Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                        return;
                    }
                    movieStr = data.getString(MovieQuery.COL_MOVIE_KEY);
                    movieRowId = data.getLong(MovieQuery.COL_MOVIEID);
                    title = data.getString(MovieQuery.COL_MOVIE_ORIGINAL_TITLE);
                    ((TextView) rootView.findViewById(R.id.title_text)).setText(title);

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                    Picasso.with(getContext())
                            .load(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)))
                            .error(R.drawable.abc_btn_rating_star_off_mtrl_alpha)
                                    //.fit()
                            .into(imageView);

                    ((TextView) rootView.findViewById(R.id.runtime_text))
                            .setText(data.getString(MovieQuery.COL_MOVIE_RUNTIME) + "min");

                    String mMovieVoteAverage = data.getString(MovieQuery.COL_MOVIE_VOTE_AVERAGE);

                    ((TextView) rootView.findViewById(R.id.voteaverage_text))
                            .setText(mMovieVoteAverage + "/10");
                    Float f = Float.parseFloat(mMovieVoteAverage);
                    ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(f);

                    ((TextView) rootView.findViewById(R.id.release_text))
                            .setText(data.getString(MovieQuery.COL_MOVIE_RELEASE_DATE));

                    btnToggle = (ToggleButton) rootView.findViewById(R.id.chkState);
                    if (data.getInt(MovieQuery.COL_MOVIE_FAVOURITE) != 0)
                        btnToggle.setChecked(true);
                    else
                        btnToggle.setChecked(false);

                    ((TextView) rootView.findViewById(R.id.overview_text))
                            .setText(data.getString(MovieQuery.COL_MOVIE_OVERVIEW));

                    movieDetailsModified.set(true);
                    Log.v(LOG_TAG, "Out of Load Finish Movie");
                }
                break;
            case MovieProvider.TRAILER_WITH_MOVIE_KEY:
                Log.e(LOG_TAG,"In trailer load finish loader");
                if (null == mTrailerAdapter)
                    mTrailerAdapter = new TrailerAdapter(getActivity(),null,0);
                if (mTrailerAdapter.getCursor() != data)
                    mTrailerAdapter.swapCursor(data);
                if (listViewTrailer.getAdapter() != mTrailerAdapter)
                    listViewTrailer.setAdapter(mTrailerAdapter);
                if (data!= null) {
                    data.moveToFirst();
                    YouTubeFirstTrilerURL = AppConstants.MOVIE_YOUTUBE_URL + data.getString(TrailerQuery.COL_TRAILER_KEY);
                    if (mShareActionProvider != null) {
                        mMenu.findItem(R.id.action_share).setVisible(true);
                    } else Log.e(LOG_TAG,"mShareActionProvider not set");
                    if (!trailerDataModified.get()) {
                        final int adapterCount = mTrailerAdapter.getCount();
                        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.trailer_linear);
                        for (int i = 0; i < adapterCount; i++) {
                            View item = mTrailerAdapter.getView(i, null, null);
                            final String videoID = data.getString(TrailerQuery.COL_TRAILER_KEY);
                            item.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    startVideoOnBrowser(videoID);
                                }
                            });
                            data.moveToNext();
                            ll.addView(item);
                        }
                        trailerDataModified.set(true);
                    }
                }
                Log.e(LOG_TAG,"out trailer load finish loader");
                break;
            case MovieProvider.REVIEW_WITH_MOVIE_KEY:
                Log.e(LOG_TAG,"In Review load finish loader Review");
                if (null == mReviewAdapter)
                    mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
                if (mReviewAdapter.getCursor() != data)
                    mReviewAdapter.swapCursor(data);
                if (listViewReview.getAdapter() != mReviewAdapter)
                    listViewReview.setAdapter(mReviewAdapter);
                if (data!= null) {
                    if ((!reviewDataModified.get())) {
                        final int adapterCount = mReviewAdapter.getCount();
                        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.review_linear);
                        for (int i = 0; i < adapterCount; i++) {
                            View item = mReviewAdapter.getView(i, null, null);
                            ll.addView(item);
                        }
                        reviewDataModified.set(true);
                    }
                }
                Log.e(LOG_TAG,"out Review load finish loader Review");
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "In onLoadReset");
    }

}
