package com.libookproject.libookapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable
{
    private String id;
    private String title;
    private List<String> authors;
    private String image;
    private String description;
    private String subjects;

    private ArrayList<Review> reviews;
    private Review user_review;

    private RatingStats rating_stats;

    public Book(String id, String title, List<String> authors, String image, String description, String subjects, ArrayList<Review> reviews, Review user_review, RatingStats rating_stats) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.image = image;
        this.description = description;
        this.subjects = subjects;
        this.reviews = reviews;
        this.user_review = user_review;
        this.rating_stats = rating_stats;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getSubjects() {
        return subjects;
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

    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        authors = in.createStringArrayList();
        image = in.readString();
        description = in.readString();
        subjects = in.readString();
        reviews = in.createTypedArrayList(Review.CREATOR);
        user_review = in.readParcelable(Review.class.getClassLoader());
        rating_stats = in.readParcelable(RatingStats.class.getClassLoader());
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(subjects);
        dest.writeTypedList(reviews);
        dest.writeParcelable(user_review, flags);
        dest.writeParcelable(rating_stats, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
