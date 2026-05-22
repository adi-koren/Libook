package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.refUsers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.libookproject.libookapp.FBRef;
import com.libookproject.libookapp.R;

/**
 * activity responsible for user authentication - both Sign In and Sign Up.
 * communicates with Firebase Authentication and Realtime Database to
 * authenticate users and set up their account data on first registration.
 * extends MasterActivity to inherit the light/dark mode toggle.
 */
public class AuthActivity extends MasterActivity
{
    private TextView tVMode;
    private EditText eTEmail;
    private EditText eTPass;
    private EditText eTUsername;
    private Button btn;
    private TextView tVMsg;
    private TextView tvSwitchMode;
    private boolean isSignInMode = true;
    private boolean isLoading = false;
    private ProgressDialog pd;

    /**
     * initializes the activity layout, UI components, and restores saved state if available.
     * @param savedInstanceState the bundle containing previously saved state, or null if none.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        init();

        if (savedInstanceState != null)
        {
            loadSavedState(savedInstanceState);
        }
        else
        {
            applyModeUI();
        }
    }

    @Override
    protected void onDestroy()
    {
        //dismisses the loading dialog if it is currently showing
        if (pd != null && pd.isShowing())
        {
            pd.dismiss();
        }

        super.onDestroy();
    }

    //initialize all UI elements references from the layout
    private void init()
    {
        tVMode = findViewById(R.id.tVMode);
        eTEmail = findViewById(R.id.eTEmail);
        eTPass = findViewById(R.id.eTPass);
        eTUsername = findViewById(R.id.eTUsername);
        btn = findViewById(R.id.btn);
        tVMsg = findViewById(R.id.tVMsg);
        tvSwitchMode = findViewById(R.id.tvSwitchMode);
    }

    //restores the UI state after the activity is recreated
    private void loadSavedState(Bundle savedInstanceState)
    {
        isSignInMode = savedInstanceState.getBoolean("mode", true);
        tVMsg.setText(savedInstanceState.getString("msg", ""));
        isLoading = savedInstanceState.getBoolean("loading", false);

        applyModeUI();

        //if request was in progress, it reshow the dialog and disable the btn
        if (isLoading)
        {
            btn.setEnabled(false);

            pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Connecting to database...");
            pd.setCancelable(false);
            pd.show();
        }
    }

    //switching the authentication mode (sign in <--> sign up)
    private void switchMode()
    {
        if (isLoading)
        {
            return;
        }
        isSignInMode = !isSignInMode;
        applyModeUI();
    }

    //updates the UI to reflect the current mode
    private void applyModeUI()
    {
        if (isSignInMode)
        {
            tVMode.setText("Login");
            btn.setText("Sign In");
            eTUsername.setVisibility(View.GONE);
        }
        else
        {
            tVMode.setText("Register");
            btn.setText("Sign Up");
            eTUsername.setVisibility(View.VISIBLE);
        }

        updateSwitchText();
    }

    /**
     * updates the mode switch TextView with a partially clickable string.
     * when user click on the clickable string it switch the auth mode
     */
    private void updateSwitchText() {

        String fullText;
        String clickableText;

        if (isSignInMode) {
            fullText = "Don't have an account? Create account";
            clickableText = "Create account";
        } else {
            fullText = "Already have an account? Sign in";
            clickableText = "Sign in";
        }

        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                switchMode();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        int startIndex = fullText.indexOf(clickableText);
        int endIndex = startIndex + clickableText.length();

        spannableString.setSpan(clickableSpan, startIndex, endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSwitchMode.setText(spannableString);
        tvSwitchMode.setMovementMethod(LinkMovementMethod.getInstance());
        tvSwitchMode.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * authenticate the user using Firebase Authentication based on the mode(sign in or sign out)
     */
    public void auth(View view)
    {
        String email = eTEmail.getText().toString();
        String pass = eTPass.getText().toString();
        String username = eTUsername.getText().toString();

        //check if all required fields are filled
        if (email.isEmpty() || pass.isEmpty() || (!isSignInMode && username.isEmpty()))
        {
            tVMsg.setText("Please fill all fields");
        }
        else
        {
            tVMsg.setText("");

            pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Connecting to database...");
            pd.setCancelable(false);
            pd.show();

            isLoading = true;
            btn.setEnabled(false);

            Task<AuthResult> task;

            if (isSignInMode)
            {
                task = refAuth.signInWithEmailAndPassword(email, pass);
            }
            else
            {
                task = refAuth.createUserWithEmailAndPassword(email, pass);
            }

            task.addOnCompleteListener(this, t -> {
                isLoading = false;
                btn.setEnabled(true);

                if (pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }

                if (t.isSuccessful())
                {
                    FirebaseUser user = refAuth.getCurrentUser();
                    if (user != null)
                    {
                        FBRef.Uid = user.getUid();

                        if (!isSignInMode)
                        {
                            finishSignUpSetup(user.getUid(), username);
                        }
                        else
                        {
                            finishSignInSetup(user.getUid());
                        }
                    }
                }
                else
                {
                    authFailed(t.getException());
                }
            });
        }
    }

    //displays the error that occurred in the Firebase authentication
    private void authFailed(Exception exp)
    {
        if (exp instanceof FirebaseAuthInvalidUserException){
            tVMsg.setText("Invalid email address.");
        } else if (exp instanceof FirebaseAuthWeakPasswordException) {
            tVMsg.setText("Password too weak.");
        } else if (exp instanceof FirebaseAuthUserCollisionException) {
            tVMsg.setText("User already exists.");
        } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
            tVMsg.setText("General authentication failure.");
        } else if (exp instanceof FirebaseNetworkException) {
            tVMsg.setText("Network error. Please check your connection and try again.");
        } else {
            tVMsg.setText("An error occurred. Please try again later.");
        }
    }

    /**
     * completes the Sign Up after successful Firebase account creation.
     * saves the username to FBRef, writes the user's data to the Realtime Database,
     * creates a default "favorites" shelf, then navigates to MainActivity.
     */
    private void finishSignUpSetup(String userId, String username)
    {
        FBRef.username = username;

        refUsers.child(userId).child("username").setValue(username);
        refUsers.child(userId).child("Shelves").child("favorites").child("_meta").setValue(true)
                .addOnSuccessListener(aVoid -> System.out.println("Favorites shelf created"))
                .addOnFailureListener(e -> System.out.println("Failed: " + e.getMessage()));
        openMain();
    }

    /**
     * completes the Sign In after successful Firebase authentication.
     * fetches the user's username from the Realtime Database and saves it to FBRef,
     * then navigates to MainActivity.
     */
    private void finishSignInSetup(String userId)
    {
        refUsers.child(userId).child("username")
                .get()
                .addOnSuccessListener(snapshot ->
                {
                    if (snapshot.exists())
                    {
                        FBRef.username = snapshot.getValue(String.class);
                    }
                    else
                    {
                        FBRef.username = "User";
                    }
                    openMain();
                })
                .addOnFailureListener(e ->
                {
                    FBRef.username = "User";
                    openMain();
                });
    }

    //starting MainActivity and closes this activity.
    private void openMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * saves the current state of the activity before it is destroyed,
     * so it can be restored when the activity is recreated.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean("mode", isSignInMode);
        outState.putString("msg", tVMsg.getText().toString());
        outState.putBoolean("loading", isLoading);
    }
}