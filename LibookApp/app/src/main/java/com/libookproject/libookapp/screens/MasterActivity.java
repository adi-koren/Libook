package com.libookproject.libookapp.screens;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.libookproject.libookapp.R;

/**
 * base activity class that all other activities in the app extend.
 * provides shared functionality available across the entire app -
 * a floating light/dark mode menu that is automatically
 * attached to every screen that extends this class.
 */
public class MasterActivity extends AppCompatActivity
{
    /**
     * initializes the activity. called by all subclasses with super.onCreate().
     * @param savedInstanceState the saved state bundle, if the activity is being recreated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        attachModeMenu();
    }

    /**
     * add a floating mode toggle button to the root view of the screen.
     * when clicked, shows a PopupMenu with light mode and dark mode options.
     * this method is called automatically for every activity that extends MasterActivity.
     */
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

    /**
     * handles the selection of a menu item from the mode toggle PopupMenu.
     * applies the selected display mode (light or dark).
     */
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