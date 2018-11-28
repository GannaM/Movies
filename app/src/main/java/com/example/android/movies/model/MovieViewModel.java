package com.example.android.movies.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;

public class MovieViewModel extends ViewModel {

    //creating livedata for PagedList and PageKeyedDataSource
    public LiveData<PagedList<Movie>> moviePagedList;
    LiveData<PageKeyedDataSource<Integer, Movie>> liveDataSource;

    // constructor
    public MovieViewModel() {
        MovieDataSourceFactory movieDataSourceFactory = new MovieDataSourceFactory();

        liveDataSource = movieDataSourceFactory.getMovieLiveDataSource();

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .setPrefetchDistance(20)
                .setInitialLoadSizeHint(20)
                .build();

        //Executor executor = Executors.newFixedThreadPool(5);

        moviePagedList = new LivePagedListBuilder<Integer, Movie>(movieDataSourceFactory, pagedListConfig)
                //        .setFetchExecutor(executor)
                .build();
    }
}
