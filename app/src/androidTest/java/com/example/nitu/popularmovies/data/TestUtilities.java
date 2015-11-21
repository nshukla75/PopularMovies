package com.example.nitu.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.model.MovieData;
import com.example.nitu.popularmovies.utils.PollingCheck;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestUtilities extends AndroidTestCase {
    static void validateRecordsToDatabase(String error, Cursor valueCursor, Map<Long, ContentValues> expectedValues) {
        while (valueCursor.moveToNext()) {
            long _id = valueCursor.getLong(0);
            Long movie_id = valueCursor.getLong(1);
            byte[] bMovieObj = valueCursor.getBlob(2);
            MovieData movieData = (MovieData) SerializationUtils.deserialize(bMovieObj);

            ContentValues cv = expectedValues.get(_id);
            Long expectedId = (Long) cv.get(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            byte[] expectedBMovieObject = (byte[]) cv.get(MovieContract.MovieEntry.COLUMN_MOVIE_BLOB);
            MovieData expectedMovieData = (MovieData) SerializationUtils.deserialize(expectedBMovieObject);

            assertEquals("movied id's don't match", expectedId, movie_id);
            assertEquals("movie objects don't match", expectedMovieData, movieData);
            assertTrue("binary movie objects don't match", Arrays.equals(expectedBMovieObject, bMovieObj));
        }
    }
    static void validateMovieCursor(Cursor valueCursor, Map<Long, ContentValues> expectedValues) {
        assertTrue("Empty cursor returned. ", valueCursor.moveToFirst());
        validateMovieCurrentRecord(valueCursor, expectedValues);
        valueCursor.close();
    }
    static void validateMovieCurrentRecord(Cursor valueCursor, Map<Long, ContentValues> expectedValue) {
        while (valueCursor.moveToNext()) {
            long _id = valueCursor.getLong(0);
            Long movie_id = valueCursor.getLong(1);
            byte[] blob = valueCursor.getBlob(2);

            ContentValues expect = expectedValue.get(movie_id);

            assertEquals("movied ids don't match", expect.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID), movie_id);
            assertTrue("Binaries don't match", Arrays.equals(expect.getAsByteArray(MovieContract.MovieEntry.COLUMN_MOVIE_BLOB), blob));
        }
    }
    static Map<Long, ContentValues> createSortedMovieValues(Context c, String sort) {
        // Create a new map of values, where column names are the keys
        Map<Long, ContentValues> testValues = new HashMap<>();
        LinkedTreeMap<String, Serializable> map = getDataAsMap(c, sort);
        List<MovieData> lMovies = Utility.covertMapToMovieDataList(map);
        long dbRowId = 0;
        for (MovieData m : lMovies) {
            ContentValues cv = createMovieContentValue(dbRowId++, m);
            testValues.put(cv.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID), cv);
        }

        return testValues;
    }
    public static LinkedTreeMap<String, Serializable> getDataAsMap(Context c, String sort) {
        InputStream in = null;

        switch (sort) {
            case "popular":
                in = c.getResources().openRawResource(R.raw.popular);
                break;
            case "rating":
                in = c.getResources().openRawResource(R.raw.rating);
                break;
            case "minute":
                in = c.getResources().openRawResource(R.raw.minute);
                break;
            case "trailer":
                in = c.getResources().openRawResource(R.raw.videos);
                break;
            case "review":
                in = c.getResources().openRawResource(R.raw.review);
                break;
            default:
                throw new RuntimeException("Invalid sort: " + sort);
        }
        String json = Utility.readStreamToString(in);
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Utility.getGson().fromJson(json, LinkedTreeMap.class);
    }
    @NonNull
    public static ContentValues createMovieContentValue(long dbRowId, MovieData m) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, m.id.longValue());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_BLOB, SerializationUtils.serialize(m));
        return cv;
    }
    public static Map<Long, Long> insertMovieRow(Context context, Map<Long, ContentValues> cvs) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map<Long, Long> rowIds = new HashMap<>();
        try {
            for (Map.Entry<Long, ContentValues> cv : cvs.entrySet()) {
                long locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv.getValue());

                // Verify we got a row back.
                assertTrue("Error: Failure to insert value", locationRowId != -1L);
                rowIds.put(locationRowId, cv.getKey());
            }
        } finally {
            db.close();
        }
        return rowIds;
    }
    public static void verifyPopularValuesInDatabase(Map<Long, ContentValues> listContentValues, Context mContext) {
        Cursor c = mContext.getContentResolver().query(MovieContract.PopularEntry.CONTENT_URI, null, null, null, null);
        assertTrue("Nothing returned from Popular Table", c.moveToFirst());
        assertEquals("Size of list content values = number of rows returned", listContentValues.size(), c.getCount());
        int count = 1;
        while (c.moveToNext()) {
            long _id = c.getLong(0);
            byte[] bMovieObj = c.getBlob(1);
            MovieData movieData = (MovieData) SerializationUtils.deserialize(bMovieObj);
            bMovieObj = null; // force GC.
            long movie_id = movieData.id;

            assertNotNull("list of content values contains movie_id = " + movie_id, listContentValues.get(movie_id));
            assertTrue("count went too high", count < listContentValues.size());
            count++;
        }
        assertEquals("count exact", count, listContentValues.size());
        c.close();
    }
    public static void verifyRatingValuesInDatabase(Map<Long, ContentValues> listContentValues, Context mContext) {
        Cursor c = mContext.getContentResolver().query(MovieContract.RatingEntry.CONTENT_URI, null, null, null, null);
        assertTrue("Nothing returned from Rating Table", c.moveToFirst());
        assertEquals("Size of list content values = number of rows returned", listContentValues.size(), c.getCount());
        int count = 1;
        while (c.moveToNext()) {
            long _id = c.getLong(0);
            byte[] bMovieObj = c.getBlob(1);
            MovieData movieData = (MovieData) SerializationUtils.deserialize(bMovieObj);
            bMovieObj = null; // force GC.
            long movie_id = movieData.id;

            assertNotNull("list of content values contains movie_id = " + movie_id, listContentValues.get(movie_id));
            assertTrue("count went too high", count < listContentValues.size());
            count++;
        }
        assertEquals("count exact", count, listContentValues.size());
        c.close();
    }
    public static Map<Long, ContentValues> insertMovies(TestDb testDb, Context mContext) {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createSortedMovieValues if you wish)

        Map<Long, ContentValues> testValues = createSortedMovieValues(mContext, "popular");
        List<ContentValues> lTestValues = Arrays.asList(testValues.values().toArray(new ContentValues[0]));
        Map<Long, ContentValues> insertOrderedTestValues = new HashMap<>();
        // Third Step: Insert ContentValues into database and get a row ID back

        testDb.insertMovieValues(db, lTestValues, insertOrderedTestValues, "popular");

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateRecordsToDatabase function in TestUtilities to validate the
        // query if you like)
        validateRecordsToDatabase("Error: Location Query Validation Failed",
                cursor, insertOrderedTestValues);

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return insertOrderedTestValues;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the menu_main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
