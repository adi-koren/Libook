package com.libookproject.libookapp.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * represents a user review for a book or a post, including the rating, comment, and additional fields.
 * implements Parcelable to allow saving the object state in onSaveInstanceState,
 */
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

    /**
     * constructs a new Review with all required fields.
     * @param review_id  the identifier of the review.
     * @param username   the username of the reviewer.
     * @param comment    the written review text.
     * @param rating     the rating given by the reviewer.
     * @param created_at the creation date.
     */
    public Review(String review_id, String username, String comment, int rating, String created_at) {
        this.review_id = review_id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.created_at = created_at;
    }

    // getters
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

    /**
     * reconstructs a Review object from a Parcel.
     * reads each field in the same order they were written in writeToParcel.
     * @param in the Parcel containing the review's serialized data.
     */
    protected Review(Parcel in) {
        review_id = in.readString();
        username = in.readString();
        comment = in.readString();
        rating = in.readInt();
        created_at = in.readString();
    }

    /**
     * required by Parcelable. creates Review instances from a Parcel or an array of them.
     */
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

    /**
     * serializes the Review's fields into a Parcel.
     * fields must be written in the same order they are read in the Parcel constructor.
     * @param dest  the Parcel to write data into.
     * @param flags additional flags (not used here).
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(review_id);
        dest.writeString(username);
        dest.writeString(comment);
        dest.writeInt(rating);
        dest.writeString(created_at);
    }

    /**
     * describes special objects contained in the Parcelable (none in here).
     * @return always returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}
