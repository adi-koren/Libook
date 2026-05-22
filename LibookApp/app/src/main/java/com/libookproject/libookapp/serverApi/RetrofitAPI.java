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

/**
 * Retrofit interface defining all HTTP endpoints of the Libook server API.
 * each method maps to a specific endpoint and HTTP method.
 * used by the service classes (BooksApiService, CommunityApiService, ReviewsApiService)
 * to make network requests through RetrofitInstance.
 */
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

    @GET("/community/user/{user_id}")
    Call<List<LitePost>> getUserPosts(@Path("user_id") String userId);

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
