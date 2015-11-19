package com.example.nitu.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.google.gson.internal.LinkedTreeMap;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by nitus on 10/17/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    private long curr_movie_id;
    private byte[] expected_reviews, expected_trailer;
    private int expected_mins;
    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

    }
    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }
    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*  This test checks to make sure that the content provider is registered correctly.
    */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*   This test doesn't touch the database.  It verifies that the ContentProvider returns
       the correct type for each type of URI that it can handle.
    */
    public void testGetType() {
        // content://com.example.android.sunshine.app/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Content Type",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        Map<Long, ContentValues> raw = TestUtilities.createSortedMovieValues(getContext(), "popular");
        Long movie_id = (Long) raw.values().toArray(new ContentValues[0])[0].get(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        // content://com.example.android.sunshine.app/movie/#
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildUri(movie_id));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Content Type", MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/movie/favorite
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildUri());
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
        assertEquals("Error: the MovieContract.MovieEntry CONTENT_URI with location and date should return MovieContract.MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

    }

    /*  This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic trailer query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicMovieQuery() {
        // insert our test records into the database

        Map<Long, ContentValues> listContentValues = TestUtilities.createSortedMovieValues(getContext(), "popular");
        Map<Long, Long> locationRowIds = TestUtilities.insertMovieRow(mContext, listContentValues);


        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateMovieCursor(movieCursor, listContentValues);
    }

    /*  This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testAddingPopularMoviesToTable() {
        mContext.getContentResolver().delete(MovieContract.PopularEntry.CONTENT_URI, null, null);
        Map<Long, ContentValues> listContentValues = TestUtilities.createSortedMovieValues(getContext(), "popular");
        ContentValues[] arr = (ContentValues[]) listContentValues.values().toArray(new ContentValues[0]);

        mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, arr);

        ContentValues[] movie_ids = new ContentValues[arr.length];
        for (int i = 0; i < arr.length; i++)
            (movie_ids[i] = new ContentValues()).put(MovieContract.PopularEntry.COLUMN_MOVIE_ID,
                    arr[i].getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

        mContext.getContentResolver().bulkInsert(MovieContract.PopularEntry.buildUri(), movie_ids);

        TestUtilities.verifyPopularValuesInDatabase(listContentValues, mContext);
    }

    public void testAddingRatedMoviesToTable() {
        mContext.getContentResolver().delete(MovieContract.RatingEntry.CONTENT_URI, null, null);
        Map<Long, ContentValues> listContentValues = TestUtilities.createSortedMovieValues(getContext(), "rating");

        ContentValues[] arr = (ContentValues[]) listContentValues.values().toArray(new ContentValues[0]);

        mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, arr);

        ContentValues[] movie_ids = new ContentValues[arr.length];
        for (int i = 0; i < arr.length; i++)
            (movie_ids[i] = new ContentValues()).put(MovieContract.RatingEntry.COLUMN_MOVIE_ID,
                    arr[i].getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

        mContext.getContentResolver().bulkInsert(MovieContract.RatingEntry.buildUri(), movie_ids);

        TestUtilities.verifyRatingValuesInDatabase(listContentValues, mContext);
    }

    public void testAddingReviewToMovieRow() {
        final TestDb testDb = new TestDb();
        TestUtilities.insertMovies(testDb, mContext);
        testAddingPopularMoviesToTable(); // add the popular movies
        LinkedTreeMap<String, Serializable> listContentValues = TestUtilities.getDataAsMap(getContext(), "review");
        curr_movie_id = Double.valueOf(listContentValues.get("id").toString()).longValue();
        Serializable reviews = listContentValues.get("results");
        expected_reviews = SerializationUtils.serialize(reviews);
        assertTrue(expected_reviews.length > 0);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS, expected_reviews);
        Uri uri = MovieContract.MovieEntry.buildUriReviews(curr_movie_id);
        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Long.valueOf(curr_movie_id).toString()};
        assertNotNull(mContext.getContentResolver().update(uri, cv, selection, selectionArgs));
    }

    public void testGettingReviews() {
        testAddingReviewToMovieRow();
        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.buildUriReviews(curr_movie_id),
                null, null, null, null);
        assertTrue("cursor returned for movie id = " + curr_movie_id, c.moveToFirst());
        byte[] blob = c.getBlob(1);
        assertNotNull("ensure object returned", blob);
        assertEquals(expected_reviews.length, blob.length);
        for (int i = 0; i < expected_reviews.length; i++)
            assertEquals(expected_reviews[i], blob[i]);
        c.close();
    }

    public void testAddingTrailersToMovieRow() {
        final TestDb testDb = new TestDb();
        TestUtilities.insertMovies(testDb, mContext);
        testAddingPopularMoviesToTable(); // add the popular movies
        LinkedTreeMap<String, Serializable> listContentValues = TestUtilities.getDataAsMap(getContext(), "trailer");
        curr_movie_id = Double.valueOf(listContentValues.get("id").toString()).longValue();
        Serializable trailers = listContentValues.get("results");
        expected_trailer = SerializationUtils.serialize(trailers);
        assertTrue(expected_trailer.length > 0);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILERS, expected_trailer);
        Uri uri = MovieContract.MovieEntry.buildUriTrailers(curr_movie_id);
        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Long.valueOf(curr_movie_id).toString()};
        assertNotNull(mContext.getContentResolver().update(uri, cv, selection, selectionArgs));
    }

    public void testGettingTrailers() {
        testAddingTrailersToMovieRow();
        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.buildUriTrailers(curr_movie_id),
                null, null, null, null);
        assertTrue("cursor returned for movie id = " + curr_movie_id, c.moveToFirst());
        byte[] blob = c.getBlob(1);
        assertNotNull("ensure object returned", blob);
        assertEquals(expected_trailer.length, blob.length);
        for (int i = 0; i < expected_trailer.length; i++)
            assertEquals(expected_trailer[i], blob[i]);
        c.close();
    }

    public void testAddingMinutesToMovieRow() {
        final TestDb testDb = new TestDb();
        TestUtilities.insertMovies(testDb, mContext);
        testAddingPopularMoviesToTable(); // add the popular movies
        LinkedTreeMap<String, Serializable> listContentValues = TestUtilities.getDataAsMap(getContext(), "minute");
        curr_movie_id = Double.valueOf(listContentValues.get("id").toString()).longValue();
        expected_mins = Double.valueOf(listContentValues.get("runtime").toString()).intValue();
        assertTrue(expected_mins > 0);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES, expected_mins);
        Uri uri = MovieContract.MovieEntry.buildUri(curr_movie_id);
        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Long.valueOf(curr_movie_id).toString()};
        assertNotNull(mContext.getContentResolver().update(uri, cv, selection, selectionArgs));
    }

    public void testGettingMinutes() {
        testAddingMinutesToMovieRow();
        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.buildUri(curr_movie_id),
                null, null, null, null);
        assertTrue(c.moveToFirst());
        int mins = c.getInt(3);
        assertEquals(expected_mins, mins);
        c.close();
    }
}
