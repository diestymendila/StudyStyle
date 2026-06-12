package com.example.studystyle.api;

import com.example.studystyle.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ApiService {

    @GET("api/quotes")
    Call<List<Quote>> getQuotes();


    @GET("api/random")
    Call<List<Quote>> getRandomQuote();
}