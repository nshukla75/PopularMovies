package com.example.nitu.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.Utilities.Utility;
import com.example.nitu.popularmovies.application.PopMovieApp;


public class BaseActivity extends AppCompatActivity {
    private static volatile PopMovieApp.State appState;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(this instanceof MainActivity) {
            if (appState.getTwoPane()== false)
                Utility.makeMenuItemInvisible(menu, R.id.action_share);
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        appState = ((PopMovieApp) getApplication()).STATE;
    }
}
