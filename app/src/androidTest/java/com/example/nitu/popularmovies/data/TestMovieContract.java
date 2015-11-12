package com.example.nitu.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final Long TEST_TRAILER_MOVIE = 1419033600L;

    public void testBuildTrailerMovie() {
        Uri movieUri = MovieContract.MovieEntry.buildTrailerMovie(TEST_TRAILER_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Trailer movie not properly appended to the end of the Uri",
                TEST_TRAILER_MOVIE.toString(), movieUri.getPathSegments().get(1));
        assertEquals("Error: Trailer movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.nitu.popularmovies/movie/"+TEST_TRAILER_MOVIE.toString()+"/trailer");
    }

    public void testBuildReviewMovie() {
        Uri movieUri = MovieContract.MovieEntry.buildReviewMovie(TEST_TRAILER_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Trailer movie not properly appended to the end of the Uri",
                TEST_TRAILER_MOVIE.toString(), movieUri.getPathSegments().get(1));
        assertEquals("Error: Review movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.nitu.popularmovies/movie/"+TEST_TRAILER_MOVIE.toString()+"/review");
    }

}
