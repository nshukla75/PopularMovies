package com.example.nitu.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/16/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String MOVIE_QUERY = "135397";

    // "content://com.example.nitu.popularmovies.data/trailer"
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    // "content://com.example.nitu.popularmovies.data/trailer/*"
    private static final Uri TEST_TRAILER_WITH_MOVIE_DIR = MovieContract.TrailerEntry.buildTrailerMovie(MOVIE_QUERY);
    // "content://com.example.nitu.popularmovies.data/review"
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    // "content://com.example.nitu.popularmovies.data/review/*"
    private static final Uri TEST_REVIEW_WITH_MOVIE_DIR = MovieContract.ReviewEntry.buildReviewMovie(MOVIE_QUERY);
    // content://com.example.android.sunshine.app/movie"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    // content://com.example.android.sunshine.app/movie/*"
    private static final Uri TEST_MOVIE_WITH_MOVIE_DIR = MovieContract.MovieEntry.buildMovie(MOVIE_QUERY);
    // content://com.example.android.sunshine.app/movie/popularity"
    private static final Uri TEST_MOVIE_WITH_POPULARITY = MovieContract.MovieEntry.buildPopularMovie();
    // content://com.example.android.sunshine.app/movie/popularity"
    private static final Uri TEST_MOVIE_WITH_RATING = MovieContract.MovieEntry.buildTopratedMovie();
    // content://com.example.android.sunshine.app/movie/popularity"
    private static final Uri TEST_MOVIE_WITH_FAVOURITE = MovieContract.MovieEntry.buildFavouriteMovie();
    // content://com.example.android.sunshine.app/movie/popularity"
    private static final Uri TEST_MOVIE_WITH_COMINGSOON = MovieContract.MovieEntry.buildComingSoonMovie();
    // content://com.example.android.sunshine.app/movie/popularity"
    private static final Uri TEST_MOVIE_WITH_PLAYINGNOW = MovieContract.MovieEntry.buildPlayingNowMovie();

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
        assertEquals("Error: The TRAILER WITH MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_WITH_MOVIE_DIR), MovieProvider.TRAILER_WITH_MOVIE);

        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
        assertEquals("Error: The REVIEW WITH MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_WITH_MOVIE_DIR), MovieProvider.REVIEW_WITH_MOVIE);

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The MOVIE WITH KEY URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_MOVIE_DIR), MovieProvider.MOVIE_WITH_KEY);

        assertEquals("Error: The POPULAR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_POPULARITY), MovieProvider.MOVIE_BY_POPULARITY);

        assertEquals("Error: The TOP RATING MOVIE was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_RATING), MovieProvider.MOVIE_BY_RATING);

        assertEquals("Error: The FAVOURITE MOVIE was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_FAVOURITE), MovieProvider.MOVIE_BY_FAVOURITE);

        assertEquals("Error: The COMING SOON MOVIE was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_COMINGSOON), MovieProvider.MOVIE_BY_COMINGSOON);

        assertEquals("Error: The PLAYING NOW MOVIE was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_PLAYINGNOW), MovieProvider.MOVIE_BY_PLAYINGNOW);
    }
}
