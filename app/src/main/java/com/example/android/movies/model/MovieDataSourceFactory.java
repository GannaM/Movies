package com.example.android.movies.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;

public class MovieDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, Movie>> movieLiveDataSource = new MutableLiveData<>();
    private String movieTag;

    public MovieDataSourceFactory(String movieTag) {
        super();
        this.movieTag = movieTag;
    }

    @Override
    public DataSource create() {

        MovieDataSource movieDataSource = new MovieDataSource(movieTag);

        movieLiveDataSource.postValue(movieDataSource);

        return movieDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Movie>> getMovieLiveDataSource() {
        return movieLiveDataSource;
    }
}
