package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.Book;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.PostReviewRequest;
import com.libookproject.libookapp.SearchRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitBooksAPI
{
    @POST("/books/search")
    Call<List<LiteBook>> searchBooks(@Body SearchRequest request);

    @GET("books/{book_id}")
    Call<Book> getBookInfo(@Path("book_id") String bookId,
                           @Query("user_id") String userId);

    @POST("books/{book_id}/review")
    Call<String> postReview(@Path("book_id") String bookId,
                            @Body PostReviewRequest review);
}
