package com.example.nitu.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nitus on 10/15/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    public void testBuildMovie() {
        String expected = "0";
        Uri movieUri = MovieContract.MovieEntry.buildUri(Long.valueOf(expected));
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovie in " +
                        "MovieContract.", movieUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",
                expected, movieUri.getLastPathSegment());
        assertEquals("Error: movie Uri doesn't match our expected result",
                "content://com.example.nitu.popularmovies/movie/" + expected,
                movieUri.toString());
    }

}
