package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;

public class BookDetail {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private Object description; // bisa String atau objek {value: "..."}

    @SerializedName("covers")
    private int[] covers;

    public String getTitle() { return title; }

    // Sinopsis bisa berupa String langsung atau objek {value: "..."}
    public String getDescription() {
        if (description == null) return null;
        if (description instanceof String) return (String) description;
        // Jika berupa objek gson LinkedTreeMap
        try {
            if (description instanceof com.google.gson.internal.LinkedTreeMap) {
                Object val = ((com.google.gson.internal.LinkedTreeMap<?, ?>) description).get("value");
                if (val != null) return val.toString();
            }
        } catch (Exception ignored) {}
        return description.toString();
    }

    // Cover ID untuk ambil gambar cover
    public int getCoverId() {
        if (covers != null && covers.length > 0) return covers[0];
        return -1;
    }

    // URL cover buku dari Open Library
    public String getCoverUrl() {
        int id = getCoverId();
        if (id > 0) return "https://covers.openlibrary.org/b/id/" + id + "-M.jpg";
        return null;
    }
}