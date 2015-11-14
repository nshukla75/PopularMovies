package com.example.nitu.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.fragments.DetailActivityFragment;
import com.example.nitu.popularmovies.fragments.MainActivityFragment;
import com.example.nitu.popularmovies.model.MovieData;


public class MainActivity extends BaseActivity implements MainActivityFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MAINFRAGMENT_TAG = "MFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private View mMovieDetailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.detail_fragment_container)!= null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, new DetailActivityFragment())
                    .commit();
            }
        }
        else{
            mTwoPane = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTwoPane = (mMovieDetailsContainer = findViewById(R.id.detail_fragment_container)) != null;
    }

    @Override
    public void onItemSelected(MovieData item) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            DetailActivityFragment fragment = new DetailActivityFragment();
            args.putLong(DetailActivityFragment.MOVIE_ID_KEY, item.id);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG)
                    .addToBackStack(DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(getString(R.string.movie_id_key), item.id.longValue());
            startActivity(intent);
        }
    }
}
