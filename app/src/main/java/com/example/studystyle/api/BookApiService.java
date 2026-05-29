package com.example.studystyle.api;

import com.example.studystyle.models.BookSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookApiService {
    // Open Library Search API - gratis, no key, no rate limit ketat
    @GET("search.json")
    Call<BookSearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("fields") String fields
    );
}