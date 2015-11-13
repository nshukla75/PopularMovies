package com.example.nitu.popularmovies.activities;

import android.os.Bundle;
import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.fragments.DetailActivityFragment;

public class MainActivity extends BaseActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MAINFRAGMENT_TAG = "MFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

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
    }


}
