package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookItem {


    @SerializedName("key")
    private String key;

    @SerializedName("title")
    private String title;


    @SerializedName("author_name")
    private List<String> authorName;


    @SerializedName("first_publish_year")
    private Integer firstPublishYear;


    @SerializedName("cover_i")
    private Integer coverId;


    @SerializedName("subject")
    private List<String> subject;


    private String synopsis;


    private String cachedAuthor;
    private String cachedYear;
    private String cachedGenre;
    private String cachedCover;

    // ── Getters ──

    public String getKey() {
        if (key == null) return "";

        return key.replace("/works/", "");
    }

    public String getTitle() {
        return title != null ? title : "Judul tidak tersedia";
    }

    public String getAuthor() {
        if (cachedAuthor != null && !cachedAuthor.isEmpty()) return cachedAuthor;
        if (authorName != null && !authorName.isEmpty()) {
            return authorName.get(0);
        }
        return "Unknown Author";
    }

    public String getYear() {
        if (cachedYear != null && !cachedYear.isEmpty()) return cachedYear;
        return firstPublishYear != null ? String.valueOf(firstPublishYear) : "";
    }

    public String getGenre() {
        if (cachedGenre != null && !cachedGenre.isEmpty()) return cachedGenre;
        if (subject != null && !subject.isEmpty()) {

            for (String s : subject) {
                if (s.length() <= 30) return s;
            }
            return subject.get(0);
        }
        return "Education";
    }

    public String getCover() {
        if (cachedCover != null && !cachedCover.isEmpty()) return cachedCover;
        if (coverId != null && coverId > 0) {

            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return "";
    }

    public String getSynopsis() {
        return synopsis != null ? synopsis : "";
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }


    public void setFavoriteFields(String key, String title, String author,
                                  String year, String genre, String cover) {
        this.key          = key;
        this.title        = title;
        this.cachedAuthor = author;
        this.cachedYear   = year;
        this.cachedGenre  = genre;
        this.cachedCover  = cover;
    }
}