package com.libookproject.libookapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * singleton manager class responsible for all communication with the Gemini AI API.
 * provides a simple way for sending text prompts and receiving AI generated responses.
 * uses the singleton pattern to ensure only one instance and one model connection exists.
 */
public class GeminiManager
{
    private static GeminiManager instance;
    private GenerativeModel gemini;
    private final String TAG = "GeminiManager";

    /**
     * private constructor - initializes the Gemini model with the app's API key.
     * called only once in getInstance().
     */
    private GeminiManager()
    {
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.Gemini_API_Key
        );
    }

    /**
     * returns the single instance of GeminiManager, creating it if it doesn't exist yet.
     * @return the singleton instance of GeminiManager.
     */
    public static GeminiManager getInstance()
    {
        if (instance == null)
        {
            instance = new GeminiManager();
        }
        return instance;
    }

    /**
     * sends a text prompt to the Gemini AI model and returns the response asynchronously
     * on a GeminiCallback.
     * @param prompt   the text prompt to send to the Gemini model.
     * @param callback the callback to invoke with the result or error.
     */
    public void sendTextPrompt(String prompt, GeminiCallback callback)
    {
        gemini.generateContent(prompt,
                new Continuation<GenerateContentResponse>()
                {
                    @NonNull
                    @Override
                    public CoroutineContext getContext()
                    {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object result)
                    {
                        if (result instanceof Result.Failure)
                        {
                            Log.i(TAG, "Error: " + ((Result.Failure) result).exception.getMessage());
                            callback.onFailure(((Result.Failure) result).exception);
                        } else
                        {
                            callback.onSuccess(((GenerateContentResponse) result).getText());
                        }
                    }
                });
    }
}
