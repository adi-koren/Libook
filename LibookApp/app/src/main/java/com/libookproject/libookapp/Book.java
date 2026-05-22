package com.libookproject.libookapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a full book object with all details, including reviews and rating statistics.
 * used when displaying a book's full detail page.
 * implements Parcelable to allow saving the object state in onSaveInstanceState.
 */
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

    /**
     * constructs a new Book with all available details.
     * @param id           the identifier of the book.
     * @param title        the title of the book.
     * @param authors      a list of the book's authors.
     * @param image        a URL string of the book's cover image.
     * @param description  the description of the book.
     * @param subjects     the categories or subjects of the book.
     * @param reviews      a list of all reviews for this book.
     * @param user_review  the current user's review, or null if they haven't reviewed it.
     * @param rating_stats The rating statistics for this book.
     */
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

    // getters
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

    /**
     * reconstructs a Book object from a Parcel.
     * reads each field in the same order they were written in writeToParcel.
     * @param in the Parcel containing the book's serialized data.
     */
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

    /**
     * required by Parcelable. creates Book instances from a Parcel or an array of them.
     */
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

    /**
     * serializes the Book's fields into a Parcel.
     * fields must be written in the same order they are read in the Parcel constructor.
     * @param dest  the Parcel to write data into.
     * @param flags additional flags (not used here).
     */
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

    /**
     * describes special objects contained in the Parcelable (none in here).
     * @return always returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}
