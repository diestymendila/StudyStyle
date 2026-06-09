package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;

public class BookItem {

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("year")
    private String year;

    @SerializedName("genre")
    private String genre;

    @SerializedName("synopsis")
    private String synopsis;

    // GetBooksInfo menggunakan field "image" untuk URL cover
    @SerializedName("image")
    private String image;

    // Fallback jika API menggunakan "cover"
    @SerializedName("cover")
    private String cover;

    @SerializedName("id")
    private String id;

    // Fallback: beberapa versi API menggunakan "name" bukan "title"
    @SerializedName("name")
    private String name;

    // Fallback: beberapa versi menggunakan "description" bukan "synopsis"
    @SerializedName("description")
    private String description;

    public String getTitle() {
        if (title != null && !title.isEmpty()) return title;
        if (name  != null && !name.isEmpty())  return name;
        return "";
    }

    public String getAuthor()  { return author   != null ? author   : "Unknown Author"; }
    public String getYear()    { return year     != null ? year     : ""; }
    public String getGenre()   { return genre    != null ? genre    : ""; }
    public String getId()      { return id       != null ? id       : ""; }

    public String getSynopsis() {
        if (synopsis    != null && !synopsis.isEmpty())    return synopsis;
        if (description != null && !description.isEmpty()) return description;
        return "";
    }

    // Coba "image" dulu (GetBooksInfo), fallback ke "cover"
    public String getCover() {
        if (image != null && !image.isEmpty()) return image;
        if (cover != null && !cover.isEmpty()) return cover;
        return "";
    }
}