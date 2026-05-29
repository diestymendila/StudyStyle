package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // Quotable API - random quote
    @GET("random")
    Call<Quote> getRandomQuote();

    // Quotable API - multiple quotes sekaligus
    @GET("quotes")
    Call<QuoteResponse> getMultipleQuotes(
            @Query("limit") int limit
    );
}