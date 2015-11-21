package com.example.nitu.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by nitus on 10/15/2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.nitu.popularmovies";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_RATING = "rating";
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = PATH_MOVIE;

        // _ID - Integer
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_BLOB = "details_serializedParseableJson";
        public static final String COLUMN_MOVIE_TRAILERS = "trailers_serializedParseableJson";
        public static final String COLUMN_MOVIE_REVIEWS = "reviews_serializedParseableJson";
        public static final String COLUMN_MOVIE_MINUTES = "runtime";
        public static final String COLUMN_FAVOURITE="favorite";

        public static Uri buildUri() {
            return CONTENT_URI;
        }

        public static Uri buildUri(Long movie_id) {
            return CONTENT_URI.buildUpon().appendPath(movie_id.toString()).build();
        }

        public static Uri buildUriReviews(Long movie_id) {
            return CONTENT_URI.buildUpon().appendPath("review").appendPath(movie_id.toString()).build();
        }

        public static Uri buildUriTrailers(Long movie_id) {
            return CONTENT_URI.buildUpon().appendPath("trailer").appendPath(movie_id.toString()).build();
        }

        public static Uri buildUriUnionFavorite() {
            return CONTENT_URI.buildUpon().appendPath("favorite").build();
        }
    }

    public static final class PopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = PATH_POPULAR;
        public static final String COLUMN_MOVIE_ID = MovieEntry.COLUMN_MOVIE_ID;

        public static Uri buildUri() {
            return CONTENT_URI;
        }
    }

    public static final class RatingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;

        public static final String TABLE_NAME = PATH_RATING;
        public static final String COLUMN_MOVIE_ID = MovieEntry.COLUMN_MOVIE_ID;

        public static Uri buildUri() {
            return CONTENT_URI;
        }

    }
}
