package com.libookproject.libookapp;

import static com.libookproject.libookapp.FBRef.refAuth;

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

public class AuthActivity extends AppCompatActivity
{
    private TextView tVMode;
    private EditText eTEmail;
    private EditText eTPass;
    private Button btn;
    private TextView tVMsg;
    private TextView tvSwitchMode;
    private boolean isSignInMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        init();
        updateSwitchText();
    }

    private void init()
    {
        tVMode = findViewById(R.id.tVMode);
        eTEmail = findViewById(R.id.eTEmail);
        eTPass = findViewById(R.id.eTPass);
        btn = findViewById(R.id.btn);
        tVMsg = findViewById(R.id.tVMsg);
        tvSwitchMode = findViewById(R.id.tvSwitchMode);
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
                ds.setUnderlineText(false); // set true if you want underline
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

    private void switchMode()
    {
        isSignInMode = !isSignInMode;

        if (isSignInMode) {
            tVMode.setText("Login");
            btn.setText("Sign In");
        } else {
            tVMode.setText("Register");
            btn.setText("Sign Up");
        }

        updateSwitchText();
    }

    public void auth(View view)
    {
        String email = eTEmail.getText().toString();
        String pass = eTPass.getText().toString();
        if (email.isEmpty() || pass.isEmpty())
        {
            tVMsg.setText("Please fill all fields");
        }
        else
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Connecting to database...");
            pd.show();

            Task<AuthResult> task;

            if (isSignInMode) {
                task = refAuth.signInWithEmailAndPassword(email, pass);
            } else {
                task = refAuth.createUserWithEmailAndPassword(email, pass);
            }

            task.addOnCompleteListener(this, t -> {
                pd.dismiss();

                if (t.isSuccessful())
                {
                    FirebaseUser user = refAuth.getCurrentUser();

                    if (isSignInMode) {
                        tVMsg.setText("Signed in successfully\nUid: " + user.getUid());
                    } else {
                        tVMsg.setText("User created successfully\nUid: " + user.getUid());
                    }

                    Intent intent = new Intent(this, MainActivity.class);
//                    intent.putExtra("userID", user.getUid());
                    startActivity(intent);
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
}