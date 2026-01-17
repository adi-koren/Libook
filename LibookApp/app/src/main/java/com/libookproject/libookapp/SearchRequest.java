package com.libookproject.libookapp;

import java.util.Map;

public class SearchRequest
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
}
