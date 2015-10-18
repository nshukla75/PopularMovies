package com.example.nitu.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String MAINACTIVITYFRAGMENT_TAG = "MAFTAG";
    private final String LOG_TAG= MainActivity.class.getSimpleName();
    private String msortBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainActivityFragment(),MAINACTIVITYFRAGMENT_TAG)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
      protected void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferences(this);
        // update the location in our second pane using the fragment manager
        if (sortBy != null && !sortBy.equals(msortBy)) {
            MainActivityFragment ff = (MainActivityFragment)getSupportFragmentManager().findFragmentByTag(MAINACTIVITYFRAGMENT_TAG);
            if ( null != ff ) {
                ff.onPreferenceChanged();
            }
            msortBy = sortBy;
        }
    }
}
