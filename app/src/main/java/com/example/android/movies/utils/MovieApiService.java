package com.example.android.movies.utils;

import com.example.android.movies.model.MoviePageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("popular")
    Call<MoviePageResult> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("top_rated")
    Call<MoviePageResult> getTopRatedMovies(@Query("page") int page, @Query("api_key") String apiKey);

}
