package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookResponse {

    @SerializedName("books")
    private List<BookItem> books;

    @SerializedName("data")
    private List<BookItem> data;

    @SerializedName("results")
    private List<BookItem> results;

    // Coba semua kemungkinan field yang dikembalikan API
    public List<BookItem> getBooks() {
        if (books   != null && !books.isEmpty())   return books;
        if (data    != null && !data.isEmpty())    return data;
        if (results != null && !results.isEmpty()) return results;
        return null;
    }
}