package com.libookproject.libookapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post
{
    @SerializedName("post_id")
    private String id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("username")
    private String username;
    @SerializedName("headline")
    private String headline;
    @SerializedName("content")
    private String content;
    @SerializedName("created_at")
    private String created_at;

    @SerializedName("reviews")
    private ArrayList<Review> reviews;
    @SerializedName("user_review")
    private Review user_review;

    @SerializedName("rating_stats")
    private RatingStats rating_stats;

    public Post(String id, String user_id, String username,
                String headline, String content, String created_at,
                ArrayList<Review> reviews, Review user_review,
                RatingStats rating_stats)
    {
        this.id = id;
        this.user_id = user_id;
        this.username = username;
        this.headline = headline;
        this.content = content;
        this.created_at = created_at;
        this.reviews = reviews;
        this.user_review = user_review;
        this.rating_stats = rating_stats;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadline() {
        return headline;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public Review getUser_review() {
        return user_review;
    }

    public RatingStats getRating_stats() {
        return rating_stats;
    }
}
