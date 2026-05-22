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

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.libookproject.libookapp.R;

/**
 * the main activity of the app, displayed after successful authentication.
 * hosts four main fragments (Library, Search, Community, Profile) inside a FrameLayout,
 * and manages navigation between them using a BottomNavigationView.
 * also monitors internet connectivity and displays a message when the connection is lost.
 * extends MasterActivity to inherit the light/dark mode toggle.
 */
public class MainActivity extends MasterActivity
{
    private static final String KEY_ACTIVE_NAV_ID = "activeNavId";
    private static final String KEY_NO_INTERNET_SHOWN = "noInternetShown";

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

    private boolean noInternetVisible = false;

    /**
     * initializes the activity, sets up fragments, navigation, and network tracking.
     * restores saved state if the activity is being recreated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);

        bottomNav.setVisibility(
                getSupportFragmentManager().getBackStackEntryCount() > 0
                        ? View.GONE
                        : View.VISIBLE);

        addNavigationBarInnerFragmentListener();
        addNavigationBarPressedListener();
        setupNetworkTracking();

        if (savedInstanceState != null)
        {
            noInternetVisible = savedInstanceState.getBoolean(KEY_NO_INTERNET_SHOWN, false);
            if (noInternetVisible)
            {
                tVNoInternet.setVisibility(View.VISIBLE);
                tVNoInternet.setTranslationY(0);
            }
        }
        else
        {
            if (!isInternetAvailable())
            {
                showNoInternetMessage();
            }
        }
    }

    /**
     * saves the currently activity state before it is destroyed,
     * so they can be restored on recreation.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_ACTIVE_NAV_ID, bottomNav.getSelectedItemId());
        outState.putBoolean(KEY_NO_INTERNET_SHOWN, noInternetVisible);
    }

    /**
     * registers the network callback when the activity becomes visible,
     * so the app starts tracking connectivity changes.
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    // unregisters the network callback when the activity is no longer visible.
    @Override
    protected void onStop()
    {
        super.onStop();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    /**
     * initializes all UI references and sets up the four main fragments.
     * if creating, adds all fragments to the fragment manager and shows Search by default.
     * if restoring from saved state, retrieves existing fragment instances and restores
     * the previously active tab.
     *
     * @param savedInstanceState The bundle containing previously saved state, or null if none.
     */
    private void init(Bundle savedInstanceState)
    {
        tVNoInternet = findViewById(R.id.tVNoInternet);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNav = findViewById(R.id.bottomNavigation);

        if (savedInstanceState == null)
        {
            libraryFragment = new LibraryFragment();
            searchFragment = new SearchFragment();
            communityFragment = new CommunityFragment();
            profileFragment = new ProfileFragment();

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
            libraryFragment = (LibraryFragment)getSupportFragmentManager().findFragmentByTag("libraryFragment");
            searchFragment = (SearchFragment)getSupportFragmentManager().findFragmentByTag("searchFragment");
            communityFragment = (CommunityFragment)getSupportFragmentManager().findFragmentByTag("communityFragment");
            profileFragment = (ProfileFragment)getSupportFragmentManager().findFragmentByTag("profileFragment");

            int savedNavId = savedInstanceState.getInt(KEY_ACTIVE_NAV_ID, R.id.nav_search);

            if (savedNavId == R.id.nav_library)
            {
                activeFragment = libraryFragment;
            }
            else if (savedNavId == R.id.nav_community)
            {
                activeFragment = communityFragment;
            }
            else if (savedNavId == R.id.nav_profile)
            {
                activeFragment = profileFragment;
            }
            else
            {
                activeFragment = searchFragment;
            }

            //setting the selected tab without on item listener
            bottomNav.setOnItemSelectedListener(null);
            bottomNav.setSelectedItemId(savedNavId);
        }
    }

    /**
     * registers a back stack listener that hides the bottom navigation bar when an inner
     * fragment is pushed onto the back stack, and shows it again when the user navigates back.
     */
    private void addNavigationBarInnerFragmentListener()
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

    /**
     * sets up the bottom navigation bar's item selection listener.
     * when a tab is selected, call for switching to the chosen fragment.
     */
    private void addNavigationBarPressedListener()
    {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_library)
            {
                switchFragment(libraryFragment);
                return true;
            }
            else if (id == R.id.nav_search)
            {
                switchFragment(searchFragment);
                return true;
            }
            else if (id == R.id.nav_community)
            {
                switchFragment(communityFragment);
                return true;
            }
            else if (id == R.id.nav_profile)
            {
                switchFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    // switches the visible fragment to the selected one
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


    // checks if the device currently has an active internet connection.
    private boolean isInternetAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (network == null)
        {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }


    // initializes the ConnectivityManager and sets up the NetworkCallback.
    private void setupNetworkTracking()
    {
        connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
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

    // displays the no internet message
    private void showNoInternetMessage()
    {
        noInternetVisible = true;
        tVNoInternet.setVisibility(View.VISIBLE);
        tVNoInternet.animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }

    // hides the no internet message
    private void hideNoInternetMessage()
    {
        noInternetVisible = false;
        tVNoInternet.animate()
                .translationY(-tVNoInternet.getHeight())
                .setDuration(300)
                .withEndAction(() -> tVNoInternet.setVisibility(View.GONE))
                .start();
    }

    /**
     * notifies the ProfileFragment that posts have changed and it should refresh its data
     * on its next appearance. called by other fragments after creating or deleting a post.
     */
    public void notifyPostsHaveChanged()
    {
        profileFragment.setNeedsRefresh(true);
    }
}