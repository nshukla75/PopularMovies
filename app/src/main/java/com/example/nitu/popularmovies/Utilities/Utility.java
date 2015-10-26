package com.example.nitu.popularmovies.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.nitu.popularmovies.R;
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

    public static String getYear(String date){
        String year = "";
        if(date != null && date.length() > 0 && date.indexOf("-") > 0) {
            year = date.substring(0, date.indexOf("-"));
        }else{
            year = date;
        }
        return year;
    }

}

