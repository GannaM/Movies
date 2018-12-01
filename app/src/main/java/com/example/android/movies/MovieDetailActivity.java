package com.example.android.movies;

import android.net.Uri;

import android.support.v4.app.LoaderManager;

import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.model.AppDatabase;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.Trailer;

import com.example.android.movies.utils.AppExecutors;
import com.example.android.movies.utils.ImageAdapter;
import com.example.android.movies.utils.MovieDataLoader;
import com.example.android.movies.utils.MovieListService;
import com.example.android.movies.utils.ReviewAdapter;
import com.example.android.movies.utils.TrailerAdapter;
import com.example.android.movies.utils.TrailerAdapter.TrailerAdapterOnClickHandler;

import com.example.android.movies.utils.ReviewAdapter.ReviewAdapterOnClickHandler;
import com.squareup.picasso.Picasso;


import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
                                    implements TrailerAdapterOnClickHandler, ReviewAdapterOnClickHandler,
                                                                    LoaderManager.LoaderCallbacks<List<?>> {

    private static final int DEFAULT_POSITION = -1;
    public static final String MOVIE_INDEX = "movie_index";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_POSTER = "movie_poster";
    public static final String MOVIE_OVERVIEW = "movie_overview";
    public static final String MOVIE_RATING = "movie_rating";
    public static final String MOVIE_DATE = "movie_date";

    public static final int DETAIL_LOADER_ID = 0;

    private boolean isFavorite;
    private ImageButton mFavoriteButton;
    private static final String BUTTON_STATE = "button_state";

    private Movie mMovie;
    private ImageView mPosterIv;
    private TextView mReleaseDateTv;
    private TextView mUserRatingTv;
    private TextView mOverviewTv;

    private View mTrailerView;
    private RecyclerView mTrailerRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TrailerAdapter mTrailerAdapter;

    private View mReviewsView;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private List<Trailer> mTrailerList;
    private List<Review> mReviewList;

    private AppDatabase mDb;
    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        API_KEY = getString(R.string.api_key);

        mPosterIv = findViewById(R.id.iv_movie_poster);
        mReleaseDateTv = findViewById(R.id.tv_release_date);
        mUserRatingTv = findViewById(R.id.tv_user_rating);
        mOverviewTv = findViewById(R.id.tv_overview);

        mTrailerList = new ArrayList<>();
        mReviewList = new ArrayList<>();
        mFavoriteButton = findViewById(R.id.imageButton_favorite);
        mDb = AppDatabase.getsInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        final Bundle bundle = intent.getExtras();
        if (bundle == null) {
            closeOnError();
        }

        mMovie = new Movie();
        mMovie.setId(bundle.getString(MOVIE_ID));
        mMovie.setTitle(bundle.getString(MOVIE_TITLE));
        mMovie.setPoster(bundle.getString(MOVIE_POSTER));
        mMovie.setOverview(bundle.getString(MOVIE_OVERVIEW));
        mMovie.setDate(bundle.getString(MOVIE_DATE));
        mMovie.setRating(bundle.getDouble(MOVIE_RATING));

//        int index = intent.getIntExtra(MOVIE_INDEX, DEFAULT_POSITION);
//        if (index == DEFAULT_POSITION) {
//            closeOnError();
//        }

        //mMovie = ImageAdapter.retrieveMovie(index);
//        String movieTitle = mMovie.getTitle();
//        if (movieTitle == null || movieTitle.isEmpty()) {
//            movieTitle = getString(R.string.unknown);
//        }
        setTitle(mMovie.getTitle());


        if (savedInstanceState != null) {
            isFavorite = savedInstanceState.getBoolean(BUTTON_STATE);
            updateButtonImage();
        }
        else {
            checkIfFavorite();
        }
        Log.d("FAVORITE:", Boolean.toString(isFavorite));

        String releaseDate = parseDateString(mMovie.getDate());
        mReleaseDateTv.setText(releaseDate);


        String rating = mMovie.getRating() + " / 10";
        mUserRatingTv.setText(rating);

        mOverviewTv.setText(mMovie.getOverview());

        String size185 = getString(R.string.poster_size_185);
        URL posterURL = MovieListService.buildPosterURL(size185, mMovie.getPoster());
        String posterPath = posterURL.toString();

        Picasso.get()
                .load(posterPath)
                .into(mPosterIv);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator_trailers);

        mTrailerView = findViewById(R.id.layout_trailers);
        mTrailerRecyclerView = findViewById(R.id.recyclerview_trailers);
        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(layoutManagerTrailers);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mReviewsView = findViewById(R.id.layout_reviews);
        mReviewRecyclerView = findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(layoutManagerReviews);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    @Override
    public void onClickTrailer(String videoKey, String site) {
        if (site.equals("YouTube")) {
            String videoUrl = "https://youtu.be/" + videoKey;
            openVideoPage(videoUrl);
        }

    }

    @Override
    public void onClickReview() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUTTON_STATE, isFavorite);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isFavorite = savedInstanceState.getBoolean(BUTTON_STATE);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Loader<List<?>> onCreateLoader(int id, Bundle args) {

        URL detailRequestUrl = MovieListService.buildMovieDetailURL(mMovie.getId(), API_KEY);
        return new MovieDataLoader(this, id, mLoadingIndicator, detailRequestUrl);
    }



    @Override
    public void onLoadFinished(Loader<List<?>> loader, List<?> data) {

        mTrailerList.clear();
        mReviewList.clear();

        // Cast data to corresponding values
        for (Object obj : data ) {
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                for (Object item : list) {
                    if (item instanceof Trailer) {
                        Trailer trailer = (Trailer) item;
                        mTrailerList.add(trailer);
                    }
                    else if (item instanceof Review) {
                        Review review = (Review) item;
                        mReviewList.add(review);
                    }
                }
            }
        }

        if (mReviewList.isEmpty()) {
            mReviewsView.setVisibility(View.INVISIBLE);
        }
        if (mTrailerList.isEmpty()) {
            mTrailerView.setVisibility(View.INVISIBLE);
        }

        mTrailerAdapter.setTrailerData(mTrailerList);
        mReviewAdapter.setReviewData(mReviewList);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    public void makeFavorite(View view) {

        if (!isFavorite) {
            isFavorite = true;

            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(mMovie);
                }
            });
        }
        else {
            isFavorite = false;
            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(mMovie);
                }
            });

        }
        updateButtonImage();
        Log.d("makeFavorite: ", Boolean.toString(isFavorite));
    }

    private void updateButtonImage() {
        if (isFavorite) {
            mFavoriteButton.setImageResource(R.drawable.button_favorite_selected);
        }
        else {
            mFavoriteButton.setImageResource(R.drawable.button_favorite_unselected);
        }
    }

    private void checkIfFavorite() {
        isFavorite = false;
        final String id = mMovie.getId();
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movie favoriteMovie = mDb.movieDao().loadMovieById(id);
                if (favoriteMovie != null) {
                    isFavorite = true;
                    updateButtonImage();
                }
            }
        });
    }

    private String parseDateString(String dateString) {
        String inPattern = "yyyy-MM-dd";
        String outPattern = "MMM d, yyyy";

        Date date;
        String parsedDate = "N/A";

        try {
            date = new SimpleDateFormat(inPattern).parse(dateString);
            parsedDate = new SimpleDateFormat(outPattern).format(date);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }

        return parsedDate;
    }

    private void openVideoPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
