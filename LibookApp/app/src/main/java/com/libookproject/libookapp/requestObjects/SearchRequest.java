package com.libookproject.libookapp.requestObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a book search request, holding all the parameters needed to perform a search.
 * contains a general query string, optional advanced search filters, and a pagination index.
 * implements Parcelable to allow saving the object state in onSaveInstanceState.
 */
public class SearchRequest implements Parcelable
{
    private String q;
    private Map<String, String> q_inter;
    private int startIndex;

    /**
     * constructs a new SearchRequest.
     * @param q          the main search query string.
     * @param q_inter    a map of advanced search filters ("intitle"->"Harry Potter").
     *                   Can be null if there are no filters.
     * @param startIndex the index of the first result to fetch (used for pagination).
     */
    public SearchRequest(String q, Map<String, String> q_inter, int startIndex)
    {
        this.q = q;
        this.q_inter = q_inter;
        this.startIndex = startIndex;
    }

    // getters and setters
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

    /**
     * reconstructs a SearchRequest object from a Parcel.
     * the q_inter map is read by first reading its size, then reading each key value pair.
     * a size of -1 mean the map was null when saved.
     * @param in the Parcel containing the serialized data.
     */
    protected SearchRequest(Parcel in)
    {
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

    /**
     * required by Parcelable. creates SearchRequest instances from a Parcel or an array of them.
     */
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

    /**
     * serializes the SearchRequest fields into a Parcel.
     * the q_inter map is written by first writing its size (-1 if null),
     * then writing each key value pair individually.
     * @param dest  the Parcel to write data into.
     * @param flags additional flags (not used here).
     */
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

    /**
     * describes special objects contained in the Parcelable (none in here).
     * @return always returns 0.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }
}
