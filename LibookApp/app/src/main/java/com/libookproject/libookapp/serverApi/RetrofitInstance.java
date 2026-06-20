package com.libookproject.libookapp.serverApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * singleton class responsible for creating and providing the Retrofit instance
 * used for all HTTP communication with the Libook server.
 * uses the singleton to ensure only one Retrofit instance exists throughout the app.
 */
public class RetrofitInstance
{
    private static final String BASE_URL = "https://libook-api.onrender.com";
    private static Retrofit retrofit;

    /**
     * returns the single Retrofit instance, creating it if it doesn't exist yet.
     * configured with Gson for automatic JSON deserialization.
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * creates and returns an implementation of the RetrofitAPI interface
     * using the singleton Retrofit instance.
     */
    public static RetrofitAPI getApiInterface() {
        return getRetrofitInstance().create(RetrofitAPI.class);
    }
}
