package com.example.android.movies.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.Trailer;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>  {

    private List<Review> mReviewList;

    private final ReviewAdapter.ReviewAdapterOnClickHandler mClickHandler;


    public interface ReviewAdapterOnClickHandler {
        void onClickReview();
    }


    public ReviewAdapter(ReviewAdapter.ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mReviewTextView;
        public final TextView mReviewAuthorTextView;
        public final ImageView mDivider;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = view.findViewById(R.id.tv_review);
            mReviewAuthorTextView = view.findViewById(R.id.tv_author_name);
            mDivider = view.findViewById(R.id.iv_divider);
            view.setOnClickListener(this);
        }

        // Expand or Collapse review on click
        @Override
        public void onClick(View view) {
            int lines = mReviewTextView.getLineCount();
            if (lines == 5) {
                mReviewTextView.setMaxLines(200);
            }
            else if (lines > 5) {
                mReviewTextView.setMaxLines(5);
            }

            mClickHandler.onClickReview();
        }
    }


    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder viewHolder, int position) {

        Review thisReview = mReviewList.get(position);

        String authorName = thisReview.getAuthor();
        viewHolder.mReviewAuthorTextView.setText(authorName);

        String review = thisReview.getContent();
        viewHolder.mReviewTextView.setText(review);

        // If it is the last Review in the list, hide the divider
        int reviewCount = getItemCount();
        if (position == reviewCount-1) {
            viewHolder.mDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) { return 0; }
        return mReviewList.size();
    }

    public void setReviewData(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }
}
