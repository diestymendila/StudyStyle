package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("_id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("author")
    private String author;

    public Quote() {}

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
}
