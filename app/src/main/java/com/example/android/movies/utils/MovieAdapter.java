package com.example.android.movies.utils;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieAdapter extends PagedListAdapter<Movie, MovieAdapter.MovieViewHolder> {


    public MovieAdapter() {
        super(DIFF_CALLBACK);
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MovieViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.poster_view);
        }

        public void bindTo(Movie movie) {
            URL posterPath = buildPosterURL("w185", movie.getPoster());

            Picasso.get()
                    .load(posterPath.toString())
                    .into(mImageView);
        }

        public void clear() {
            //mImageView.setImageDrawable(R.drawable.ic_launcher_foreground);
        }

        private URL buildPosterURL(String posterSize, String posterPath) {
            String POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
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
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MovieViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder viewHolder, int position) {
        Movie movie = getItem(position);
        if (movie != null) {
            viewHolder.bindTo(movie);
        } else {
            viewHolder.clear();
        }
    }

    public static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(@NonNull Movie oldMovie, @NonNull Movie newMovie) {
                    return oldMovie.getId().equals(newMovie.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Movie oldMovie, @NonNull Movie newMovie) {
                    return oldMovie.equals(newMovie);
                }
            };

}

