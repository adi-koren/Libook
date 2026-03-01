package com.libookproject.libookapp;

import java.util.List;

public class Book
{
    private String id;
    private String title;
    private List<String> authors;
    private String image;
    private String description;
    private String subjects;

    public Book(String id, String title, List<String> authors,
                String image, String description, String subjects)
    {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.image = image;
        this.description = description;
        this.subjects = subjects;
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
}
