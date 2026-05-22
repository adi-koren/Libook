package com.libookproject.libookapp.requestObjects;

/**
 * represents the request body sent to the server when a user publish a book/post review.
 * holds all the data required by the API to create a new review.
 */
public class PostReviewRequest {
    private String user_id;
    private String username;
    private String comment;
    private int rating;

    /**
     * constructs a new PostReviewRequest with all required fields.
     * @param user_id  the identifier of the user publishing the review.
     * @param username the username of the reviewer.
     * @param comment  the written review text.
     * @param rating   the rating given by the user.
     */    public PostReviewRequest(String user_id, String username, String comment, int rating) {
        this.user_id = user_id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    // getters and setters
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
