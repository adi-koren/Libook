package com.libookproject.libookapp;

public class IndustryIdentifier
{
    private String type;
    private String identifier;

    public IndustryIdentifier(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isIsbn()
    {
        return type == "ISBN_10" || type == "ISBN_13";
    }
}
