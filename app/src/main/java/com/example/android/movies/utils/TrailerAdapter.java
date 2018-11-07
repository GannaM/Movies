package com.example.android.movies.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.model.Trailer;

import org.w3c.dom.Text;

import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private List<Trailer> mTrailerList;

    private final TrailerAdapterOnClickHandler mClickHandler;


    public interface TrailerAdapterOnClickHandler {
        void onClickTrailer(String videoKey, String site);
    }


    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrailerTextView;
        public final TextView mTrailerTypeTextView;
        public final ImageView mDivider;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerTextView = view.findViewById(R.id.tv_trailer_name);
            mTrailerTypeTextView = view.findViewById(R.id.tv_trailer_label);
            mDivider = view.findViewById(R.id.iv_divider_trailer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = mTrailerList.get(adapterPosition);
            String linkKey = trailer.getKey();
            String site = trailer.getSite();
            mClickHandler.onClickTrailer(linkKey, site);
        }
    }


    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new TrailerAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder viewHolder, int position) {

        Trailer thisTrailer = mTrailerList.get(position);

        String trailerName = thisTrailer.getName();
        viewHolder.mTrailerTextView.setText(trailerName);

        String trailerType = thisTrailer.getType();
        viewHolder.mTrailerTypeTextView.setText(trailerType);


        // If it is the last Trailer in the list, hide the divider
        int trailerCount = getItemCount();
        if (trailerCount > 0 && position == trailerCount-1) {
            viewHolder.mDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        if (mTrailerList == null) { return 0; }
        return mTrailerList.size();
    }

    public void setTrailerData(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }
}
