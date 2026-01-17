package com.libookproject.libookapp;

import java.util.List;

public interface ApiCallback<T>
{
    default void onSuccess(T result) {}

    default void onBookInfoLoaded(Book book) {}
    default void onBookInfoError(String err) {}

    default void onSearchResultsLoaded(List<LiteBook> books) {}
    default void onSearchResultsError(String err) {}

    //default void onCommunityPostsLoaded(List<Post> posts) {}
}
