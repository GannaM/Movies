package com.example.android.movies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by A_m_ie on 5/8/18.
 */

public class MovieListService {

    public static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String KEY_PARAM = "api_key";
    public static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/";

    // Builds a url to fetch popular / top-rated movies
    public static URL buildURL(String popularityTag, String API_KEY) {

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(popularityTag)
                .appendQueryParameter(KEY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("LINK", url.toString());
        return url;
    }

    // Builds a url to return a path for a poster
    public static URL buildPosterURL(String posterSize, String posterPath) {
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(posterSize)
                .appendEncodedPath(posterPath)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieDetailURL(String movieId, String API_KEY) {

        String APPEND_PARAM = "append_to_response";
        String APPEND_VALUE = "videos,reviews";

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .appendQueryParameter(APPEND_PARAM, APPEND_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Movie Detail LINK", url.toString());
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
