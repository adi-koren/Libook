package com.libookproject.libookapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post implements Parcelable
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

    protected Post(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        username = in.readString();
        headline = in.readString();
        content = in.readString();
        created_at = in.readString();
        reviews = in.createTypedArrayList(Review.CREATOR);
        user_review = in.readParcelable(Review.class.getClassLoader());
        rating_stats = in.readParcelable(RatingStats.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(headline);
        dest.writeString(content);
        dest.writeString(created_at);
        dest.writeTypedList(reviews);
        dest.writeParcelable(user_review, flags);
        dest.writeParcelable(rating_stats, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
