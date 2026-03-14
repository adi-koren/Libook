package com.libookproject.libookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterReviews extends RecyclerView.Adapter<CustomAdapterReviews.ReviewViewHolder> {

    private ArrayList<Review> reviews;
    private Context context;

    public CustomAdapterReviews(Context context, ArrayList<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_review_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        Review review = reviews.get(position);

        holder.tvReviewerName.setText(review.getUsername());
        holder.tvReviewComment.setText(review.getComment());
        holder.ratingBarReview.setRating(review.getRating());
        holder.tvReviewDate.setText(review.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView tvReviewerName;
        TextView tvReviewComment;
        TextView tvReviewDate;
        RatingBar ratingBarReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBarReview = itemView.findViewById(R.id.ratingBarReview);
        }
    }
}
