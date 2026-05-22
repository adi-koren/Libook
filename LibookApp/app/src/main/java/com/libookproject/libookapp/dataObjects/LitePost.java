package com.libookproject.libookapp.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LitePost implements Parcelable
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

    protected LitePost(Parcel in) {
        id = in.readString();
        username = in.readString();
        headline = in.readString();
        created_at = in.readString();
    }

    public static final Parcelable.Creator<LitePost> CREATOR = new Parcelable.Creator<LitePost>() {
        @Override
        public LitePost createFromParcel(Parcel in) {
            return new LitePost(in);
        }

        @Override
        public LitePost[] newArray(int size) {
            return new LitePost[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(headline);
        dest.writeString(created_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
