package com.libookproject.libookapp;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("review_id")
    private String review_id;
    @SerializedName("username")
    private String username;
    @SerializedName("comment")
    private String comment;
    @SerializedName("rating")
    private int rating;
    @SerializedName("created_at")
    private String created_at;

    public Review(String review_id, String username, String comment, int rating, String created_at) {
        this.review_id = review_id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.created_at = created_at;
    }

    public String getReview_id() {
        return review_id;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public String getCreated_at() {
        return created_at;
    }
}
