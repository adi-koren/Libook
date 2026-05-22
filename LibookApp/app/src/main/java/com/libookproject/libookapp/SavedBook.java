package com.libookproject.libookapp;

/**
 * represents a saved book in the user's library.
 * contains only the minimal fields needed to display the book in the saved books list.
 */
public class SavedBook
{
    private String id;
    private String image;

    /**
     * constructs a new SavedBook.
     * @param image the URL of the book's cover image.
     * @param id    the identifier of the book.
     */
    public SavedBook(String image, String id)
    {
        this.image = image;
        this.id = id;
    }

    // getters
    public String getImage()
    {
        return image;
    }

    public String getId()
    {
        return id;
    }
}
