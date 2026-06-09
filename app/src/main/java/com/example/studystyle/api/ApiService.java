package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {

    // ZenQuotes — returns List<Quote> directly with fields "q" and "a"
    @GET("api/quotes")
    Call<List<Quote>> getQuotes();

    // Quotable API - random quote (kept for compatibility)
    @GET("random")
    Call<Quote> getRandomQuote();

    // Quotable API - multiple quotes
    @GET("quotes")
    Call<QuoteResponse> getMultipleQuotes(
            @Query("limit") int limit
    );
}