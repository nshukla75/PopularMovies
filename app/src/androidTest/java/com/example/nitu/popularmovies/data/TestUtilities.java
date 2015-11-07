package com.example.nitu.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.nitu.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_MOVIE = "135397";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue()== null ? null:entry.getValue().toString();
            assertEquals("Value '" + expectedValue +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createReviewValues(String movieKey) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOV_KEY, movieKey);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_KEY, "55910381c3a36807f900065d");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "jonlikesmoviesthatdontsuck");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Overall action packed movie... But there should be more puzzles in the climax... But I really love the movie....Excellent...");
        return reviewValues;
    }

    static ContentValues createTrailerValues(String movieKey) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOV_KEY, movieKey);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, "5576eac192514111e4001b03");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "lP-sUUUfamw");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, 720);
        return trailerValues;
    }

    static ContentValues createMovieValues(byte[] image) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, "135397");
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 46.6);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.1);
        testValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
        testValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,"Jurassic World");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,TEST_DATE);
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,"http://image.tmdb.org/t/p/w185/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, image);

        return testValues;
    }

    static long insertMovieValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues(null);

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
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
