package com.example.android.movies.utils;

import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A_m_ie on 5/19/18.
 */

public class JsonUtils {

    public static List<Movie> parseMoviesJson(String json) {

        List<Movie> movies = new ArrayList<>();
        Movie movie;

        try {
            JSONObject movieData = new JSONObject(json);

            JSONArray resultsArray = movieData.getJSONArray("results");

            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movieJsonOb = resultsArray.getJSONObject(i);
                    movie = getMovieObject(movieJsonOb);

                    movies.add(movie);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }


    public static List<List<?>> parseDetails(String json) {

        List<List<?>> detailList = new ArrayList<>();

        try {
            JSONObject detailData = new JSONObject(json);
            JSONObject videos = detailData.getJSONObject("videos");
            JSONObject reviews = detailData.getJSONObject("reviews");

            List<Trailer> trailerList = parseTrailerJson(videos);
            List<Review> reviewList = parseReviewJson(reviews);

            detailList.add(trailerList);
            detailList.add(reviewList);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return detailList;
    }


    private static List<Trailer> parseTrailerJson(JSONObject trailers) {

        List<Trailer> trailerList = new ArrayList<>();

        try {

            JSONArray resultsArray = trailers.getJSONArray("results");

            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject trailerJsonObject = resultsArray.getJSONObject(i);

                    String id = trailerJsonObject.getString("id");
                    String name = trailerJsonObject.getString("name");
                    String key = trailerJsonObject.getString("key");
                    String type = trailerJsonObject.getString("type");
                    String site = trailerJsonObject.getString("site");

                    Trailer trailer = new Trailer(id, name, key, type, site);
                    trailerList.add(trailer);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailerList;
    }


    private static List<Review> parseReviewJson(JSONObject reviews) {

        List<Review> reviewList = new ArrayList<>();

        try {
            JSONArray resultsArray = reviews.getJSONArray("results");

            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject reviewJsonObject = resultsArray.getJSONObject(i);

                    String author = reviewJsonObject.getString("author");
                    String content = reviewJsonObject.getString("content");
                    String id = reviewJsonObject.getString("id");
                    String url = reviewJsonObject.getString("url");

                    Review review = new Review(author, content, id, url);
                    reviewList.add(review);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviewList;
    }




    private static Movie getMovieObject(JSONObject data) {

        Movie movie = new Movie();

        try {
            String id = data.getString("id");
            movie.setId(id);

            String title = data.getString("title");
            movie.setTitle(title);

            String poster = data.getString("poster_path");
            movie.setPoster(poster);

            String overview = data.getString("overview");
            movie.setOverview(overview);

            double rating = data.getDouble("vote_average");
            movie.setRating(rating);

            String date = data.getString("release_date");
            movie.setDate(date);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;

    }



}
