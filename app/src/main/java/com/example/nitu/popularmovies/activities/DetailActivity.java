package com.example.nitu.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nitu.popularmovies.fragments.DetailActivityFragment;
import com.example.nitu.popularmovies.R;


public class DetailActivity extends AppCompatActivity implements DetailActivityFragment.OnDataPass {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public String FirstTrailerURL="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailfragment, menu);

        // Locate MenuItem with ShareActionProvider
        //MenuItem item = menu.findItem(R.id.share);

        // Fetch and store ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //setShareIntent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Trailer of Movie");
            shareIntent.putExtra(Intent.EXTRA_TEXT, FirstTrailerURL);
            startActivity(Intent.createChooser(shareIntent, "Reciever's Address"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DataPass(String data) {

        FirstTrailerURL = data;
        Log.d("LOG","hello " + FirstTrailerURL);

    }
}

