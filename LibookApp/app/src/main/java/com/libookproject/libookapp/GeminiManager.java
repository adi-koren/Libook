package com.libookproject.libookapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class GeminiManager
{
    private static GeminiManager instance;
    private GenerativeModel gemini;
    private final String TAG = "GeminiManager";

    private GeminiManager()
    {
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.Gemini_API_Key
        );
    }

    public static GeminiManager getInstance()
    {
        if (instance == null)
        {
            instance = new GeminiManager();
        }
        return instance;
    }

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
