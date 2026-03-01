package com.libookproject.libookapp.screens;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.libookproject.libookapp.R;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private Fragment libraryFragment;
    private Fragment searchFragment;
    //private Fragment profileFragment;
    private Fragment activeFragment;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
        addNevigationBarListener();
    }

    private void init()
    {
        frameLayout = findViewById(R.id.frameLayout);
        libraryFragment = new LibraryFragment();
        searchFragment = new SearchFragment();
        activeFragment = searchFragment;

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_search);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, libraryFragment, "libraryFragment")
                .hide(libraryFragment)
                .add(R.id.frameLayout, searchFragment, "searchFragment")
                .commit();
    }

    private void addNevigationBarListener()
    {
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_library)
            {
                switchFragment(libraryFragment);
                return true;
            }
            else if (item.getItemId() == R.id.nav_search)
            {
                switchFragment(searchFragment);
                return true;
            }
//            else if (item.getItemId() == R.id.nav_profile)
//            {
//                switchFragment(profileFragment);
//                return true;
//            }
            return false;
        });
    }

    private void switchFragment(Fragment selectedFragment)
    {
        if (selectedFragment == activeFragment)
        {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(selectedFragment)
                .commit();

        activeFragment = selectedFragment;
    }
}