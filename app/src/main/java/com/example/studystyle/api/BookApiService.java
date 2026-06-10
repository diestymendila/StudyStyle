package com.example.studystyle.api;

import com.example.studystyle.models.BookSearchResponse;
import com.example.studystyle.models.BookDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {

    // Open Library Search: GET /search.json?q=<query>&limit=10&fields=key,title,author_name,first_publish_year,subject,cover_i
    @GET("search.json")
    Call<BookSearchResponse> searchBooks(
            @Query("q")      String query,
            @Query("limit")  int limit,
            @Query("fields") String fields
    );

    // Open Library Works detail (untuk sinopsis): GET /works/{workId}.json
    @GET("works/{workId}.json")
    Call<BookDetail> getBookDetail(@Path("workId") String workId);
}