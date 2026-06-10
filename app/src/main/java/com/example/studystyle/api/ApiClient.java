package com.example.studystyle.api;

import com.example.studystyle.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit retrofitQuote = null;
    private static Retrofit retrofitBooks = null;

    private static OkHttpClient buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public static ApiService getApiService() {
        if (retrofitQuote == null) {
            retrofitQuote = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL_QUOTE)
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitQuote.create(ApiService.class);
    }

    public static BookApiService getBookApiService() {
        if (retrofitBooks == null) {
            retrofitBooks = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL_BOOKS)
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitBooks.create(BookApiService.class);
    }

    public static void resetBookClient() {
        retrofitBooks = null;
    }
}