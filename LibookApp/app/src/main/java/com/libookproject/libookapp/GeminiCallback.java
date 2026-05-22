package com.libookproject.libookapp;

/**
 * callback interface for handling responses from the Gemini AI API.
 * implemented by any component that needs to receive the result of a Gemini prompt.
 */
public interface GeminiCallback
{
    /**
     * called when the Gemini API returns a successful response.
     * @param result the text response from Gemini.
     */
    public void onSuccess(String result);

    /**
     * called when the Gemini API request fails.
     * @param error the throwable containing details about the failure.
     */
    public void onFailure(Throwable error);
}
