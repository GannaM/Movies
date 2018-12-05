package com.example.android.movies.utils;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
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

import java.net.URL;

public class MovieAdapter extends PagedListAdapter<Movie, MovieAdapter.MovieViewHolder> {

    final private MovieAdapter.ItemClickListener mItemClickListener;


    public MovieAdapter(MovieAdapter.ItemClickListener listener) {
        super(DIFF_CALLBACK);

        mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(Movie movie);
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;

        private MovieViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.poster_view);
            view.setOnClickListener(this);
        }

        private void bindTo(Movie movie) {
            URL posterPath = MovieListService.buildPosterURL("w185", movie.getPoster());

            Picasso.get()
                    .load(posterPath.toString())
                    .into(mImageView);
        }

        public void clear() {
            //mImageView.setImageDrawable(R.drawable.ic_launcher_foreground);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = getItem(position);
            mItemClickListener.onItemClick(movie);
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

    private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
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

