package com.libookproject.libookapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksApiService
{
    public static void searchBooks(SearchRequest request, ApiCallback callback)
    {
        RetrofitInstance.getBooksApiInterface().searchBooks(request).enqueue(new Callback<List<LiteBook>>() {
            @Override
            public void onResponse(Call<List<LiteBook>> call, Response<List<LiteBook>> response) {
                callback.onSearchResultsLoaded(response.body());
            }

            @Override
            public void onFailure(Call<List<LiteBook>> call, Throwable t) {
                callback.onSearchResultsError(t.getLocalizedMessage());
            }
        });
    }

    public static void getBookInfo(String id, ApiCallback callback)
    {
        RetrofitInstance.getBooksApiInterface().getBookInfo(id).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                callback.onBookInfoLoaded(response.body());
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                callback.onBookInfoError(t.getLocalizedMessage());
            }
        });
    }
}
