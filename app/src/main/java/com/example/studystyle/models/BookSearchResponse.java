package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookSearchResponse {
    @SerializedName("docs")
    private List<BookItem> docs;

    @SerializedName("numFound")
    private int numFound;

    public List<BookItem> getDocs() { return docs; }
    public int getNumFound()        { return numFound; }
}