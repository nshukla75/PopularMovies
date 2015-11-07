package com.example.nitu.popularmovies.activities;

import android.os.Bundle;
import com.example.nitu.popularmovies.R;


public class DetailActivity extends BaseActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}

