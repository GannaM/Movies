package com.example.android.movies.utils;

import android.support.v4.content.AsyncTaskLoader;
//import android.content.AsyncTaskLoader;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.example.android.movies.MainActivity;
import com.example.android.movies.MovieDetailActivity;
import com.example.android.movies.model.Movie;


import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieDataLoader extends AsyncTaskLoader<List<?>> {

    private int loaderID;
    private ProgressBar loadingIndicator;
    private URL requestUrl;


    private List<?> objectList = null;


    public MovieDataLoader(Context context, int loaderID, ProgressBar loadingIndicator, URL requestUrl) {
        super(context);

        this.loaderID = loaderID;
        this.loadingIndicator = loadingIndicator;
        this.requestUrl = requestUrl;
    }


    @Override
    protected void onStartLoading() {
        if (objectList != null) {
            deliverResult(objectList);
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public List<?> loadInBackground() {

        try {
            String jsonMovieResponse = MovieListService.getResponseFromHttpUrl(requestUrl);
            Log.d("JSON", jsonMovieResponse);

            switch (loaderID) {

                case MainActivity.MOVIES_LOADER_ID:
                    objectList = JsonUtils.parseMoviesJson(jsonMovieResponse);
                    //List<Movie> movieList = JsonUtils.parseMoviesJson(jsonMovieResponse);

                    break;

                case MovieDetailActivity.DETAIL_LOADER_ID:
                    objectList = JsonUtils.parseDetails(jsonMovieResponse);
                    break;

            }

            return objectList;

            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }


    }

    public void deliverResult(List<?> data) {
        objectList = data;
        super.deliverResult(data);
    }


}
