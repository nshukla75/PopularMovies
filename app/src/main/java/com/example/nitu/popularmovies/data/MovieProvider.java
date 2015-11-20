package com.example.nitu.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.security.InvalidParameterException;

/**
 * Created by nitus on 10/15/2015.
 */
public class MovieProvider extends ContentProvider {
    Context context;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = "MovieProvider";
    private MovieDbHelper mOpenHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 101;
   // public static final int MOVIE_WITH_ID_AND_MAYBE_FAVORITE = 102;
    //public static final int MOVIE_MINUTES = 103;
    public static final int MOVIE_REVIEWS = 104;
    public static final int MOVIE_TRAILERS = 105;
    public static final int MOVIE_FAVORITE = 200;
    //public static final int MOVIE_FAVORITE_WITH_ID = 201;
    public static final int MOVIE_RATING = 300;
    public static final int MOVIE_POPULAR = 400;

    private static final SQLiteQueryBuilder sMoviesAll;
    private static final SQLiteQueryBuilder sMovieById;
    private static final SQLiteQueryBuilder sFavoriteMovies;
    private static final SQLiteQueryBuilder sMovieReviews;
    private static final SQLiteQueryBuilder sMovieMinutes;
    private static final SQLiteQueryBuilder sMovieTrailers;

    static {
        sMoviesAll = new SQLiteQueryBuilder();
        sMovieById = new SQLiteQueryBuilder();
        sMovieReviews = new SQLiteQueryBuilder();
        sMovieMinutes = new SQLiteQueryBuilder();
        sMovieTrailers = new SQLiteQueryBuilder();
        sFavoriteMovies = new SQLiteQueryBuilder();


        sMoviesAll.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieTrailers.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieReviews.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieMinutes.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sFavoriteMovies.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieById.setTables(MovieContract.MovieEntry.TABLE_NAME);
    }

    private static final String sMovieSelection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
    private Cursor getAllMovies() {
        return sMoviesAll.query(mOpenHelper.getReadableDatabase(), null, null, null, null, null, null);
    }

    private Cursor getPopularMovies() {
        return mOpenHelper.getReadableDatabase().rawQuery("SELECT "
                + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " AS _id, "
                + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_BLOB
                + " FROM " + MovieContract.MovieEntry.TABLE_NAME
                + " WHERE " + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                + " IN (SELECT "
                + MovieContract.PopularEntry.TABLE_NAME + "." + MovieContract.PopularEntry.COLUMN_MOVIE_ID
                + " FROM " + MovieContract.PopularEntry.TABLE_NAME
                + " WHERE 1 ORDER BY "
                + MovieContract.PopularEntry.TABLE_NAME + "." + MovieContract.PopularEntry._ID + " desc)", null);

    }

    private Cursor getRatedMovies() {
        return mOpenHelper.getReadableDatabase().rawQuery("SELECT "
                + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " AS _id, "
                + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_BLOB
                + " FROM " + MovieContract.MovieEntry.TABLE_NAME
                + " WHERE " + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                + " IN (SELECT "
                + MovieContract.RatingEntry.TABLE_NAME + "." + MovieContract.RatingEntry.COLUMN_MOVIE_ID
                + " FROM " + MovieContract.RatingEntry.TABLE_NAME
                + " WHERE 1 ORDER BY "
                + MovieContract.RatingEntry.TABLE_NAME + "." + MovieContract.RatingEntry._ID + " desc)", null);
    }

    private Cursor getMovieTrailers(String movie_id) {
        return sMovieTrailers.query(mOpenHelper.getReadableDatabase(),
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_TRAILERS},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movie_id},
                null, null, null);
    }

    private Cursor getMovieReviews(String movie_id) {
        return sMovieReviews.query(mOpenHelper.getReadableDatabase(),
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movie_id},
                null, null, null);
    }

    private Cursor getMovieMinutes(String movie_id) {
        return sMovieMinutes.query(mOpenHelper.getReadableDatabase(),
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movie_id},
                null, null, null);
    }

    private Cursor getMovieById(String movie_id) {
        return sMovieById.query(mOpenHelper.getReadableDatabase(),
                new String[]{MovieContract.MovieEntry._ID,MovieContract.MovieEntry.COLUMN_MOVIE_BLOB,MovieContract.MovieEntry.COLUMN_FAVOURITE,MovieContract.MovieEntry.COLUMN_MOVIE_MINUTES},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movie_id},
                null, null, null);
    }

    private Cursor getFavoriteMovies() {
        int yes = 1;
        return sFavoriteMovies.query(mOpenHelper.getReadableDatabase(),
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_BLOB},
                MovieContract.MovieEntry.COLUMN_FAVOURITE + "=?",
                new String[]{Integer.toString(yes)},
                null, null, null);
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        // Get all movies
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        // Get a movie by id
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
       // Get a movie's review(s)
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/review/#", MOVIE_REVIEWS);
        // Get a movie's trailer(s)
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/trailer/#", MOVIE_TRAILERS);
       // Get all movies marked favorite (should not be limited)
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorite", MOVIE_FAVORITE);
        // Get a favorite movie
        //matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorite/#", MOVIE_FAVORITE_WITH_ID);
        // Get all movies marked as highest rated (should be limited to 20)
        matcher.addURI(authority, MovieContract.PATH_RATING, MOVIE_RATING);
        // Get all movies marked as most popular (should be limited to 20)
        matcher.addURI(authority, MovieContract.PATH_POPULAR, MOVIE_POPULAR);

        return matcher;
    }

    public MovieProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_WITH_ID:
            //case MOVIE_MINUTES:
            case MOVIE_TRAILERS:
            case MOVIE_REVIEWS:
            //case MOVIE_FAVORITE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_RATING:
                return MovieContract.RatingEntry.CONTENT_TYPE;
            case MOVIE_POPULAR:
                return MovieContract.PopularEntry.CONTENT_TYPE;
            default:
                throw new InvalidParameterException("Unknown uri/match: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = getAllMovies();
                break;
            case MOVIE_WITH_ID:
                retCursor = getMovieById(uri.getLastPathSegment());
                break;
            /*case MOVIE_MINUTES:
                retCursor = getMovieMinutes(uri.getLastPathSegment());
                break;*/
            case MOVIE_REVIEWS:
                retCursor = getMovieReviews(uri.getLastPathSegment());
                break;
            case MOVIE_TRAILERS:
                retCursor = getMovieTrailers(uri.getLastPathSegment());
                break;
            case MOVIE_RATING:
                retCursor = getRatedMovies();
                break;
            case MOVIE_FAVORITE:
                retCursor = getFavoriteMovies();
                break;
           /* case MOVIE_FAVORITE_WITH_ID:
                retCursor = getFavoriteMovieId(uri.getLastPathSegment());
                break;*/
            case MOVIE_POPULAR:
                retCursor = getPopularMovies();
                break;
            default:
                throw new UnsupportedOperationException("Unknown/Unimplemented uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
    private long insertRated(ContentValues values) {
        try {
            return mOpenHelper.getWritableDatabase().insertOrThrow(MovieContract.RatingEntry.TABLE_NAME, null, values);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            return -2L;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    private long insertPopular(ContentValues values) {
        try {
            return mOpenHelper.getWritableDatabase().insertOrThrow(MovieContract.PopularEntry.TABLE_NAME, null, values);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e(LOG_TAG, String.format("Did not insert %d because of constraint (already exists)", values.getAsLong(MovieContract.PopularEntry.COLUMN_MOVIE_ID)));
            return -2L;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    private long insertMovie(ContentValues values) {
        try {
            return mOpenHelper.getWritableDatabase().insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            return -2L;
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long _id;
        Long value_mid = values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        Uri returnUri;
        switch (match) {
            case MOVIE:
                _id = insertMovie(values);
                returnUri = MovieContract.MovieEntry.buildUri();
                break;
            case MOVIE_WITH_ID:
                _id = insertMovie(values);
                returnUri = MovieContract.MovieEntry.buildUri(values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                break;
            case MOVIE_RATING:
                _id = insertRated(values);
                returnUri = MovieContract.RatingEntry.buildUri();
                break;
            case MOVIE_POPULAR:
                _id = insertPopular(values);
                returnUri = MovieContract.PopularEntry.buildUri();
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unknown/Unimplemented match (%d) & uri (%s)", match, uri));
        }
        if (_id == -1)
            throw new android.database.SQLException(String.format("Failed to insert values (%s) with uri (%s)", values, uri));
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int retVal = super.bulkInsert(uri, values);
            if (retVal > 0) db.setTransactionSuccessful();
            return retVal;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_POPULAR:
                rowsDeleted = db.delete(MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_RATING:
                rowsDeleted = db.delete(MovieContract.RatingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("Unknown/Unimplemented match :: uri: %d / %s", match, uri));
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rows;
        // long value_mid = values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        switch (match) {
            case MOVIE_REVIEWS:
            case MOVIE_TRAILERS:
            case MOVIE_WITH_ID:
            /*case MOVIE_MINUTES:
            case MOVIE_FAVORITE_WITH_ID:*/
                rows = mOpenHelper.getWritableDatabase().update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unknown/Unimplemented match (%d) & uri (%s)", match, uri));
        }
        if (rows == -1)
            throw new RuntimeException("Failed insert of values: " + values);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
