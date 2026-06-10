package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookSearchResponse {

    @SerializedName("numFound")
    private int numFound;

    @SerializedName("docs")
    private List<BookItem> docs;

    public int getNumFound() { return numFound; }
    public List<BookItem> getDocs() { return docs; }
}