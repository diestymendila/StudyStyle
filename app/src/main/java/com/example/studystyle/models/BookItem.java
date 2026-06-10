package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookItem {

    // Open Library: key buku, contoh "/works/OL123W"
    @SerializedName("key")
    private String key;

    @SerializedName("title")
    private String title;

    // Open Library: array nama penulis
    @SerializedName("author_name")
    private List<String> authorName;

    // Open Library: tahun terbit pertama
    @SerializedName("first_publish_year")
    private Integer firstPublishYear;

    // Open Library: cover ID (gunakan untuk membangun URL cover)
    @SerializedName("cover_i")
    private Integer coverId;

    // Open Library: subject/genre
    @SerializedName("subject")
    private List<String> subject;

    // Sinopsis — diisi manual setelah fetch detail
    private String synopsis;

    // ── Getters ──

    public String getKey() {
        if (key == null) return "";
        // Ambil hanya bagian ID, contoh "/works/OL123W" → "OL123W"
        return key.replace("/works/", "");
    }

    public String getTitle() {
        return title != null ? title : "Judul tidak tersedia";
    }

    public String getAuthor() {
        if (authorName != null && !authorName.isEmpty()) {
            return authorName.get(0);
        }
        return "Unknown Author";
    }

    public String getYear() {
        return firstPublishYear != null ? String.valueOf(firstPublishYear) : "";
    }

    public String getGenre() {
        if (subject != null && !subject.isEmpty()) {
            // Ambil subjek pertama yang tidak terlalu panjang
            for (String s : subject) {
                if (s.length() <= 30) return s;
            }
            return subject.get(0);
        }
        return "Education";
    }

    public String getCover() {
        if (coverId != null && coverId > 0) {
            // Open Library cover URL: https://covers.openlibrary.org/b/id/{cover_i}-M.jpg
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
}