package com.libookproject.libookapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SearchRequest implements Parcelable
{
    private String q;
    private Map<String, String> q_inter;
    private int startIndex;

    public SearchRequest(String q, Map<String, String> q_inter, int startIndex) {
        this.q = q;
        this.q_inter = q_inter;
        this.startIndex = startIndex;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Map<String, String> getQ_inter() {
        return q_inter;
    }

    public void setQ_inter(Map<String, String> q_inter) {
        this.q_inter = q_inter;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    protected SearchRequest(Parcel in) {
        q = in.readString();
        startIndex = in.readInt();
        // read the map: written as key-value string pairs
        int size = in.readInt();
        if (size >= 0) {
            q_inter = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String key = in.readString();
                String value = in.readString();
                q_inter.put(key, value);
            }
        }
    }

    public static final Creator<SearchRequest> CREATOR = new Creator<SearchRequest>()
    {
        @Override
        public SearchRequest createFromParcel(Parcel in)
        {
            return new SearchRequest(in);
        }

        @Override
        public SearchRequest[] newArray(int size)
        {
            return new SearchRequest[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeInt(startIndex);
        if (q_inter == null)
        {
            dest.writeInt(-1);
        }
        else
        {
            dest.writeInt(q_inter.size());
            for (Map.Entry<String, String> entry : q_inter.entrySet())
            {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
