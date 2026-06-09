package com.example.studystyle.api;

import com.example.studystyle.models.BookItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface BookApiService {

    // GetBooksInfo API: GET /search?s=<query>
    // Response: JSON array langsung [ {...}, {...} ]
    @GET("search")
    Call<List<BookItem>> searchBooks(@Query("s") String query);
}