package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ApiService {

    // ZenQuotes /api/quotes — rate-limit ketat (~1x/hari per IP), hindari untuk reload
    @GET("api/quotes")
    Call<List<Quote>> getQuotes();

    // ZenQuotes /api/random — 1 quote acak, rate-limit jauh lebih longgar
    // Gunakan ini untuk fetch per-session
    @GET("api/random")
    Call<List<Quote>> getRandomQuote();
}