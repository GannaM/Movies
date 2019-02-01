package com.example.android.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.model.Movie;
import com.example.android.movies.model.MovieViewModel;
import com.example.android.movies.settings.SettingsActivity;
import com.example.android.movies.utils.MovieAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
                            implements SharedPreferences.OnSharedPreferenceChangeListener,
                                        MovieAdapter.ItemClickListener {

    public static final int MOVIES_LOADER_ID = 42;

    private boolean isOnline = false;

    public SharedPreferences sharedPreferences;

    private String sort_tag;
    private String favorite_tag;
    private Menu menu;

    private MovieViewModel mMovieViewModel;
    private MovieAdapter mMovieAdapter;

    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    @BindView(R.id.main_recycler_view) RecyclerView mMoviesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int columnsNumber = getResources().getInteger(R.integer.column_span);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnsNumber);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        favorite_tag = getString(R.string.pref_sort_favorite_value);
        sort_tag = loadSortTagFromPreferences(sharedPreferences);

        setupViewModel();

        validateConnectionStatus();

        if (!isOnline) {
            if (!sort_tag.equals(favorite_tag)) {
                showErrorMessage();
            }
        }

        updateAppTitle();
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

        Bundle movieBundle = new Bundle();
        movieBundle.putString(MovieDetailActivity.MOVIE_ID, movie.getId());
        movieBundle.putString(MovieDetailActivity.MOVIE_TITLE, movie.getTitle());
        movieBundle.putString(MovieDetailActivity.MOVIE_OVERVIEW, movie.getOverview());
        movieBundle.putString(MovieDetailActivity.MOVIE_POSTER, movie.getPoster());
        movieBundle.putDouble(MovieDetailActivity.MOVIE_RATING, movie.getRating());
        movieBundle.putString(MovieDetailActivity.MOVIE_DATE, movie.getDate());

        intent.putExtras(movieBundle);
        startActivity(intent);

    }

    public void setupViewModel() {
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        mMovieViewModel.getMoviesByDataSource(sort_tag).observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> movies) {
                mMovieAdapter.submitList(movies);
            }
        });

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
            if (isOnline) {
                setupViewModel();
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sort_tag = loadSortTagFromPreferences(sharedPreferences);
        setupViewModel();
        updateAppTitle();
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


    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);

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
