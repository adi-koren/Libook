package com.libookproject.libookapp.screens;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.libookproject.libookapp.R;

public class MasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        attachModeMenu();
    }

    private void attachModeMenu() {
        ViewGroup rootView = findViewById(android.R.id.content);

        View menuView = getLayoutInflater().inflate(R.layout.mode_menu_button, rootView, false);
        rootView.addView(menuView);

        menuView.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);

            popup.getMenuInflater().inflate(R.menu.mode_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> onMenuItemSelected(item.getItemId()));

            popup.show();
        });
    }

    private boolean onMenuItemSelected(int id)
    {
        if (id == R.id.menuLightMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        }
        else if (id == R.id.menuDarkMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            return true;
        }
        return false;
    }
}