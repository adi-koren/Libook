package com.libookproject.libookapp;

//import android.os.Parcel;
//import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiteBook //implements Parcelable
{
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("authors")
    private List<String> authors;
    @SerializedName("image")
    private String image;

    public LiteBook(String id, String title, List<String> authors, String image) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.image = image;
    }


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


//    protected LiteBook(Parcel in) {
//        id = in.readString();
//        title = in.readString();
//        authors = in.createStringArrayList();
//        image = in.readString();
//    }
//
//    public static final Creator<LiteBook> CREATOR = new Creator<LiteBook>() {
//        @Override
//        public LiteBook createFromParcel(Parcel in) {
//            return new LiteBook(in);
//        }
//
//        @Override
//        public LiteBook[] newArray(int size) {
//            return new LiteBook[size];
//        }
//    };
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(title);
//        dest.writeStringList(authors);
//        dest.writeString(image);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
}
