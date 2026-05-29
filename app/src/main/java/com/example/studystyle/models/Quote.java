package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("_id")     private String id;
    @SerializedName("content") private String content;
    @SerializedName("author")  private String author;

    public Quote() {}
    public Quote(String content, String author) {
        this.content = content;
        this.author  = author;
    }

    public String getId()      { return id; }
    public String getContent() { return content; }
    public String getAuthor()  { return author; }
}
