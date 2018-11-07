package com.example.android.movies.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by A_m_ie on 5/11/18.
 */

public class ImageAdapter extends BaseAdapter {

    private static List<Movie> mMovieData;
    private String[] mImages;

    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
        mImages = getImageLinks(mMovieData);
    }

    public int getCount() {
        return mImages.length;
    }


    public String getItem(int position) {
        return mImages[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(350, 525));
            imageView.setPadding(8, 8, 8, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        String image = getItem(position);
        Picasso.get()
                .load(image)
                .into(imageView);

        return imageView;
    }


    private String[] getImageLinks (List<Movie> movies) {
        int length = 0;

        if (movies != null) {
            length = movies.size();
        }

        String[] list = new String[length];

        for (int i = 0; i < length; i++) {
            Movie movie = movies.get(i);
            String posterPath = movie.getPoster();

            String posterSize = mContext.getResources().getString(R.string.poster_size_185);
            URL posterURL = MovieListService.buildPosterURL(posterSize, posterPath);
            String url = posterURL.toString();

            list[i] = url;
        }

        return list;
    }

    public void setImageData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        mImages = getImageLinks(mMovieData);
        super.notifyDataSetChanged();
    }

    public static Movie retrieveMovie(int index) {
        return mMovieData.get(index);
    }
}