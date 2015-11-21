package com.example.nitu.popularmovies.activities;

import android.os.Bundle;
import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.fragments.DetailActivityFragment;

public class DetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            DetailActivityFragment fragment = new DetailActivityFragment();
            args.putLong(DetailActivityFragment.MOVIE_ID_KEY, getIntent().getLongExtra("movie_id_key",Long.MIN_VALUE));
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_fragment_container, new DetailActivityFragment())
                .commit();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

