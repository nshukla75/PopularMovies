package com.example.nitu.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_TRAILER_MOVIE = "/135397";
    private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildTrailerMovie() {
        Uri movieUri = MovieContract.TrailerEntry.buildTrailerMovie(TEST_TRAILER_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Trailer movie not properly appended to the end of the Uri",
                TEST_TRAILER_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: Trailer movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.nitu.popularmovies.data/trailer/%2F135397");
    }

}
