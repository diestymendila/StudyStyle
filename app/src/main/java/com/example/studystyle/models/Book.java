package com.example.studystyle.models;

/**
 * Model buku dari Open Library API
 */
public class Book {
    private String title;
    private String author;
    private String coverId;  // untuk URL thumbnail: https://covers.openlibrary.org/b/id/{coverId}-M.jpg
    private String openLibraryKey; // /works/OL...
    private int firstPublishYear;

    public Book() {}

    public Book(String title, String author, String coverId, String openLibraryKey, int firstPublishYear) {
        this.title = title;
        this.author = author;
        this.coverId = coverId;
        this.openLibraryKey = openLibraryKey;
        this.firstPublishYear = firstPublishYear;
    }

    public String getTitle()           { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor()              { return author; }
    public void setAuthor(String author)   { this.author = author; }

    public String getCoverId()               { return coverId; }
    public void setCoverId(String coverId)   { this.coverId = coverId; }

    public String getOpenLibraryKey()                    { return openLibraryKey; }
    public void setOpenLibraryKey(String openLibraryKey) { this.openLibraryKey = openLibraryKey; }

    public int getFirstPublishYear()                       { return firstPublishYear; }
    public void setFirstPublishYear(int firstPublishYear)  { this.firstPublishYear = firstPublishYear; }

    /** URL cover buku, pakai default drawable jika tidak ada */
    public String getCoverUrl() {
        if (coverId != null && !coverId.isEmpty() && !coverId.equals("null")) {
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return null;
    }
}
