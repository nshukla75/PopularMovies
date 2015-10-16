package com.example.nitu.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.Date;
import java.util.Calendar;

/**
 * Created by nitus on 10/15/2015.
 */
public class MovieProvider extends ContentProvider {
    Context context;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int TRAILER = 100;
    static final int TRAILER_WITH_MOVIE = 101;
    static final int MOVIE = 300;
    static final int MOVIE_BY_POPULARITY = 301;
    static final int MOVIE_BY_RATING = 302;
    static final int MOVIE_BY_FAVOURITE = 303;
    static final int MOVIE_BY_COMINGSOON = 304;
    static final int MOVIE_BY_PLAYINGNOW = 305;

    static final int REVIEW = 200;
    static final int REVIEW_WITH_MOVIE = 201;

    private static final SQLiteQueryBuilder sTrailerByMovieSettingQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieSettingQueryBuilder;

    static{
        sTrailerByMovieSettingQueryBuilder = new SQLiteQueryBuilder();
        sReviewByMovieSettingQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sTrailerByMovieSettingQueryBuilder.setTables(
            MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
            MovieContract.MovieEntry.TABLE_NAME +
            " ON " + MovieContract.TrailerEntry.TABLE_NAME +
            "." + MovieContract.TrailerEntry.COLUMN_MOV_KEY +
            " = " + MovieContract.MovieEntry.TABLE_NAME +
            "." + MovieContract.MovieEntry._ID);

        sReviewByMovieSettingQueryBuilder.setTables(
            MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
            MovieContract.MovieEntry.TABLE_NAME +
            " ON " + MovieContract.ReviewEntry.TABLE_NAME +
            "." + MovieContract.ReviewEntry.COLUMN_MOV_KEY +
            " = " + MovieContract.MovieEntry.TABLE_NAME +
          "." + MovieContract.MovieEntry._ID);
    }

    //movie.movieid = ?
    private static final String sMovieSettingSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    //movie.releaseDate >= ? AND movie.releaseDate <= ?
    private static final String sPlayingNowWithReleaseDateSelection =
        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " >= ? AND " +
        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " <= ? ";

    //movie.releaseDate >= ?
    private static final String sComingSoonWithReleaseDateSelection =
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " >= ? " ;

    //movie.releaseDate >= ?
    private static final String sFavouriteSelection =
            MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ? " ;

    private Cursor getFavouriteMovie(Uri uri, String[] projection, String sortOrder) {
        int yes = 1;
        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{Integer.toString(yes)};
        selection = sFavouriteSelection;

        return sTrailerByMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getPlayingNowMovie(Uri uri, String[] projection, String sortOrder) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, -7);
        String startDate = c.getTime().toString();
        c.setTime(new Date());
        c.add(Calendar.DATE,7);
        String endDate = c.getTime().toString();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{startDate, endDate};
        selection = sPlayingNowWithReleaseDateSelection;

        return sTrailerByMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getComingSoonMovie(Uri uri, String[] projection, String sortOrder) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE,7);
        String endDate = c.getTime().toString();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{endDate};
        selection = sComingSoonWithReleaseDateSelection;

        return sTrailerByMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getTrailerByMovieSetting(Uri uri, String[] projection, String sortOrder) {
        String movieSetting = MovieContract.TrailerEntry.getMovieSettingFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieSettingSelection;
        selectionArgs = new String[]{movieSetting};

        return sTrailerByMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewByMovieSetting(Uri uri, String[] projection, String sortOrder) {
        String movieSetting = MovieContract.ReviewEntry.getMovieSettingFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieSettingSelection;
        selectionArgs = new String[]{movieSetting};

        return sReviewByMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority,  MovieContract.PATH_TRAILER + "/*", TRAILER_WITH_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_BY_POPULARITY);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_BY_RATING);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_BY_FAVOURITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_BY_COMINGSOON);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_BY_PLAYINGNOW);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority,  MovieContract.PATH_REVIEW + "/*", REVIEW_WITH_MOVIE);
        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        context=getContext();
        mOpenHelper = new MovieDbHelper(context);
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case REVIEW_WITH_MOVIE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_BY_POPULARITY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_RATING:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_FAVOURITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_COMINGSOON:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_PLAYINGNOW:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "review/*"
            case REVIEW_WITH_MOVIE: {
                retCursor = getReviewByMovieSetting(uri, projection, sortOrder);
                break;
            }
            // "review"
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "trailer/*"
            case TRAILER_WITH_MOVIE: {
                retCursor = getTrailerByMovieSetting(uri, projection, sortOrder);
                break;
            }
            // "trailer"
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case MOVIE_BY_POPULARITY: {
                sortOrder= MovieContract.MovieEntry.COLUMN_POPULARITY + " [DESC]";
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case MOVIE_BY_RATING: {
                sortOrder= MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " [DESC]";
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case MOVIE_BY_FAVOURITE: {
                retCursor =  getFavouriteMovie(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE_BY_COMINGSOON: {
                retCursor =  getComingSoonMovie(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE_BY_PLAYINGNOW: {
                retCursor =  getPlayingNowMovie(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRAILER: {
                normalizeDate(values);
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                normalizeDate(values);
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        context.getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case TRAILER: {
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIE: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0){
            context.getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TRAILER: {
                normalizeDate(values);
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values,selection, selectionArgs);
                break;
            }
            case REVIEW: {
                normalizeDate(values);
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values,selection, selectionArgs);
                break;
            }
            case MOVIE: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }if (rowsUpdated !=0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAILER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                int returnCount1 = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount1++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount1;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}