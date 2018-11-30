package com.example.android.movies.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;


// To retrieve movies from a local database

public class MainViewModel extends AndroidViewModel {

//    private static final String TAG = MainViewModel.class.getSimpleName();
//
//    private LiveData<List<Movie>> movies;
//
//    public MainViewModel(@NonNull Application application) {
//        super(application);
//        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
//        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
//        movies = database.movieDao().loadAllMovies();
//    }
//
//    public LiveData<List<Movie>> getMovies() {
//        return movies;
//    }


    private static final String TAG = MainViewModel.class.getSimpleName();

    public final LiveData<PagedList<Movie>> movieList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving pages from the DataBase");

        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(20)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(true)
                .build();



        DataSource.Factory<Integer, Movie> movieDataSource = database.movieDao().loadMoviesByPage();

        movieList = new LivePagedListBuilder<>(movieDataSource, myPagingConfig).build();
    }

    public LiveData<PagedList<Movie>> getMovieList() {
        return movieList;
    }
}
