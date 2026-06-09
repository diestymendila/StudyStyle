package com.example.studystyle.api;

import com.example.studystyle.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpClient buildRapidApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // BODY untuk debug response API
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("x-rapidapi-key",  Constants.RAPIDAPI_KEY)
                            .header("x-rapidapi-host", Constants.RAPIDAPI_HOST)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
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
                    .client(buildRapidApiClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitBooks.create(BookApiService.class);
    }

    public static void resetBookClient() {
        retrofitBooks = null;
    }
}