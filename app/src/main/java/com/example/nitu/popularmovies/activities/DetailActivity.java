package com.example.nitu.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.fragments.DetailActivityFragment;
import com.example.nitu.popularmovies.model.MovieData;


public class DetailActivity extends BaseActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

