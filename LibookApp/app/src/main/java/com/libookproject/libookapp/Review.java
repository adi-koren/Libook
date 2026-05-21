package com.libookproject.libookapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {
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

    protected Review(Parcel in) {
        review_id = in.readString();
        username = in.readString();
        comment = in.readString();
        rating = in.readInt();
        created_at = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(review_id);
        dest.writeString(username);
        dest.writeString(comment);
        dest.writeInt(rating);
        dest.writeString(created_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
