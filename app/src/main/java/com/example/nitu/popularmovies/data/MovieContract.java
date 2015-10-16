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
    public static final String CONTENT_AUTHORITY = "com.example.nitu.popularmovies.data";
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

        //public static final String COLUMN_MOV_KEY = "movie_id";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_POPULARITY="popularity";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_FAVOURITE="favourite";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title" ;
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*public static long getReleaseDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_RELEASE_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }


        //get movie based on popularity
        public static String getPopularMoviesFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        //get movie based on rating
        public static String getTopRatedMoviesFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
        //get movie based on Favourite
        public static String getFavouriteMoviesFromUri(Uri uri) {
            return uri.getPathSegments().get(4);
        }*/

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
        public static final String COLUMN_MOV_KEY = "movie_id";
        //id from API
        public static final String COLUMN_TRAILER_ID = "trailerid";
        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_SIZE = "size";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerMovie(String movieSetting) {
            return CONTENT_URI.buildUpon().appendPath(movieSetting).build();
        }

        public static String getMovieSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the Review table */
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the Movie table.
        public static final String COLUMN_MOV_KEY = "movie_id";
        public static final String COLUMN_REVIEW_ID = "reviewid";
        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";


        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewMovie(String movieSetting) {
            return CONTENT_URI.buildUpon().appendPath(movieSetting).build();
        }

        public static String getMovieSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
