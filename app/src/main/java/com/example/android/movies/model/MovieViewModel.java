package com.example.android.movies.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import android.support.annotation.NonNull;



public class MovieViewModel extends AndroidViewModel {

    private LiveData<PagedList<Movie>> moviePagedList;
    private AppDatabase mDb;
    private DataSource.Factory<Integer, Movie> movieDataSourceFactory;
    private PagedList.Config pagedListConfig;

    public MovieViewModel(@NonNull Application application) {
        super(application);

        pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .setPrefetchDistance(20)
                .setInitialLoadSizeHint(20)
                .build();

        mDb = AppDatabase.getsInstance(this.getApplication());
    }

    public LiveData<PagedList<Movie>> getMoviesByDataSource(String sortTag) {
        // For Favorite Movies that are derived from a local database
        if (sortTag.equals("favorite")) {
            movieDataSourceFactory = mDb.movieDao().loadMoviesByPage();
        }

        // For Popular and Top Rated Movies that are derived from network
        else {
            movieDataSourceFactory = new MovieDataSourceFactory(sortTag);
        }

        moviePagedList = new LivePagedListBuilder<>(movieDataSourceFactory, pagedListConfig)
                .build();
        return moviePagedList;
    }
}
