package com.libookproject.libookapp.serverApi;

import com.libookproject.libookapp.requestObjects.PostReviewRequest;
import com.libookproject.libookapp.dataObjects.RatingStats;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * service class responsible for all review related API calls.
 * provides static methods that wrap Retrofit calls and transfer results
 * or errors to the provided ApiCallback.
 */
public class ReviewsApiService
{
    public static void postReview(String itemId, PostReviewRequest review, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().postReview(itemId, review).enqueue(new Callback<RatingStats>() {
            @Override
            public void onResponse(Call<RatingStats> call, Response<RatingStats> response)
            {
                if (response.isSuccessful())
                {
                    callback.onPostReviewSucceeded(response.body());
                }
                else
                {
                    callback.onPostReviewFailed(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<RatingStats> call, Throwable t) {
                callback.onPostReviewFailed(t.getLocalizedMessage());
            }
        });
    }


    public static void deleteReview(String itemId, String userId, ApiCallback callback)
    {
        RetrofitInstance.getApiInterface().deleteReview(itemId, userId).enqueue(new Callback<RatingStats>() {
            @Override
            public void onResponse(Call<RatingStats> call, Response<RatingStats> response)
            {
                if (response.isSuccessful())
                {
                    callback.onDeleteReviewSucceeded(response.body());
                }
                else
                {
                    callback.onDeleteReviewFailed(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<RatingStats> call, Throwable t) {
                callback.onDeleteReviewFailed(t.getLocalizedMessage());
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
