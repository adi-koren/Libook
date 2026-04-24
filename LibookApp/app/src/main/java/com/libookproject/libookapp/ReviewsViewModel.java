package com.libookproject.libookapp;

import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewsViewModel extends ViewModel
{
    private String itemId;
    private ArrayList<Review> reviews;
    private Review user_review;
    private RatingStats rating_stats;
    private boolean isLoaded = false;

    public void clear()
    {
        itemId = null;
        reviews = null;
        user_review = null;
        rating_stats = null;
        isLoaded = false;
    }

    public void setFields(String itemId, ArrayList<Review> reviews,
                          Review user_review, RatingStats rating_stats)
    {
        this.itemId = itemId;
        this.reviews = reviews;
        this.user_review = user_review;
        this.rating_stats = rating_stats;
        this.isLoaded = true;
    }

    public String getItemId()
    {
        return itemId;
    }

    public ArrayList<Review> getReviews()
    {
        return reviews;
    }

    public Review getUser_review() {
        return user_review;
    }

    public RatingStats getRating_stats() {
        return rating_stats;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }
}
