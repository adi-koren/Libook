package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.Book;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.SearchRequest;

import java.util.List;
import org.json.JSONObject;

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
                if (response.isSuccessful())
                {
                    callback.onSearchResultsLoaded(response.body());
                }
                else
                {
                    String message = "HTTP Error " + response.code();

                    try
                    {
                        if (response.errorBody() != null)
                        {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            message = obj.getString("detail");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    callback.onSearchResultsError(message);
                }
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
            public void onResponse(Call<Book> call, Response<Book> response)
            {
                if (response.isSuccessful())
                {
                    callback.onBookInfoLoaded(response.body());
                }
                else
                {
                    String message = "HTTP Error " + response.code();

                    try
                    {
                        if (response.errorBody() != null)
                        {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            message = obj.getString("detail");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    callback.onBookInfoError(message);
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                callback.onBookInfoError(t.getLocalizedMessage());
            }
        });
    }
}
