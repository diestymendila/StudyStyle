package com.example.studystyle.api;

import com.example.studystyle.models.BookSearchResponse;
import com.example.studystyle.models.BookDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {


    @GET("search.json")
    Call<BookSearchResponse> searchBooks(
            @Query("q")      String query,
            @Query("limit")  int limit,
            @Query("fields") String fields
    );

    @GET("works/{workId}.json")
    Call<BookDetail> getBookDetail(@Path("workId") String workId);
}