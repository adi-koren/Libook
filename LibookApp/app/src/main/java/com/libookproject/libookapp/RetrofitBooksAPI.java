package com.libookproject.libookapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitBooksAPI
{
    @POST("/books/search")
    Call<List<LiteBook>> searchBooks(@Body SearchRequest request);

    @GET("books/{id}")
    Call<Book> getBookInfo(@Path("id") String id);
}
