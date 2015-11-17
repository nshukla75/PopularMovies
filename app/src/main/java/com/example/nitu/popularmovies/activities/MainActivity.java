package com.example.nitu.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.animation.ShowAnimation;
import com.example.nitu.popularmovies.application.PopMovieApp;
import com.example.nitu.popularmovies.fragments.DetailActivityFragment;
import com.example.nitu.popularmovies.fragments.MainActivityFragment;
import com.example.nitu.popularmovies.model.MovieData;


public class MainActivity extends BaseActivity implements MainActivityFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MAINFRAGMENT_TAG = "MFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private View mMovieDetailsContainer;
    private PopMovieApp.State appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appState = ((PopMovieApp)getApplication()).STATE;
        if(findViewById(R.id.detail_fragment_container)!= null) {
            mTwoPane = true;
            appState.setTwoPane(true);
        }
        else{
            mTwoPane = false;
            appState.setTwoPane(false);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mMovieDetailsContainer != null)
            appState.setDetailsPaneShown(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTwoPane = (mMovieDetailsContainer = findViewById(R.id.detail_fragment_container)) != null;
        appState.setTwoPane(mTwoPane);
    }

    @Override
    public void onItemSelected(MovieData item) {
        if (mTwoPane) {
            appState.setTwoPane(true);
            //appState.setDetailsPaneShown(true);
            if (item.id == Long.MIN_VALUE){
                if (appState.isDetailsPaneShown()){
                    mMovieDetailsContainer.startAnimation(new ShowAnimation(mMovieDetailsContainer, 0f, 1000L));
                    appState.setDetailsPaneShown(false);
                }
            }else {
                if (!appState.isDetailsPaneShown()) {
                    mMovieDetailsContainer.startAnimation(new ShowAnimation(mMovieDetailsContainer, 4f, 1000L));
                    appState.setDetailsPaneShown(true);
                }
                Bundle args = new Bundle();
                DetailActivityFragment fragment = new DetailActivityFragment();
                args.putLong(DetailActivityFragment.MOVIE_ID_KEY, item.id);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentById(R.id.fragment) != null) {
                    fragmentTransaction.replace(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG);
                    //fragmentTransaction.addToBackStack(DETAILFRAGMENT_TAG);

                } else {
                    fragmentTransaction.add(R.id.detail_fragment_container, fragment, DETAILFRAGMENT_TAG);
                }
                fragmentTransaction.commit();
            }
        } else {
            appState.setTwoPane(false);
            appState.setDetailsPaneShown(false);
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(getString(R.string.movie_id_key), item.id.longValue());
            startActivity(intent);
        }
    }
}
