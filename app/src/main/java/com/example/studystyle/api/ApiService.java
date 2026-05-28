package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Get random quote
    @GET("random")
    Call<Quote> getRandomQuote();

    // Get quotes by tag
    @GET("quotes")
    Call<QuoteResponse> getQuotesByTag(
            @Query("tags") String tags,
            @Query("limit") int limit
    );
}
