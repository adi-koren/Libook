package com.libookproject.libookapp.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * represents a light book object used for displaying books in lists and search results.
 * contains only the essential fields needed for previewing a book (id, title, authors, image).
 * implements Parcelable to allow saving the object state in onSaveInstanceState.
 */
public class LiteBook implements Parcelable
{
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("authors")
    private List<String> authors;
    @SerializedName("image")
    private String image;

    /**
     * constructs a new LiteBook with all required fields.
     * @param id      the identifier of the book.
     * @param title   the title of the book.
     * @param authors a list of the book's authors.
     * @param image   a URL string of the book's cover image.
     */
    public LiteBook(String id, String title, List<String> authors, String image) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.image = image;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * reconstructs a LiteBook object from a Parcel (used when passing between components).
     * @param in the Parcel containing the book's serialized data.
     */
    protected LiteBook(Parcel in) {
        id = in.readString();
        title = in.readString();
        authors = in.createStringArrayList();
        image = in.readString();
    }

    /**
     * required by Parcelable. creates LiteBook instances from a Parcel or an array of them.
     */
    public static final Creator<LiteBook> CREATOR = new Creator<LiteBook>() {
        @Override
        public LiteBook createFromParcel(Parcel in) {
            return new LiteBook(in);
        }

        @Override
        public LiteBook[] newArray(int size) {
            return new LiteBook[size];
        }
    };

    /**
     * serializes the LiteBook's fields into a Parcel.
     * @param dest  the Parcel to write data into.
     * @param flags additional flags (not used here).
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeString(image);
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
