package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.dataObjects.Book;
import com.libookproject.libookapp.dataObjects.LiteBook;
import com.libookproject.libookapp.dataObjects.LitePost;
import com.libookproject.libookapp.dataObjects.Post;
import com.libookproject.libookapp.dataObjects.RatingStats;

import java.util.List;

/**
 * generic callback interface used by all API service classes to return
 * results or errors asynchronously to the calling fragment or activity.
 * all methods have default no-op implementations so that callers only
 * need to override the methods relevant to their specific request.
 */
public interface ApiCallback<T>
{
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
