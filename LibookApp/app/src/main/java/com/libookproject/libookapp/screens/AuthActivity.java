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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AuthActivity extends AppCompatActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        if (pd != null && pd.isShowing())
        {
            pd.dismiss();
        }

        super.onDestroy();
    }

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

    private void loadSavedState(Bundle savedInstanceState)
    {
        eTEmail.setText(savedInstanceState.getString("email", ""));
        eTPass.setText(savedInstanceState.getString("pass", ""));
        eTUsername.setText(savedInstanceState.getString("username", ""));
        isSignInMode = savedInstanceState.getBoolean("mode", true);
        tVMsg.setText(savedInstanceState.getString("msg", ""));
        isLoading = savedInstanceState.getBoolean("loading", false);

        applyModeUI();

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

    private void switchMode()
    {
        if (isLoading)
        {
            return;
        }
        isSignInMode = !isSignInMode;
        applyModeUI();
    }

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
                ds.setUnderlineText(true); // set true if you want underline
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

    public void auth(View view)
    {
        String email = eTEmail.getText().toString();
        String pass = eTPass.getText().toString();
        String username = eTUsername.getText().toString();
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

    private void finishSignUpSetup(String userId, String username)
    {
        FBRef.username = username;

        refUsers.child(userId).child("username").setValue(username);
        refUsers.child(userId).child("Shelves").child("favorites").child("_meta").setValue(true)
                .addOnSuccessListener(aVoid -> System.out.println("Favorites shelf created"))
                .addOnFailureListener(e -> System.out.println("Failed: " + e.getMessage()));
        openMain();
    }

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

    private void openMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("email", eTEmail.getText().toString());
        outState.putString("pass", eTPass.getText().toString());
        outState.putString("username", eTUsername.getText().toString());
        outState.putBoolean("mode", isSignInMode);
        outState.putString("msg", tVMsg.getText().toString());
        outState.putBoolean("loading", isLoading);
    }
}