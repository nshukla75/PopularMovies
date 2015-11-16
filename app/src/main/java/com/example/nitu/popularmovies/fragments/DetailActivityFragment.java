package com.example.nitu.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.AppConstants;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.adaptors.ReviewListViewAdapter;
import com.example.nitu.popularmovies.adaptors.TrailerListViewAdapter;
import com.example.nitu.popularmovies.application.PopMovieApp;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.data.MovieProvider;
import com.example.nitu.popularmovies.model.ReviewData;
import com.example.nitu.popularmovies.model.TrailerData;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static volatile PopMovieApp.State appState;
    public static final String MOVIE_ID_KEY = "movie_id_key";
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
    private static final AtomicBoolean isInit = new AtomicBoolean();
    private final AtomicBoolean trailerDataModified = new AtomicBoolean();
    private final AtomicBoolean reviewDataModified = new AtomicBoolean();
    private final AtomicBoolean movieDetailsModified = new AtomicBoolean();
    private final AtomicBoolean movieMinutesModified = new AtomicBoolean();

    public Long mMovieId = Long.MIN_VALUE;
    public Long movieRowId;
    private String title;
    private TrailerData YouTubeFirstTrailerURL=null;
    private static String sMovieIdKey;
    private static String sBaseUrl;
    private static String sVideoUrl;
    private static String sReviewKey;
    private static String sYoutubeUrl;

    public ToggleButton btnToggle;
    private View rootView;
    private View noTrailerView;
    private View noReviewView;
    private ListView listViewTrailer;
    private ListView listViewReview;
    private final List<TrailerData> mTrailerList = new ArrayList<>();
    private final List<ReviewData> mReviewList = new ArrayList<>();
    private TrailerListViewAdapter mTrailerListViewAdapter;
    private ReviewListViewAdapter mReviewListViewAdapter;

    private Menu mMenu;
    private MenuItem shareMenuItem;
    private ShareActionProvider mShareActionProvider;
    private RequestQueue mVolleyRequestQueue;

    public DetailActivityFragment() {setHasOptionsMenu(true);}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(sMovieIdKey, mMovieId);
        outState.putParcelable("ShareYoutubeLinkKey", YouTubeFirstTrailerURL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVolleyRequestQueue = Volley.newRequestQueue(getActivity());
        intilizeStatic();
        mMovieAdapter=new MovieAdapter(getActivity(),null,0);
        trailerDataModified.set(false);
        reviewDataModified.set(false);
        movieDetailsModified.set(false);
        movieMinutesModified.set(false);

        if (savedInstanceState != null) {
            synchronized (mMovieId) {
                mMovieId = savedInstanceState.getLong(sMovieIdKey);
                YouTubeFirstTrailerURL = savedInstanceState.getParcelable("ShareYoutubeLinkKey");
                if (YouTubeFirstTrailerURL != null)
                    if (mShareActionProvider != null) {
                        mMenu.findItem(R.id.action_share).setVisible(true);
                    } else Log.e(LOG_TAG, "mShareActionProvider not set");
                try {
                    mMovieId.notifyAll();
                } catch (IllegalMonitorStateException x) {}
            }
            Bundle b = new Bundle();
            b.putLong(sMovieIdKey, mMovieId);
            getLoaderManager().restartLoader(MovieQuery.DETAIL_LOADER, b, this);
            getLoaderManager().restartLoader(TrailerQuery.TRAILER_LOADER, b, this);
            getLoaderManager().restartLoader(ReviewQuery.REVIEW_LOADER, b, this);
         }
        else {
            //runFragment();
        }
    }

    private void intilizeStatic() {
        synchronized (DetailActivityFragment.class) {
            if (!isInit.get()) {
                appState = ((PopMovieApp) getActivity().getApplication()).STATE;
                sMovieIdKey = getString(R.string.movie_id_key);
                sBaseUrl = getString(R.string.tmdb_api_base_movie_url);
                sVideoUrl = getString(R.string.tmdb_api_movie_videos_url);
                sReviewKey = getString(R.string.tmdb_api_movie_review_url);
                sYoutubeUrl= getString(R.string.youtube_url);
                isInit.set(true);
            }
        }
    }

    private void runFragment() {
        if (mMovieId == Long.MIN_VALUE) synchronized (mMovieId) {
            Bundle args = getArguments();
            mMovieId = args == null ?
                    getActivity().getIntent().getLongExtra(sMovieIdKey, Long.MIN_VALUE) :
                    args.getLong(DetailActivityFragment.MOVIE_ID_KEY);
            try {
                mMovieId.notifyAll();
            } catch (IllegalMonitorStateException x) {
            }

            Bundle b = new Bundle();
            b.putLong(sMovieIdKey, mMovieId);
            getLoaderManager().initLoader(MovieQuery.DETAIL_LOADER, b, this);
            getLoaderManager().initLoader(TrailerQuery.TRAILER_LOADER, b, this);
            getLoaderManager().initLoader(ReviewQuery.REVIEW_LOADER, b, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        YouTubeFirstTrailerURL = null;
        runFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        if (!appState.getTwoPane()){
            Utility.makeMenuItemInvisible(mMenu, R.id.action_settings);
            //for crate home button
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("MovieDetail");
        }
        else
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Pop Movies");
        }
        shareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
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
                Intent shareIntent = createShareYoutubeIntent();
                if (shareIntent!=null)
                    startActivity(createShareYoutubeIntent());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Create View", "in Create View...............");
        rootView = (View) inflater.inflate(R.layout.fragment_detail, container, false);

        getActivity().setTitle(title);
        btnToggle = (ToggleButton) rootView.findViewById(R.id.chkState);
        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateFavourite(1, mMovieId);
                    Toast.makeText(getActivity(), title + "is added to your Favourite List", Toast.LENGTH_SHORT).show();
                } else {
                    updateFavourite(0, mMovieId);
                    Toast.makeText(getActivity(), title + "is removed from your Favourite List", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.v(LOG_TAG, "going to load view" + mMovieId.toString());
        noTrailerView = (View) inflater.inflate(R.layout.no_trailer, container, false);
        View trailerView = (View) inflater.inflate(R.layout.trailer_movie, container, false);
        listViewTrailer = (ListView) trailerView.findViewById(R.id.listView_trailer);
        mTrailerListViewAdapter = new TrailerListViewAdapter(getActivity(), R.layout.list_item_trailer, mTrailerList);
        listViewTrailer.setAdapter(mTrailerListViewAdapter);

        Log.v(LOG_TAG, "going to load view" + mMovieId.toString());
        noReviewView = (View) inflater.inflate(R.layout.no_review, container, false);
        View reviewView = inflater.inflate(R.layout.review_movie, container, false);
        listViewReview = (ListView) reviewView.findViewById(R.id.listView_review);
        mReviewListViewAdapter = new ReviewListViewAdapter(getActivity(), R.layout.list_item_review, mReviewList);
        listViewReview.setAdapter(mReviewListViewAdapter);

        return rootView;
    }

    public void updateFavourite(int chkFavourite, Long movieKey){
        Cursor movieCursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        ContentValues updateValues = new ContentValues();
        updateValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, chkFavourite);
        updateValues.put(MovieContract.MovieEntry._ID, movieRowId);
        int count = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?", new String[]{Long.toString(movieRowId)});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        long mid = args.getLong(sMovieIdKey);
        if (mid > 0L)
            switch (id) {
                case 0:
                    Uri detailUri = MovieContract.MovieEntry.buildMovie(mid);
                    return new CursorLoader(
                            getActivity(),
                            detailUri,
                            MovieQuery.MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );

            case 1:
                Uri trailerUri = MovieContract.MovieEntry.buildTrailerMovie(mid);
                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TrailerQuery.TRAILER_COLUMNS,
                        null,
                        null,
                        null
                );

            case 2:
                Uri reviewUri = MovieContract.MovieEntry.buildReviewMovie(mid);
                return new CursorLoader(getActivity(),
                        reviewUri,
                        ReviewQuery.REVIEW_COLUMNS,
                        null,
                        null,
                        null
                );
            default: return null;
        }
        else return null;
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
                        Toast.makeText(getContext(), "No Data Loaded. Please go back and select movie again", Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                        return;
                    }

                    movieRowId=data.getLong(MovieQuery.COL_MOVIEID);
                    mMovieId = data.getLong(MovieQuery.COL_MOVIE_KEY);
                    title = data.getString(MovieQuery.COL_MOVIE_ORIGINAL_TITLE);
                    ((TextView) rootView.findViewById(R.id.title_text)).setText(title);

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                    Picasso.with(getContext())
                            .load(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)))
                            .error(R.drawable.abc_btn_rating_star_off_mtrl_alpha)
                                    //.fit()
                            .into(imageView);

                    if (data.getInt(MovieQuery.COL_MOVIE_RUNTIME)<=0)
                        movieMinutesModified.set(false);
                    else {
                        movieMinutesModified.set(true);
                        ((TextView) rootView.findViewById(R.id.runtime_text))
                                .setText(Integer.toString(data.getInt(MovieQuery.COL_MOVIE_RUNTIME)) + "min");
                    }

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
                    if(!movieMinutesModified.get())
                    {
                        updateMinutesDataOrAskServer(data);
                    }
                    Log.v(LOG_TAG, "Out of Load Finish Movie");
                }
                break;
            case MovieProvider.TRAILER_WITH_MOVIE_KEY:
                Log.v(LOG_TAG,"In Trailer load finish loader");
                if (!trailerDataModified.get())
                    updateTrailerDataOrAskServer(data);
                Log.v(LOG_TAG,"out Trailer load finish loader");
                break;
            case MovieProvider.REVIEW_WITH_MOVIE_KEY:
                Log.v(LOG_TAG,"In Review load finish loader");
                if (!reviewDataModified.get())
                    updateReviewDataOrAskServer(data);
                Log.v(LOG_TAG,"out Review load finish loader");
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "In onLoadReset");
    }

    private void updateMinutesDataOrAskServer(Cursor data) {
        Integer minutes = data == null ? 0 : data.getInt(MovieQuery.COL_MOVIE_RUNTIME);
        if (minutes <= 0) getMinutesDataAsync();
        else handleMinutesResults(minutes.toString());
    }

    private void updateTrailerDataOrAskServer(Cursor data) {
        if (data == null || data.getCount() == 0) getVideoDataAsync();
        else
        {
            data.moveToFirst();
            Set<TrailerData> th = new LinkedHashSet<>();
            while(!data.isAfterLast()) {
                String trailer_title =data.getString(data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SIZE));
                String youtube_key = data.getString(data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY));
                Long movie_key = mMovieId;
                String trailer_key = data.getString(data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY));;
                th.add(new TrailerData(youtube_key, trailer_title, movie_key,trailer_key));
                data.moveToNext();
            }
            mTrailerList.clear();
            mTrailerList.addAll(th);
            showTrailerUIAsync(mTrailerList);
            trailerDataModified.set(true);
        }
    }

    private void updateReviewDataOrAskServer(Cursor data) {
        if (data == null || data.getCount() == 0) getReviewDataAsync();
        else
        {
            data.moveToFirst();
            Set<ReviewData> rev = new LinkedHashSet<>();
            while(!data.isAfterLast()) {
                String content =data.getString(data.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
                String author = data.getString(data.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
                Long movie_key = mMovieId;
                String url = "";
                rev.add(new ReviewData(content, author, movie_key, url));
                data.moveToNext();
            }
            mReviewList.clear();
            mReviewList.addAll(rev);
            showReviewUIAsync(mReviewList);
            reviewDataModified.set(true);
        }
    }

    @NonNull
    private void getMinutesDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sBaseUrl, mMovieId)).buildUpon()
                .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
                .build();
        String url = "";
        try {
            url = new URL(builtUri.toString()).toString();
        } catch (MalformedURLException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "Minutes Response received.");
                        Map<String, Object> map = Utility.getGson().fromJson(response.toString(), LinkedTreeMap.class);
                        try {
                            String rt = map.get("runtime").toString().trim();
                            handleMinutesResults(rt);
                        } catch (NumberFormatException | NullPointerException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getClass().getSimpleName(), error.getMessage(), error);
                        ((TextView) rootView.findViewById(R.id.runtime_text)).setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error connecting to server.", Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequestQueue.add(jsObjRequest);
    }

    private void getVideoDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sVideoUrl, mMovieId)).buildUpon()
                .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
                .build();
        String url = "";
        try {
            url = new URL(builtUri.toString()).toString();
        } catch (MalformedURLException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "Video Response received.");
                        Map<String, Object> map = Utility.getGson().fromJson(response.toString(), LinkedTreeMap.class);
                        try {
                            List<Map<String, String>> results = (List<Map<String, String>>) map.get("results");
                            handleTrailerResults(results);
                        } catch (NumberFormatException | NullPointerException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getClass().getSimpleName(), error.getMessage(), error);
                        Toast.makeText(getContext(), "Error connecting to server.", Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequestQueue.add(jsObjRequest);
    }

    private void getReviewDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sReviewKey, mMovieId)).buildUpon()
                .appendQueryParameter(AppConstants.API_KEY, AppConstants.MOVIE_API_KEY)
                .build();
        String url = "";
        try {
            url = new URL(builtUri.toString()).toString();
        } catch (MalformedURLException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DetailsActivity", "Review Response received.");
                        Map<String, Object> map = Utility.getGson().fromJson(response.toString(), LinkedTreeMap.class);
                        try {
                            List<Map<String, String>> results = (List<Map<String, String>>) map.get("results");
                            handleReviewResults(results);
                        } catch (NumberFormatException | NullPointerException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getClass().getSimpleName(), error.getMessage(), error);
                        Toast.makeText(getContext(), "Error connecting to server.", Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequestQueue.add(jsObjRequest);
    }

    private void handleMinutesResults(String rt) {
        ((TextView) rootView.findViewById(R.id.runtime_text)).setText(Double.valueOf(rt).intValue() + " mins");
        updateMinutesDataInternal(rt);
        movieMinutesModified.set(true);
    }

    private void handleTrailerResults(List<Map<String, String>> results) {
        Set<TrailerData> th = new LinkedHashSet<>();
        for (Map<String, String> r : results) {
            String trailer_title = r.get("name");
            String youtube_key = r.get("key");
            Long movie_key = mMovieId;
            String trailer_key = r.get("id");
            th.add(new TrailerData(youtube_key, trailer_title, movie_key,trailer_key));
        }
        mTrailerList.clear();
        mTrailerList.addAll(th);
        if (!trailerDataModified.get()) showTrailerUIAsync(mTrailerList);
        updateTrailerDataInternal(mTrailerList);
        trailerDataModified.set(true);
    }

    private void handleReviewResults(List<Map<String, String>> results) {
        Set<ReviewData> rev = new LinkedHashSet<>();
        for (Map<String, String> r : results) {
            String content = r.get("content");
            String author = r.get("author");
            Long movie_key = mMovieId;
            String url = r.get("url");
            rev.add(new ReviewData(content, author, movie_key, url));
        }
        mReviewList.clear();
        mReviewList.addAll(rev);
        if (!reviewDataModified.get()) showReviewUIAsync(mReviewList);
        updateReviewDataInternal(mReviewList);
        reviewDataModified.set(true);
    }

    private void showTrailerUIAsync(List<TrailerData> mTrailerList){
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.trailer_linear);
        if (!mTrailerList.isEmpty()) {
            mTrailerListViewAdapter.setData();
            YouTubeFirstTrailerURL = mTrailerList.get(0);
            final int adapterCount = mTrailerListViewAdapter.getCount();
            // Only few trailers(<10) so easiest way for Linear Layout
            for (int i = 0; i < adapterCount; i++) {
                View item = mTrailerListViewAdapter.getView(i, null, null);
                ll.addView(item);
            }
        }
        else {
            ll.addView(noTrailerView);
            YouTubeFirstTrailerURL = null;
        }
    }

    private void showReviewUIAsync(List<ReviewData> mReviewList){
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.review_linear);
        if (!mReviewList.isEmpty()) {
            mReviewListViewAdapter.setData();
            final int adapterCount = mReviewListViewAdapter.getCount();
            // Only few Reviews(<10) so easiest way for Linear Layout
            for (int i = 0; i < adapterCount; i++) {
                View item = mReviewListViewAdapter.getView(i, null, null);
                ll.addView(item);
            }
        }
        else {
            ll.addView(noReviewView);
        }
    }

    private void updateMinutesDataInternal(String minutes) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, mMovieId);
        updateValues.put(MovieContract.MovieEntry.COLUMN_MINUTE, Double.valueOf(minutes).intValue());

        // add to database
        getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "= ?", new String[]{mMovieId.toString()});
}

    private void updateTrailerDataInternal(List<TrailerData> mTrailerList) {
        Vector<ContentValues> cVVector = new Vector<ContentValues>(mTrailerList.size());
        for (int i = 0; i < mTrailerList.size(); i++) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOV_KEY, mTrailerList.get(i).movie_key);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, mTrailerList.get(i).trailer_key);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, mTrailerList.get(i).youtube_key);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, mTrailerList.get(i).trailer_title);
            cVVector.add(trailerValues);
        }

        int inserted = 0;
        // add to database
        if(cVVector.size()>0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,cvArray);
        }
    }

    private void updateReviewDataInternal(List<ReviewData> mReviewList) {
        Vector<ContentValues> cVVector = new Vector<ContentValues>(mReviewList.size());
        for (int i = 0; i < mReviewList.size(); i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOV_KEY, mReviewList.get(i).movie_key);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, mReviewList.get(i).content);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, mReviewList.get(i).author);
            cVVector.add(reviewValues);
        }

        int inserted = 0;
        // add to database
        if(cVVector.size()>0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,cvArray);
        }
    }

    private void blockUntilMovieIdSet() {
        if (mMovieId == Long.MIN_VALUE)
            synchronized (mMovieId) {
                try {
                    mMovieId.wait();
                } catch (InterruptedException e) {
                }
            }
    }

    private Intent createShareYoutubeIntent() {
        if (YouTubeFirstTrailerURL != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            String text = (title + " - ")
                    + YouTubeFirstTrailerURL.trailer_title + " - "
                    + String.format(sYoutubeUrl, YouTubeFirstTrailerURL.youtube_key);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            return shareIntent;
        }
        return null;
    }

}
