package com.libookproject.libookapp;

import com.google.gson.annotations.SerializedName;

public class RatingStats
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

    public RatingStats(double avg_rating, int total_reviews, int stars_5, int stars_4, int stars_3, int stars_2, int stars_1) {
        this.avg_rating = avg_rating;
        this.total_reviews = total_reviews;
        this.stars_5 = stars_5;
        this.stars_4 = stars_4;
        this.stars_3 = stars_3;
        this.stars_2 = stars_2;
        this.stars_1 = stars_1;
    }

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
}
