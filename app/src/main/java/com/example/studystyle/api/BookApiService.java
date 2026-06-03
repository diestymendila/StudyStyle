package com.example.studystyle.api;

import com.example.studystyle.models.BookDetail;
import com.example.studystyle.models.BookSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {
    // Search buku
    @GET("search.json")
    Call<BookSearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("fields") String fields
    );

    // Detail buku — sinopsis, cover, dll
    @GET("works/{workId}.json")
    Call<BookDetail> getBookDetail(@Path("workId") String workId);
}