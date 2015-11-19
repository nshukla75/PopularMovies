package com.example.nitu.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.RatingEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.PopularEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertTrue("Database should be open.", db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without all required tables.",
                tableNameHashSet.isEmpty());
        c.close();
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_BLOB);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
        c.close();
    }

    public void testMovieTable() {
        TestUtilities.insertMovies(this, mContext);
    }

    void insertMovieValues(SQLiteDatabase db, List<ContentValues> lTestValues, Map<Long, ContentValues> insertOrderedTestValues, String sort) {
        int insertCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues cv : lTestValues) {
                long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
                ContentValues cv0 = new ContentValues();
                cv0.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, cv.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                String subTable = StringUtils.containsIgnoreCase("popular", sort) ? MovieContract.PopularEntry.TABLE_NAME : MovieContract.RatingEntry.TABLE_NAME;
                db.insert(subTable, null, cv0);
                // Verify we got a row back.
                assertFalse("Insert failed!", -1L == movieRowId);
                insertOrderedTestValues.put(movieRowId, cv);
                assertEquals("InsertCount match RowId", ++insertCount, movieRowId);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
