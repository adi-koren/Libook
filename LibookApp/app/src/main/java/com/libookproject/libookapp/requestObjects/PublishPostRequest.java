package com.libookproject.libookapp.requestObjects;

public class PublishPostRequest
{
    private String user_id;
    private String username;
    private String headline;
    private String content;

    public PublishPostRequest(String user_id, String username, String headline, String content)
    {
        this.user_id = user_id;
        this.username = username;
        this.headline = headline;
        this.content = content;
    }
}
