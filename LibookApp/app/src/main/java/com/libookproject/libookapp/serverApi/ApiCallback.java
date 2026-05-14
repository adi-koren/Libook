package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.Book;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.LitePost;
import com.libookproject.libookapp.Post;
import com.libookproject.libookapp.RatingStats;

import java.util.List;

public interface ApiCallback<T>
{
    //default void onSuccess(T result) {}

    default void onBookInfoLoaded(Book book) {}
    default void onBookInfoError(String err) {}

    default void onSearchResultsLoaded(List<LiteBook> books) {}
    default void onSearchResultsError(String err) {}

    default void onPostReviewSucceeded(RatingStats ratingStats) {}
    default void onPostReviewFailed(String err) {}

    default void onDeleteReviewSucceeded(RatingStats ratingStats) {}
    default void onDeleteReviewFailed(String err) {}

    default void onSearchPostsLoaded(List<LitePost> posts) {}
    default void onSearchPostsError(String err) {}

    default void onPostInfoLoaded(Post post) {}
    default void onPostInfoError(String err) {}

    default void onGetUserPostsSucceeded(List<LitePost> posts) {}
    default void onGetUserPostsFailed(String err) {}

    default void onPublishPostSucceeded(int postId) {}
    default void onPublishPostFailed(String err) {}

    default void onDeletePostSucceeded() {}
    default void onDeletePostFailed(String err) {}
}
