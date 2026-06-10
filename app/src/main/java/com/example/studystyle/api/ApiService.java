package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ApiService {

    // ZenQuotes — GET https://zenquotes.io/api/quotes
    // Mengembalikan array JSON: [{"q":"...", "a":"...", "h":"..."}, ...]
    @GET("api/quotes")
    Call<List<Quote>> getQuotes();
}