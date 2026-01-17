package com.libookproject.libookapp;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment())
                .addToBackStack(null)
                .commit();
    }

    private void init()
    {
        frameLayout = findViewById(R.id.frameLayout);
    }
}