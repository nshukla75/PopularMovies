package com.example.nitu.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(intent.EXTRA_TEXT))
        {
            MovieData movie = (MovieData)intent.getParcelableExtra(intent.EXTRA_TEXT);
            String mMovieTitle = movie.getTitle();
            ((TextView)rootView.findViewById(R.id.title_text)).setText(mMovieTitle);
            //getActivity().setTitle(mMovieTitle);
            String url =movie.getPoster_path();
            if (Patterns.WEB_URL.matcher(url).matches())
                Picasso.with(getActivity()).load(url).into((ImageView) rootView.findViewById(R.id.imageView));
           /* String mMovieVoteCount = " ("+ movie.getVote_count()+" Votes)";
            ((TextView)rootView.findViewById(R.id.votecount_text))
                    .setText(mMovieVoteCount);*/
            String mMovieVoteAverage = movie.getVote_average();
            ((TextView)rootView.findViewById(R.id.voteaverage_text))
                    .setText(mMovieVoteAverage +"/10");
            Float f= Float.parseFloat(mMovieVoteAverage);
            ((RatingBar)rootView.findViewById(R.id.ratingBar)).setRating(f);
            String mMovieReleaseDate = movie.getRelease_date();
            ((TextView)rootView.findViewById(R.id.release_text))
                    .setText(mMovieReleaseDate);
            String mMovieOverview = movie.getOverview();
            ((TextView)rootView.findViewById(R.id.overview_text))
                    .setText(mMovieOverview);

        }
        return rootView;
    }
}
