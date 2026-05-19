package com.libookproject.libookapp.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.libookproject.libookapp.R;

public class MainActivity extends MasterActivity {
    private FrameLayout frameLayout;
    private LibraryFragment libraryFragment;
    private SearchFragment searchFragment;
    private CommunityFragment communityFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;
    private BottomNavigationView bottomNav;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private TextView tVNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
        addNevigationBarInnerFragmentListener();
        addNevigationBarPressedListener();

        setupNetworkTracking();
        if (!isInternetAvailable())
        {
            showNoInternetMessage();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();

        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    private void init(Bundle savedInstanceState)
    {
        tVNoInternet = findViewById(R.id.tVNoInternet);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNav = findViewById(R.id.bottomNavigation);

        //app start first time
        if (savedInstanceState == null)
        {
            libraryFragment = new LibraryFragment();
            searchFragment = new SearchFragment();
            communityFragment = new CommunityFragment();
            profileFragment = new ProfileFragment();

            //default configuration
            activeFragment = searchFragment;
            bottomNav.setSelectedItemId(R.id.nav_search);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameLayout, libraryFragment, "libraryFragment")
                    .hide(libraryFragment)
                    .add(R.id.frameLayout, communityFragment, "communityFragment")
                    .hide(communityFragment)
                    .add(R.id.frameLayout, profileFragment, "profileFragment")
                    .hide(profileFragment)
                    .add(R.id.frameLayout, searchFragment, "searchFragment")
                    .commit();
        }
        else
        {
            //recover existing fragments
            libraryFragment = (LibraryFragment)getSupportFragmentManager().findFragmentByTag("libraryFragment");
            searchFragment = (SearchFragment)getSupportFragmentManager().findFragmentByTag("searchFragment");
            communityFragment = (CommunityFragment)getSupportFragmentManager().findFragmentByTag("communityFragment");
            profileFragment = (ProfileFragment)getSupportFragmentManager().findFragmentByTag("profileFragment");

            //check which fragment was visible
            if (libraryFragment != null && !libraryFragment.isHidden())
            {
                activeFragment = libraryFragment;
            }
            else if (communityFragment != null && !communityFragment.isHidden())
            {
                activeFragment = communityFragment;
            }
            else if (profileFragment != null && !profileFragment.isHidden())
            {
                activeFragment = profileFragment;
            }
            else
            {
                activeFragment = searchFragment;
            }
        }
    }

    private void addNevigationBarInnerFragmentListener()
    {
        getSupportFragmentManager()
                .addOnBackStackChangedListener(() -> {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    {
                        bottomNav.setVisibility(View.GONE);
                    }
                    else
                    {
                        bottomNav.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void addNevigationBarPressedListener()
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
            else if (item.getItemId() == R.id.nav_community)
            {
                switchFragment(communityFragment);
                return true;
            }
            else if (item.getItemId() == R.id.nav_profile)
            {
                switchFragment(profileFragment);
                return true;
            }
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

    private boolean isInternetAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Network network = cm.getActiveNetwork();
        if (network == null)
        {
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);

        return (capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
    }

    private void setupNetworkTracking()
    {
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback()
        {

            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> hideNoInternetMessage());
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> showNoInternetMessage());
            }
        };
    }

    private void showNoInternetMessage() {

        tVNoInternet.setVisibility(View.VISIBLE);

        tVNoInternet.animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }

    private void hideNoInternetMessage() {

        tVNoInternet.animate()
                .translationY(-tVNoInternet.getHeight())
                .setDuration(300)
                .withEndAction(() -> tVNoInternet.setVisibility(View.GONE))
                .start();
    }

    public void notifyPostsHaveChanged()
    {
        //communityFragment.setNeedsRefresh(true);
        profileFragment.setNeedsRefresh(true);
    }
}