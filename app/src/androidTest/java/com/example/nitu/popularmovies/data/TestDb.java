package com.example.nitu.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without tables",tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_KEY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVOURITE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        //movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }


    public long testMovieTable() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues(null);

        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,testValues);
        assertTrue(movieRowId != -1);
        // Query the database and receive a Cursor back
        Cursor c = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from movie query", c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", c, testValues);
        // query if you like)
        assertFalse("Error: More than one record returned from movie query", c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
        return movieRowId;
    }


    public void testTrailerTable() {
        long movieRowId = testMovieTable();
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createTrailerValues(TestUtilities.TEST_MOVIE);
        // Insert ContentValues into database and get a row ID back
        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,testValues);
        assertTrue(trailerRowId != -1);
        // Query the database and receive a Cursor back
        Cursor c = db.query(
                MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from trailer query", c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: trailer Query Validation Failed", c, testValues);
        assertFalse("Error: More than one record returned from trailer query", c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    public void testReviewTable() {
        long movieRowId = testMovieTable();
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createReviewValues(TestUtilities.TEST_MOVIE);
        // Insert ContentValues into database and get a row ID back
        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME,null,testValues);
        assertTrue(reviewRowId != -1);
        // Query the database and receive a Cursor back
        Cursor c = db.query(
                MovieContract.ReviewEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from review query", c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: review Query Validation Failed", c, testValues);
        assertFalse("Error: More than one record returned from review query", c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }



    public long insertMovie() {
        return -1L;
    }
}
