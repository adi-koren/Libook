package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.Book;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.LitePost;
import com.libookproject.libookapp.Post;
import com.libookproject.libookapp.PostReviewRequest;
import com.libookproject.libookapp.PublishPostRequest;
import com.libookproject.libookapp.RatingStats;
import com.libookproject.libookapp.SearchRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI
{
    //books endpoints
    @POST("/books/search")
    Call<List<LiteBook>> searchBooks(@Body SearchRequest request);

    @GET("/books/{book_id}")
    Call<Book> getBookInfo(@Path("book_id") String bookId,
                           @Query("user_id") String userId);


    //community endpoints
    @GET("/community/search")
    Call<List<LitePost>> searchPosts(@Query("q") String q,
                                     @Query("startIndex") int startIndex);

    @GET("/community/{post_id}")
    Call<Post> getPostInfo(@Path("post_id") String postId,
                           @Query("user_id") String userId);

    @POST("/community/publish")
    Call<Integer> publishPost(@Body PublishPostRequest post);

    @DELETE("/community/{post_id}")
    Call<Void> deletePost(@Path("post_id") String postId);


    //reviews endpoints
    @POST("/reviews/{item_id}")
    Call<RatingStats> postReview(@Path("item_id") String itemId,
                                 @Body PostReviewRequest review);

    @DELETE("/reviews/{item_id}")
    Call<RatingStats> deleteReview(@Path("item_id") String itemId,
                                   @Query("user_id") String userId);
}
