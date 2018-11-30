package com.example.android.movies.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.android.movies.R;

public class MovieViewModel extends AndroidViewModel {

    //creating livedata for PagedList and PageKeyedDataSource

    public LiveData<PagedList<Movie>> moviePagedList;
//    LiveData<PageKeyedDataSource<Integer, Movie>> liveDataSource;

    public MovieViewModel(@NonNull Application application) {
        super(application);

        //LiveData<PageKeyedDataSource<Integer, Movie>> liveDataSource = movieDataSourceFactory.getMovieLiveDataSource();

        recreateStuff();
    }

    public void recreateStuff() {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .setPrefetchDistance(20)
                .setInitialLoadSizeHint(20)
                .build();

        //Executor executor = Executors.newFixedThreadPool(5);
        Context context = this.getApplication().getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortTag = sharedPreferences.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular_value));

        DataSource.Factory<Integer, Movie> movieDataSourceFactory = null;

//
        switch (sortTag) {
            case "favorite":
                AppDatabase database = AppDatabase.getsInstance(this.getApplication());
                movieDataSourceFactory = database.movieDao().loadMoviesByPage();

                break;

            default:
                movieDataSourceFactory = new MovieDataSourceFactory(sortTag);
                moviePagedList = new LivePagedListBuilder<Integer, Movie>(movieDataSourceFactory, pagedListConfig)
                        //        .setFetchExecutor(executor)
                        .build();
                break;
        }

        moviePagedList = new LivePagedListBuilder<>(movieDataSourceFactory, pagedListConfig)
                //        .setFetchExecutor(executor)
                .build();
    }
}
