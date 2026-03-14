package com.libookproject.libookapp;

public class PostReviewRequest {
    private String user_id;
    private String username;
    private String comment;
    private int rating;

    public PostReviewRequest(String user_id, String username, String comment, int rating) {
        this.user_id = user_id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
