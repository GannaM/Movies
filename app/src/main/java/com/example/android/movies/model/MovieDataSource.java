package com.example.android.movies.model;

import android.arch.paging.PageKeyedDataSource;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.movies.BuildConfig;
import com.example.android.movies.utils.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    private String API_KEY = BuildConfig.API_KEY;
    private static final int FIRST_PAGE = 1;
    private String movieTag;

    public MovieDataSource(String movieTag) {
        super();
        this.movieTag = movieTag;
    }


    @Override
    public void loadInitial(@NonNull PageKeyedDataSource.LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Movie> callback) {

        Log.d("MOVIE DATA SOURCE", "Retrieving movies...");
        RetrofitClient.getInstance()
                //.getApi().getPopularMovies(FIRST_PAGE, API_KEY)
                .getApi().getMovies(movieTag, FIRST_PAGE, API_KEY)
                .enqueue(new Callback<MoviePageResult>() {
                    @Override
                    public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {
                        if (response.body() != null) {

                            Log.d("DATA LINK:", call.toString());

                            List<Movie> movies = response.body().getResults();
                            callback.onResult(movies, null, FIRST_PAGE + 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviePageResult> call, Throwable t) {
                        Log.d("Something went wrong", call.toString());
                        t.fillInStackTrace();

                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        RetrofitClient.getInstance()
                //.getApi().getPopularMovies(params.key, API_KEY)
                .getApi().getMovies(movieTag, params.key, API_KEY)
                .enqueue(new Callback<MoviePageResult>() {
                    @Override
                    public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {
                        Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                        if (response.body() != null) {
                            callback.onResult(response.body().getResults(), adjacentKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviePageResult> call, Throwable t) {

                    }
                });


    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        RetrofitClient.getInstance()
                //.getApi().getPopularMovies(params.key, API_KEY)
                .getApi().getMovies(movieTag, params.key, API_KEY)
                .enqueue(new Callback<MoviePageResult>() {
                    @Override
                    public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {
                        if (response.body() != null) {

                            int nextKey = (params.key == response.body().getTotalPages()) ? null : params.key + 1;

                            callback.onResult(response.body().getResults(), nextKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviePageResult> call, Throwable t) {

                    }
                });

    }
}

