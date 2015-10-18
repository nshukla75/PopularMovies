package com.example.nitu.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.nitu.popularmovies.data.MovieContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by nitus on 10/17/2015.
 */
public class Utility {
    public static String getPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default)));
    }

    public static byte[] getImage(Cursor cursor) {
        // get row indices for our cursor
        byte[] bb = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        return bb;
    }

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

}

