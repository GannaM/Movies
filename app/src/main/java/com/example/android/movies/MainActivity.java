package com.example.android.movies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.model.AppDatabase;
import com.example.android.movies.model.MainViewModel;
import com.example.android.movies.model.Movie;
import com.example.android.movies.settings.SettingsActivity;
import com.example.android.movies.utils.ImageAdapter;
import com.example.android.movies.utils.MovieDataLoader;
import com.example.android.movies.utils.MovieListService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
                            implements LoaderManager.LoaderCallbacks<List<?>>,
                                        SharedPreferences.OnSharedPreferenceChangeListener {

    private GridView mMoviesViewGridView;
    private ImageAdapter imageAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    public static final int MOVIES_LOADER_ID = 42;

    private boolean isOnline = false;

    public List <Movie> movieList;
    public SharedPreferences sharedPreferences;

    private String sort_tag;
    private String favorite_tag;
    private Menu menu;

    private String API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = getString(R.string.api_key);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mMoviesViewGridView = findViewById(R.id.movies_gridview);


        imageAdapter = new ImageAdapter(this);
        mMoviesViewGridView.setAdapter(imageAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        movieList = new ArrayList<>();

        favorite_tag = getString(R.string.pref_sort_favorite_value);
        sort_tag = loadSortTagFromPreferences(sharedPreferences);

        validateConnectionStatus();

        if (isOnline) {
            if (sort_tag.equals(favorite_tag)) {
                setupViewModel();
            }
            else {
                getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
            }
        }

        mMoviesViewGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_INDEX, position);
                startActivity(intent);
            }
        });

        updateAppTitle();
    }


    public void setupViewModel() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (sort_tag.equals(favorite_tag)){
                    if (movies.isEmpty()) {
                        mErrorMessageDisplay.setText(R.string.no_favorite_movies_message);
                        showErrorMessage();
                    }
                    else {
                        Log.d("MainActivity", "Updating list of tasks from LiveData in ViewModel");
                        imageAdapter.setImageData(movies);
                    }
                }
            }
        });
    }



    @Override
    public Loader<List<?>> onCreateLoader(int id, Bundle bundle) {
        if (sort_tag.equals(favorite_tag)) {
            return null;
        }

        URL movieRequestUrl = MovieListService.buildURL(sort_tag, API_KEY);

        return new MovieDataLoader(this, id, mLoadingIndicator, movieRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<?>> loader, List<?> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data == null) {
            mErrorMessageDisplay.setText(R.string.error_message);
            showErrorMessage();
        } else {
            showMoviesGridView();
        }

        movieList.clear();

        for (Object obj : data) {
            if (obj instanceof Movie) {
                Movie m = (Movie) obj;
                movieList.add(m);
            }
        }

        imageAdapter.setImageData(movieList);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);

        showHideRefreshButton(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }

        if (id == R.id.action_refresh) {
            validateConnectionStatus();
            reloadData();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        validateConnectionStatus();

        sort_tag = loadSortTagFromPreferences(sharedPreferences);
        if (key.equals(getString(R.string.pref_sort_key))) {

            if (sort_tag.equals(favorite_tag)) {
                setupViewModel();
            }

            else {
                reloadData();
            }

        }
        updateAppTitle();
        showHideRefreshButton(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public String loadSortTagFromPreferences(SharedPreferences sharedPreferences) {
        String tag = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular_value));
        return tag;
    }

    private void reloadData() {
        if (isOnline) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    private void showErrorMessage() {
        mMoviesViewGridView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMoviesViewGridView.setVisibility(View.VISIBLE);

    }

    private void showHideRefreshButton(Menu menu) {
        int id = R.id.action_refresh;
        MenuItem refreshItem = menu.findItem(id);
        if (sort_tag.equals(favorite_tag)) {
            refreshItem.setVisible(false);
        }
        else {
            refreshItem.setVisible(true);
        }
    }

    private void validateConnectionStatus() {
        isOnline = getConnectionStatus(getApplicationContext());
        if (isOnline) {
            showMoviesGridView();
        } else  {
            showErrorMessage();
        }
    }

    public boolean getConnectionStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void updateAppTitle() {
        String appTitle;
        if (sort_tag.equals(getString(R.string.pref_sort_top_rated_value))) {
            appTitle = getString(R.string.app_name_top_rated);
        }
        else if (sort_tag.equals(favorite_tag)) {
            appTitle = getString(R.string.app_name_favorite);
        }
        else {
            appTitle = getString(R.string.app_name_popular);
        }
        setTitle(appTitle);
    }
}
