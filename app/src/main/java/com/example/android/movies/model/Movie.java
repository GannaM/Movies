package com.example.android.movies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by A_m_ie on 5/7/18.
 */

@Entity
public class Movie {

    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String poster;
    private String overview;
    private double rating;
    private String date;
    //private List<Trailer> trailerList;
    //private List<Review> reviewList;



    // CONSTRUCTORS

    @Ignore
    public Movie() { }

    public Movie(String id, String title, String poster, String overview, double rating, String date) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.rating = rating;
        this.date = date;
    }

    // GETTERS

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPoster() { return poster; }
    public String getOverview() { return overview; }
    public double getRating() { return rating; }
    public String getDate() { return date; }
    //public List<Trailer> getTrailerList() { return trailerList; }
    //public List<Review> getReviewList() { return reviewList; }


    // SETTERS

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPoster(String poster) { this.poster = poster; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setRating(double rating) { this.rating = rating; }
    public void setDate(String date) { this.date = date; }
    //public void setTrailerList(List<Trailer> trailerList) { this.trailerList = trailerList; }
    //public void setReviewList(List<Review> reviewList) { this.reviewList = reviewList; }


}
