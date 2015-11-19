package com.example.nitu.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nitus on 10/14/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";
    private final String SQL_CREATE_MOVIE_TABLE;
    private final String SQL_CREATE_POPULAR_TABLE;
    private final String SQL_CREATE_RATING_TABLE;

    public MovieDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_BLOB + " BLOB NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TRAILERS + " BLOB, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS + " BLOB, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_FAVOURITE + " INTEGER CHECK( " + MovieContract.MovieEntry.COLUMN_FAVOURITE + " IN (0,1))," +
                " UNIQUE (" + MovieContract.MovieEntry._ID + ") ON CONFLICT REPLACE);";

        SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + MovieContract.PopularEntry.TABLE_NAME + " (" +
                MovieContract.PopularEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.PopularEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.PopularEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.PopularEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + MovieContract.PopularEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        SQL_CREATE_RATING_TABLE = "CREATE TABLE " + MovieContract.RatingEntry.TABLE_NAME + " (" +
                MovieContract.RatingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.RatingEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.RatingEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.RatingEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + MovieContract.RatingEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createMovieTable(db);
            createPopularTable(db);
            createRatingTable(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void createMovieTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    public void createPopularTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POPULAR_TABLE);
    }

    public void createRatingTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RATING_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.PopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.RatingEntry.TABLE_NAME);
        onCreate(db);
    }
}
