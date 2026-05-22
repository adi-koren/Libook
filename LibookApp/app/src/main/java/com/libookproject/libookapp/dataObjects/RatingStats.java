package com.libookproject.libookapp.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * represents the rating statistics for a book or a post.
 * holds the average rating, total review count, and the stars rate division.
 * implements Parcelable to allow saving the object state in onSaveInstanceState,
 */
public class RatingStats implements Parcelable
{
    @SerializedName("avg_rating")
    private double avg_rating;
    @SerializedName("total_reviews")
    private int total_reviews;
    @SerializedName("stars_5")
    private int stars_5;
    @SerializedName("stars_4")
    private int stars_4;
    @SerializedName("stars_3")
    private int stars_3;
    @SerializedName("stars_2")
    private int stars_2;
    @SerializedName("stars_1")
    private int stars_1;

    /**
     * constructs a new RatingStats with all rating data.
     * @param avg_rating    the average rating.
     * @param total_reviews the total number of reviews.
     * @param stars_5       the number of 5 star reviews.
     * @param stars_4       the number of 4 star reviews.
     * @param stars_3       the number of 3 star reviews.
     * @param stars_2       the number of 2 star reviews.
     * @param stars_1       the number of 1 star reviews.
     */
    public RatingStats(double avg_rating, int total_reviews, int stars_5, int stars_4, int stars_3, int stars_2, int stars_1) {
        this.avg_rating = avg_rating;
        this.total_reviews = total_reviews;
        this.stars_5 = stars_5;
        this.stars_4 = stars_4;
        this.stars_3 = stars_3;
        this.stars_2 = stars_2;
        this.stars_1 = stars_1;
    }

    // getters
    public double getAvg_rating() {
        return avg_rating;
    }

    public int getTotal_reviews() {
        return total_reviews;
    }

    public int getStars_5() {
        return stars_5;
    }

    public int getStars_4() {
        return stars_4;
    }

    public int getStars_3() {
        return stars_3;
    }

    public int getStars_2() {
        return stars_2;
    }

    public int getStars_1() {
        return stars_1;
    }

    /**
     * reconstructs a RatingStats object from a Parcel.
     * reads each field in the same order they were written in writeToParcel.
     * @param in the Parcel containing the serialized data.
     */
    protected RatingStats(Parcel in) {
        avg_rating = in.readDouble();
        total_reviews = in.readInt();
        stars_5 = in.readInt();
        stars_4 = in.readInt();
        stars_3 = in.readInt();
        stars_2 = in.readInt();
        stars_1 = in.readInt();
    }

    /**
     * required by Parcelable. creates RatingStats instances from a Parcel or an array of them.
     */
    public static final Creator<RatingStats> CREATOR = new Creator<RatingStats>() {
        @Override
        public RatingStats createFromParcel(Parcel in) {
            return new RatingStats(in);
        }

        @Override
        public RatingStats[] newArray(int size) {
            return new RatingStats[size];
        }
    };

    /**
     * serializes the RatingStats fields into a Parcel.
     * fields must be written in the same order they are read in the Parcel constructor.
     * @param dest  the Parcel to write data into.
     * @param flags additional flags (not used here).
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(avg_rating);
        dest.writeInt(total_reviews);
        dest.writeInt(stars_5);
        dest.writeInt(stars_4);
        dest.writeInt(stars_3);
        dest.writeInt(stars_2);
        dest.writeInt(stars_1);
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
