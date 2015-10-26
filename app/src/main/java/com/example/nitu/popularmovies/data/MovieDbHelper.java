package com.example.nitu.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nitu.popularmovies.data.MovieContract.MovieEntry;
import com.example.nitu.popularmovies.data.MovieContract.TrailerEntry;
import com.example.nitu.popularmovies.data.MovieContract.ReviewEntry;
/**
 * Created by nitus on 10/14/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the movie entry associated with this trailer data
                TrailerEntry.COLUMN_MOV_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT UNIQUE NOT NULL, " +
                TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +

                // Set up the movie column as a foreign key to movie table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOV_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_KEY + ")); ";

                //, " +

                // To assure the application dont have duplicate trailer entry
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                //" UNIQUE (" + TrailerEntry.COLUMN_TRAILER_KEY + ", " +
                //TrailerEntry.COLUMN_MOV_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the movie entry associated with this trailer data
                ReviewEntry.COLUMN_MOV_KEY + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_KEY + " TEXT UNIQUE NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                // Set up the movie column as a foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOV_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_KEY + " ));";//, " +

                // To assure the application dont have duplicate trailer entry
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                //" UNIQUE (" + ReviewEntry.COLUMN_REVIEW_KEY + ", " +
                //ReviewEntry.COLUMN_MOV_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_KEY + " TEXT UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_FAVOURITE + " INTEGER NOT NULL CHECK( " + MovieEntry.COLUMN_FAVOURITE + " IN (0,1))," +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " BLOB NULL );"; /*, UNIQUE (" + MovieEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";*/


        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
