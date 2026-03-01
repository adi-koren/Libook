package com.libookproject.libookapp;

public class SavedBook
{
    private String id;
    private String image;

    public SavedBook(String image, String id)
    {
        this.image = image;
        this.id = id;
    }

    public String getImage()
    {
        return image;
    }

    public String getId()
    {
        return id;
    }
}
