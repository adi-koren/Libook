package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.LitePost;
import com.libookproject.libookapp.Post;
import com.libookproject.libookapp.PublishPostRequest;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * service class responsible for all community post related API calls.
 * provides static methods that wrap Retrofit calls and transfer results
 * or errors to the provided ApiCallback.
 */
public class CommunityApiService
{
    public static void searchPosts(String q, int startIndex, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().searchPosts(q, startIndex).enqueue(new Callback<List<LitePost>>() {
            @Override
            public void onResponse(Call<List<LitePost>> call, Response<List<LitePost>> response)
            {
                if (response.isSuccessful())
                {
                    callback.onSearchPostsLoaded(response.body());
                }
                else
                {
                    callback.onSearchPostsError(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<LitePost>> call, Throwable t) {
                callback.onSearchPostsError(t.getLocalizedMessage());
            }
        });
    }

    public static void getPostInfo(String postId, String userId, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().getPostInfo(postId, userId).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response)
            {
                if (response.isSuccessful())
                {
                    callback.onPostInfoLoaded(response.body());
                }
                else
                {
                    callback.onPostInfoError(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                callback.onPostInfoError(t.getLocalizedMessage());
            }
        });
    }

    public static void getUserPosts(String userId, ApiCallback callback) {
        RetrofitInstance.getApiInterface().getUserPosts(userId).enqueue(new Callback<List<LitePost>>() {
            @Override
            public void onResponse(Call<List<LitePost>> call, Response<List<LitePost>> response) {
                if (response.isSuccessful())
                {
                    callback.onGetUserPostsSucceeded(response.body());
                }
                else
                {
                    callback.onGetUserPostsFailed(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<LitePost>> call, Throwable t) {
                callback.onGetUserPostsFailed(t.getLocalizedMessage());
            }
        });
    }

    public static void publishPost(PublishPostRequest post, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().publishPost(post).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                if (response.isSuccessful())
                {
                    callback.onPublishPostSucceeded(response.body());
                }
                else
                {
                    callback.onPublishPostFailed(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onPublishPostFailed(t.getLocalizedMessage());
            }
        });
    }


    public static void deletePost(String postId, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().deletePost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if (response.isSuccessful())
                {
                    callback.onDeletePostSucceeded();
                }
                else
                {
                    callback.onDeletePostFailed(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onDeletePostFailed(t.getLocalizedMessage());
            }
        });
    }





    private static String getErrorMessage(Response response)
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
        return message;
    }
}
