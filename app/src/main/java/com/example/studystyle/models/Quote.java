package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;

public class Quote {
    // ZenQuotes API fields
    @SerializedName("q") private String q;
    @SerializedName("a") private String a;

    // Quotable API fields (backward-compat)
    @SerializedName("_id")     private String id;
    @SerializedName("content") private String content;
    @SerializedName("author")  private String author;

    public Quote() {}
    public Quote(String content, String author) {
        this.content = content;
        this.author  = author;
    }

    public String getId() { return id; }

    /** Returns content from either ZenQuotes (q) or Quotable (content) */
    public String getContent() {
        if (q != null && !q.isEmpty()) return q;
        return content != null ? content : "";
    }

    /** Returns author from either ZenQuotes (a) or Quotable (author) */
    public String getAuthor() {
        if (a != null && !a.isEmpty()) return a;
        return author != null ? author : "";
    }
}