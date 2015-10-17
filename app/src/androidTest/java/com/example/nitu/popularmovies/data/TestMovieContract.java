package com.example.nitu.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_TRAILER_MOVIE = "135397";

    public void testBuildTrailerMovie() {
        Uri movieUri = MovieContract.TrailerEntry.buildTrailerMovie(TEST_TRAILER_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Trailer movie not properly appended to the end of the Uri",
                TEST_TRAILER_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: Trailer movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.nitu.popularmovies/trailer/135397");
    }

    public void testBuildReviewMovie() {
        Uri movieUri = MovieContract.ReviewEntry.buildReviewMovie(TEST_TRAILER_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Trailer movie not properly appended to the end of the Uri",
                TEST_TRAILER_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: Review movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.nitu.popularmovies/review/135397");
    }

}
