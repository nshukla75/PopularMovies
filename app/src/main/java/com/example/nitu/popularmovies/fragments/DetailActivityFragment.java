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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitu.popularmovies.BuildConfig;
import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.adaptors.MovieAdapter;
import com.example.nitu.popularmovies.adaptors.ReviewListViewAdapter;
import com.example.nitu.popularmovies.adaptors.TrailerListViewAdapter;
import com.example.nitu.popularmovies.application.PopMovieApp;
import com.example.nitu.popularmovies.data.MovieContract;
import com.example.nitu.popularmovies.data.MovieProvider;
import com.example.nitu.popularmovies.model.MovieData;
import com.example.nitu.popularmovies.model.ReviewData;
import com.example.nitu.popularmovies.model.TrailerData;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                MovieContract.MovieEntry.COLUMN_FAVOURITE,
                MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES
        };
        static final int COL_MOVIEID = 0;
        static final int COL_MOVIE_FAVOURITE = 2;
        static final int COL_MOVIE_RUNTIME = 3;
    }
    public interface TrailerQuery {
        static final int TRAILER_LOADER = 1;
    }
    public interface ReviewQuery {
        static final int REVIEW_LOADER = 2;
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
    private TrailerData YouTubeFirstTrailerURL = null;
    private MovieData mMovieData = null;
    private static String sMovieIdKey;
    private static String sParamApi;
    private static String sMinuteUrl;
    private static String sVideoUrl;
    private static String sReviewKey;
    private static String sYoutubeUrl;
    private static String sImgSize;
    private static String sImgUrl;

    public Button btnToggle;
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
    }

    private void intilizeStatic() {
        synchronized (DetailActivityFragment.class) {
            if (!isInit.get()) {
                appState = ((PopMovieApp) getActivity().getApplication()).STATE;
                sMovieIdKey = getString(R.string.movie_id_key);
                sParamApi = getString(R.string.tmdb_param_api);
                sMinuteUrl = getString(R.string.tmdb_api_minute_movie_url);
                sVideoUrl = getString(R.string.tmdb_api_movie_videos_url);
                sReviewKey = getString(R.string.tmdb_api_movie_review_url);
                sYoutubeUrl= getString(R.string.youtube_url);
                sImgUrl = getString(R.string.tmdb_image_base_url);
                sImgSize = "w185";
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
        switch(item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_share:
                Intent shareIntent = createShareYoutubeIntent();
                if (shareIntent != null)
                    startActivity(createShareYoutubeIntent());
                else
                    Toast.makeText(getActivity(), "No Trailer to share", Toast.LENGTH_SHORT).show();
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
        btnToggle = (Button) rootView.findViewById(R.id.chkState);
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFavourite(v);
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

    public void updateFavourite(View v) {
        if (movieDetailsModified.get()) {
            Uri detailUri = MovieContract.MovieEntry.buildUri(mMovieId);
            Cursor movieCursor = getContext().getContentResolver().query(detailUri, null, null, null, null);
            if (movieCursor.moveToFirst()) {
                int chkFavourite;
                if (movieCursor.getInt(MovieQuery.COL_MOVIE_FAVOURITE) == 0){
                    chkFavourite = 1;}
                else {
                    chkFavourite = 0;}
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{mMovieId.toString()};
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, chkFavourite);
                getActivity().getContentResolver().update(MovieContract.MovieEntry.buildUri(mMovieId), cv, selection, selectionArgs);

                if (chkFavourite == 0){
                    btnToggle.setText("Mark as Favorite");
                    Toast.makeText(getActivity(), title+ " is removed from Favorites", Toast.LENGTH_SHORT).show();
                }
                else {
                    btnToggle.setText("Favourite");
                    Toast.makeText(getActivity(), title+ " is added to Favorites", Toast.LENGTH_SHORT).show();}
            }
            movieCursor.close();
        }
        else{
            Toast.makeText(getActivity(), "Please Wait... still loading", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        long mid = args.getLong(sMovieIdKey);
        if (mid > 0L)
            switch (id) {
            case 0:
                return new CursorLoader(getActivity(), MovieContract.MovieEntry.buildUri(mid),
                        null, null, null, null);
            case 1:
                return new CursorLoader(getActivity(), MovieContract.MovieEntry.buildUriTrailers(mid),
                    null, null, null, null);
            case 2:
                return new CursorLoader(getActivity(), MovieContract.MovieEntry.buildUriReviews(mid),
                        null, null, null, null);
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
            case MovieProvider.MOVIE_WITH_ID:
                mMovieAdapter.swapCursor(data);
                Log.v(LOG_TAG, "In onLoadFinished Movie ");
                if (!movieDetailsModified.get()) {
                    if (data == null || !data.moveToFirst()) {
                        Toast.makeText(getContext(), "No Data Loaded. Please go back and select movie again", Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                        return;
                    }
                    mMovieData = SerializationUtils.deserialize(data.getBlob(1));
                    movieRowId=data.getLong(MovieQuery.COL_MOVIEID);
                    mMovieId = mMovieData.id;
                    title = mMovieData.original_title;
                    Log.v(LOG_TAG, "Movie Title = " + title);
                    ((TextView) rootView.findViewById(R.id.title_text)).setText(title);

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                    Picasso.with(getContext())
                            .load(String.format(sImgUrl, sImgSize, mMovieData.poster_path))
                            .error(R.drawable.abc_btn_rating_star_off_mtrl_alpha)
                                    //.fit()
                            .into(imageView);

                    if (data.getInt(3)<=0)
                        movieMinutesModified.set(false);
                    else {
                        movieMinutesModified.set(true);
                        ((TextView) rootView.findViewById(R.id.runtime_text))
                                .setText(Integer.toString(data.getInt(MovieQuery.COL_MOVIE_RUNTIME)) + "min");
                    }

                    String mMovieVoteAverage =  Double.toString(mMovieData.vote_average);

                    ((TextView) rootView.findViewById(R.id.voteaverage_text))
                            .setText(mMovieVoteAverage + "/10");
                    Float f = Float.parseFloat(mMovieVoteAverage);
                    ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(f);

                    ((TextView) rootView.findViewById(R.id.release_text))
                            .setText(Utility.getYear(mMovieData.release_date));

                    btnToggle = (Button) rootView.findViewById(R.id.chkState);
                    if (data.getInt(MovieQuery.COL_MOVIE_FAVOURITE) != 0)
                        btnToggle.setText("Favourite");
                    else
                        btnToggle.setText("Mark as Favourite");


                    ((TextView) rootView.findViewById(R.id.overview_text))
                            .setText(mMovieData.overview);

                    movieDetailsModified.set(true);
                    if(!movieMinutesModified.get())
                    {
                        updateMinutesDataOrAskServer(data);
                    }
                    Log.v(LOG_TAG, "Out of Load Finish Movie");
                }
                break;
            case MovieProvider.MOVIE_TRAILERS:
                Log.v(LOG_TAG,"In Trailer load finish loader");
                if (!trailerDataModified.get())
                    updateTrailerDataOrAskServer(data);
                Log.v(LOG_TAG,"out Trailer load finish loader");
                break;
            case MovieProvider.MOVIE_REVIEWS:
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
        byte[] bTrailer = data == null ? null : data.getBlob(1);
        if (bTrailer == null || bTrailer.length == 0) getVideoDataAsync();
        else
            handleTrailerResults((List<Map<String, String>>) SerializationUtils.deserialize(bTrailer));
    }

    private void updateReviewDataOrAskServer(Cursor data) {
        byte[] bReview = data == null ? null : data.getBlob(1);
        if (bReview == null || bReview.length == 0) getReviewDataAsync();
        else
            handleReviewResults((List<Map<String, String>>) SerializationUtils.deserialize(bReview));
    }

    @NonNull
    private void getMinutesDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sMinuteUrl, mMovieId)).buildUpon()
                .appendQueryParameter(sParamApi, BuildConfig.MOVIE_API_KEY)
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
                        Toast.makeText(getContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequestQueue.add(jsObjRequest);
    }

    private void getVideoDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sVideoUrl, mMovieId)).buildUpon()
                .appendQueryParameter(sParamApi, BuildConfig.MOVIE_API_KEY)
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
                        Toast.makeText(getContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequestQueue.add(jsObjRequest);
    }

    private void getReviewDataAsync() {
        blockUntilMovieIdSet();
        Uri builtUri = Uri.parse(String.format(sReviewKey, mMovieId)).buildUpon()
                .appendQueryParameter(sParamApi, BuildConfig.MOVIE_API_KEY)
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
                        Toast.makeText(getContext(), "Network is not Available", Toast.LENGTH_SHORT).show();
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
            String movie_title = mMovieData != null ? mMovieData.original_title : null;
            th.add(new TrailerData(youtube_key, trailer_title,movie_title));
        }
        mTrailerList.clear();
        mTrailerList.addAll(th);
        if (!trailerDataModified.get()) showTrailerUIAsync(mTrailerList);
        updateTrailerDataInternal((Serializable) results);
        trailerDataModified.set(true);
    }

    private void handleReviewResults(List<Map<String, String>> results) {
        Set<ReviewData> rev = new LinkedHashSet<>();
        for (Map<String, String> r : results) {
            String content = r.get("content");
            String author = r.get("author");
            String url = r.get("url");
            rev.add(new ReviewData(content, author, url));
        }
        mReviewList.clear();
        mReviewList.addAll(rev);
        if (!reviewDataModified.get()) showReviewUIAsync(mReviewList);
        updateReviewDataInternal((Serializable) results);
        reviewDataModified.set(true);
    }

    private void showTrailerUIAsync(List<TrailerData> mTrailerList){
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.trailer_linear);
        ll.removeAllViews();
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
        ll.removeAllViews();
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
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{mMovieId.toString()};
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES, Double.valueOf(minutes).intValue());
        getActivity().getContentResolver().update(MovieContract.MovieEntry.buildUri(mMovieId), cv, selection, selectionArgs);
}

    private void updateTrailerDataInternal(Serializable results) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{mMovieId.toString()};
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILERS, SerializationUtils.serialize(results));
        getActivity().getContentResolver().update(MovieContract.MovieEntry.buildUriTrailers(mMovieId), cv, selection, selectionArgs);
    }

    private void updateReviewDataInternal(Serializable results) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{mMovieId.toString()};
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS, SerializationUtils.serialize(results));
        getActivity().getContentResolver().update(MovieContract.MovieEntry.buildUriReviews(mMovieId), cv, selection, selectionArgs);
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
