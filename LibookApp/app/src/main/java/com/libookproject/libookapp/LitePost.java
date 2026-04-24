package com.libookproject.libookapp;

import com.google.gson.annotations.SerializedName;

public class LitePost
{
    @SerializedName("post_id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("headline")
    private String headline;
    @SerializedName("created_at")
    private String created_at;

    public LitePost(String id, String username, String headline, String created_at)
    {
        this.id = id;
        this.username = username;
        this.headline = headline;
        this.created_at = created_at;
    }

    public String getId()
    {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadline() {
        return headline;
    }

    public String getCreated_at() {
        return created_at;
    }
}
