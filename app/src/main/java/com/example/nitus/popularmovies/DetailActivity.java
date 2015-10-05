package com.example.nitus.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailActivityFragment extends Fragment {
        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
        private String mMovieStr;

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if(mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }else{
                Log.d(LOG_TAG, "Share Action Provider is null");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra(intent.EXTRA_TEXT))
            {
                MovieData movie = (MovieData)intent.getSerializableExtra(intent.EXTRA_TEXT);
                String mMovieTitle = movie.getTitle();
                ((TextView)rootView.findViewById(R.id.title_text))
                    .setText(mMovieTitle);
                String url =movie.getPoster_path();
                Picasso.with(getActivity()).load(url).into((ImageView) rootView.findViewById(R.id.imageView));
                String mMovieVoteCount = "( "+ movie.getVote_count()+" Votes)";
                ((TextView)rootView.findViewById(R.id.votecount_text))
                        .setText(mMovieVoteCount);
                String mMovieVoteAverage = movie.getVote_average();
                ((TextView)rootView.findViewById(R.id.voteaverage_text))
                        .setText(mMovieVoteAverage);
                String mMovieReleaseDate = movie.getRelease_date();
                ((TextView)rootView.findViewById(R.id.release_text))
                        .setText(mMovieReleaseDate);
                String mMovieOverview = movie.getOverview();
                ((TextView)rootView.findViewById(R.id.overview_text))
                        .setText(mMovieOverview);

            }
            return rootView;
        }

        private Intent createShareForecastIntent(){
            Intent shareIntent =new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,mMovieStr);
            return shareIntent;
        }
    }

}
