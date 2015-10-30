package com.example.nitu.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.sql.Blob;

/**
 * Created by nitus on 10/15/2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.nitu.popularmovies";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_MOVIE = "movie";


    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String TABLE_NAME = "movie";

        // _ID - Integer
        public static final String COLUMN_MOVIE_KEY = "moviekey";
        public static final String COLUMN_POPULARITY="popularity";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_FAVOURITE="favourite";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title" ;
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POSTER = "poster";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovie(String movieKey) {
            return CONTENT_URI.buildUpon().appendPath(movieKey).build();
        }
        public static String getMovieSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static Uri buildPopularMovie() {
            return CONTENT_URI.buildUpon().appendPath("popularity").build();
        }
        public static Uri buildTopratedMovie() {
            return CONTENT_URI.buildUpon().appendPath("rating").build();
        }
        public static Uri buildFavouriteMovie() {
            return CONTENT_URI.buildUpon().appendPath("favourite").build();
        }
        public static Uri buildTrailerMovie(String movieKey) {
            final String trailerUri= CONTENT_URI +"/"+ movieKey +"/trailer";
            Uri returnUri = Uri.parse(trailerUri);
            return returnUri;
        }
        public static Uri buildReviewMovie(String movieKey) {
            final String reviewUri= CONTENT_URI +"/"+ movieKey +"/review";
            return Uri.parse(reviewUri).buildUpon().build();
        }
    }

    /* Inner class that defines the table contents of the Trailer table */
    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;


        public static final String TABLE_NAME = "trailer";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOV_KEY = "movieId";
        //id from API
        public static final String COLUMN_TRAILER_KEY = "trailerid";

        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_SIZE = "size";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }
    }

    /* Inner class that defines the table contents of the Review table */
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW ;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the Movie table.
        public static final String COLUMN_MOV_KEY = "movieId";
        public static final String COLUMN_REVIEW_KEY = "reviewid";
        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";


        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
