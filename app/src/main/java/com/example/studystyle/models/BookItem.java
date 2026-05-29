package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookItem {
    @SerializedName("title")
    private String title;

    @SerializedName("author_name")
    private List<String> authorName;

    @SerializedName("first_publish_year")
    private Integer firstPublishYear;

    @SerializedName("key")
    private String key;

    public String getTitle() { return title; }
    public String getAuthor() {
        if (authorName != null && !authorName.isEmpty()) return authorName.get(0);
        return "Unknown Author";
    }
    public Integer getFirstPublishYear() { return firstPublishYear; }
    public String getKey() { return key; }

    // URL buku di Open Library
    public String getBookUrl() {
        if (key != null) return "https://openlibrary.org" + key;
        return "";
    }
}