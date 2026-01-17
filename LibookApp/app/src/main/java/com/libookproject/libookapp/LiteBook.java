package com.libookproject.libookapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiteBook
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
}
