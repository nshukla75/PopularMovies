package com.example.nitu.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/16/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    // content://com.example.nitu.popularmovies/movie/0
    private static final Uri TEST_MOVIE_ITEM = MovieContract.MovieEntry.buildUri(0L);
    // content://com.example.nitu.popularmovies/movie
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.buildUri();
    // content://com.example.nitu.popularmovies/movie/favorite
    private static final Uri TEST_FAVORITE_DIR = MovieContract.MovieEntry.buildUriUnionFavorite();

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_ITEM), MovieProvider.MOVIE_WITH_ID);
        assertEquals("Error: The Movie URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The Favorite URI was matched incorrectly.",
                testMatcher.match(TEST_FAVORITE_DIR), MovieProvider.MOVIE_FAVORITE);
    }
}
