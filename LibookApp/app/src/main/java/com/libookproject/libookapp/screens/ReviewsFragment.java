package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;
import static com.libookproject.libookapp.FBRef.username;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.libookproject.libookapp.ReviewsViewModel;
import com.libookproject.libookapp.adapters.CustomAdapterReviews;
import com.libookproject.libookapp.requestObjects.PostReviewRequest;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.dataObjects.RatingStats;
import com.libookproject.libookapp.dataObjects.Review;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.ReviewsApiService;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment
{
    private static final String KEY_SEE_ALL_ENABLED = "seeAllEnabled";
    private static final String KEY_SEND_BTN_TEXT = "sendBtnText";
    private static final String KEY_DELETE_BTN_VISIBLE = "deleteBtnVisible";
    private static final String KEY_RECYCLER_STATE = "recyclerState";

    private View view;
    private RatingBar ratingBarUser;
    private EditText etComment;
    private Button btnSendReview;
    private ImageButton btnDeleteReview;
    private Button btnSeeAllReviews;
    private RecyclerView rvReviews;

    private ReviewsViewModel reviewsViewModel;
    private String itemId = null;
    private ArrayList<Review> reviewsList = new ArrayList<>();
    private CustomAdapterReviews adpReviews;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reviews, container, false);

        init();

        reviewsViewModel = new ViewModelProvider(requireActivity())
                .get(ReviewsViewModel.class);

        if (savedInstanceState != null)
        {
            restoreState(savedInstanceState);
        }
        else if (reviewsViewModel.isLoaded())
        {
            bindData();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //check if is hidden
        if (view == null)
        {
            return;
        }

        //save buttons states
        outState.putBoolean(KEY_SEE_ALL_ENABLED, btnSeeAllReviews.isEnabled());
        outState.putString(KEY_SEND_BTN_TEXT, btnSendReview.getText().toString());
        outState.putBoolean(KEY_DELETE_BTN_VISIBLE,
                btnDeleteReview.getVisibility() == View.VISIBLE);

        //save RecyclerView scroll position
        Parcelable recyclerState = rvReviews.getLayoutManager() != null
                ? rvReviews.getLayoutManager().onSaveInstanceState()
                : null;
        outState.putParcelable(KEY_RECYCLER_STATE, recyclerState);
    }

    private void init()
    {
        ratingBarUser = view.findViewById(R.id.ratingBarUser);
        etComment = view.findViewById(R.id.etComment);
        btnSendReview = view.findViewById(R.id.btnSendReview);
        btnDeleteReview  = view.findViewById(R.id.btnDeleteReview);
        btnSeeAllReviews = view.findViewById(R.id.btnSeeAllReviews);

        btnSendReview.setOnClickListener(v -> addReviewToBook());
        btnSeeAllReviews.setOnClickListener(v -> showAllReviews());
        btnDeleteReview.setOnClickListener(v ->
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete review")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> deleteReviewFromBook())
                        .setNegativeButton("Cancel", null)
                        .show());

        rvReviews = view.findViewById(R.id.rvReviews);
        rvReviews.setNestedScrollingEnabled(false);
        adpReviews = new CustomAdapterReviews(getContext(), reviewsList);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReviews.setAdapter(adpReviews);
    }

    private void restoreState(Bundle savedInstanceState)
    {
        //restore buttons states
        btnSeeAllReviews.setEnabled(
                savedInstanceState.getBoolean(KEY_SEE_ALL_ENABLED, true));
        btnSendReview.setText(
                savedInstanceState.getString(KEY_SEND_BTN_TEXT, "Post Review"));
        btnDeleteReview.setVisibility(
                savedInstanceState.getBoolean(KEY_DELETE_BTN_VISIBLE, false)
                        ? View.VISIBLE : View.GONE);

        if (reviewsViewModel.isLoaded())
        {
            itemId = reviewsViewModel.getItemId();
            showRatingStats(reviewsViewModel.getRating_stats());

            //if the user had expanded all reviews show the full list
            if (!savedInstanceState.getBoolean(KEY_SEE_ALL_ENABLED, true))
            {
                showAllReviews();
            }
            else
            {
                showTopThreeReviews(reviewsViewModel.getReviews());
            }
        }

        //restore RecyclerView scroll position
        Parcelable recyclerState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
        if (recyclerState != null) {
            rvReviews.post(() -> {
                if (rvReviews.getLayoutManager() != null) {
                    rvReviews.getLayoutManager().onRestoreInstanceState(recyclerState);
                }
            });
        }
    }

    public void bindData()
    {
        if (reviewsViewModel == null || reviewsViewModel.getItemId() == null)
        {
            return;
        }
        itemId = reviewsViewModel.getItemId();
        showRatingStats(reviewsViewModel.getRating_stats());
        showUserReview(reviewsViewModel.getUser_review());
        showTopThreeReviews(reviewsViewModel.getReviews());
    }

    private void showUserReview(Review review)
    {
        if (review != null)
        {
            ratingBarUser.setRating(review.getRating());
            etComment.setText(review.getComment());
            btnSendReview.setText("Update Review");
            btnDeleteReview.setVisibility(View.VISIBLE);
        }
    }

    private void showTopThreeReviews(ArrayList<Review> reviews)
    {
        reviewsList.clear();
        reviewsList.addAll(reviews.subList(0, Math.min(3, reviews.size())));
        adpReviews.notifyDataSetChanged();
    }

    public void showAllReviews()
    {
        reviewsList.clear();
        reviewsList.addAll(reviewsViewModel.getReviews());
        adpReviews.notifyDataSetChanged();
        btnSeeAllReviews.setEnabled(false);
    }

    private void showRatingStats(RatingStats ratingStats)
    {
        TextView tvAvgRating = view.findViewById(R.id.tvAvgRating);
        TextView tvReviewCount = view.findViewById(R.id.tvReviewCount);
        ProgressBar bar5 = view.findViewById(R.id.bar5);
        ProgressBar bar4 = view.findViewById(R.id.bar4);
        ProgressBar bar3 = view.findViewById(R.id.bar3);
        ProgressBar bar2 = view.findViewById(R.id.bar2);
        ProgressBar bar1 = view.findViewById(R.id.bar1);

        String avgRating = String.format("%.1f", ratingStats.getAvg_rating());
        int totalReviews = ratingStats.getTotal_reviews();

        tvAvgRating.setText(avgRating);
        tvReviewCount.setText(String.valueOf(totalReviews));

        bar5.setMax(totalReviews);
        bar5.setProgress(ratingStats.getStars_5());

        bar4.setMax(totalReviews);
        bar4.setProgress(ratingStats.getStars_4());

        bar3.setMax(totalReviews);
        bar3.setProgress(ratingStats.getStars_3());

        bar2.setMax(totalReviews);
        bar2.setProgress(ratingStats.getStars_2());

        bar1.setMax(totalReviews);
        bar1.setProgress(ratingStats.getStars_1());
    }

    private void addReviewToBook()
    {
        if (itemId == null) {
            Toast.makeText(getContext(),
                    "An error occurred, please try again later",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = (int)ratingBarUser.getRating();
        if (rating == 0)
        {
            ratingBarUser.requestFocus();
            Toast.makeText(getContext(),
                    "Please rate the book", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etComment.getText().toString();
        if (comment.length() == 0)
        {
            etComment.setError("comment can't be empty");
            return;
        }

        ReviewsApiService.postReview(itemId,
                new PostReviewRequest(Uid, username, comment, rating),
                new ApiCallback()
                {
                    @Override
                    public void onPostReviewSucceeded(RatingStats ratingStats) {
                        if (isAdded()) {
                            btnSendReview.setText("Update Review");
                            ratingBarUser.clearFocus();
                            btnDeleteReview.setVisibility(View.VISIBLE);
                            showRatingStats(ratingStats);
                            Toast.makeText(getContext(),
                                    "Your comment has been posted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPostReviewFailed(String err) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteReviewFromBook()
    {
        if (itemId == null)
        {
            Toast.makeText(getContext(),
                    "An error occurred, please try again later",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewsApiService.deleteReview(itemId, Uid, new ApiCallback()
        {
            @Override
            public void onDeleteReviewSucceeded(RatingStats ratingStats)
            {
                if (isAdded())
                {
                    ratingBarUser.setRating(0);
                    etComment.setText("");
                    btnSendReview.setText("Post Review");
                    btnDeleteReview.setVisibility(View.GONE);
                    showRatingStats(ratingStats);
                    Toast.makeText(getContext(),
                            "Your comment has been deleted",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteReviewFailed(String err)
            {
                if (isAdded())
                {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}