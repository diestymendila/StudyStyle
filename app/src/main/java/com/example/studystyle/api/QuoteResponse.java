package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuoteResponse {
    @SerializedName("results") private List<Quote> results;
    @SerializedName("count")   private int count;
    public List<Quote> getResults() { return results; }
    public int getCount()           { return count; }
}
