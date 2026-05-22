package com.libookproject.libookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.libookproject.libookapp.R;
import com.libookproject.libookapp.Review;

import java.util.ArrayList;

/**
 * custom RecyclerView adapter for displaying a list of book/post reviews.
 * each item displays the reviewer's username, comment, star rating, and date.
 * uses ViewHolder for efficient view recycling.
 */
public class CustomAdapterReviews extends RecyclerView.Adapter<CustomAdapterReviews.ReviewViewHolder> {

    private ArrayList<Review> reviews;
    private Context context;

    /**
     * constructs a new CustomAdapterReviews.
     * @param context the context of the activity using this adapter.
     * @param reviews the list of Review objects to display.
     */
    public CustomAdapterReviews(Context context, ArrayList<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    /**
     * inflates the custom review item layout and returns a new ViewHolder wrapping it.
     * @param parent   the parent ViewGroup the new view will be attached to.
     * @param viewType the view type of the new view (not used here).
     * @return a new ReviewViewHolder holding the inflated item view.
     */
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_review_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    /**
     * binds the review data at the given position to the provided ViewHolder.
     * sets the reviewer's username, comment text, star rating, and creation date.
     * @param holder   the ViewHolder to bind data to.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        Review review = reviews.get(position);

        holder.tvReviewerName.setText(review.getUsername());
        holder.tvReviewComment.setText(review.getComment());
        holder.ratingBarReview.setRating(review.getRating());
        holder.tvReviewDate.setText(review.getCreated_at());
    }

    /**
     * returns the total number of reviews in the list.
     * @return the size of the reviews list.
     */
    @Override
    public int getItemCount() {
        return reviews.size();
    }

    /**
     * ViewHolder class that holds references to all UI elements of a single review item.
     * created once for each visible item and reused as the user scrolls, avoiding repeated
     * findViewById calls for better performance.
     */
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView tvReviewerName;
        private TextView tvReviewComment;
        private TextView tvReviewDate;
        private RatingBar ratingBarReview;

        /**
         * constructs the ViewHolder and binds all UI element references.
         * @param itemView the inflated view for this review item.
         */
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBarReview = itemView.findViewById(R.id.ratingBarReview);
        }
    }
}
