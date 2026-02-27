package com.libookproject.libookapp;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private SearchFragment searchFragment= new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
//        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment())
//                .addToBackStack(null)
//                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, searchFragment, "searchFragment")
                //.add(R.id.frameLayout, fragmentB, "B")
                //.hide(fragmentB)
                .commit();
    }

    private void init()
    {
        frameLayout = findViewById(R.id.frameLayout);
    }
}