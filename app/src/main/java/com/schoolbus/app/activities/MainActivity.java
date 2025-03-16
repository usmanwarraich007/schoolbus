package com.schoolbus.app.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.schoolbus.app.R;
import com.schoolbus.app.fragments.HomeFragment;
import com.schoolbus.app.fragments.NotificationsFragment;
import com.schoolbus.app.fragments.ProfileFragment;
import com.schoolbus.app.fragments.RoutesFragment;
import com.schoolbus.app.fragments.TrackingFragment;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        initViews();
        
        // Set up toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        
        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            setTitle(R.string.home);
        }
    }

    private void initViews() {
        // Toolbar might not be in the layout
        try {
            toolbar = findViewById(R.id.toolbar);
        } catch (Exception e) {
            // Toolbar not found, continue without it
        }
        
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int titleResId = 0;
        
        int itemId = item.getItemId();
        if (itemId == R.id.homeFragment) {
            fragment = new HomeFragment();
            titleResId = R.string.home;
        } else if (itemId == R.id.trackingFragment) {
            fragment = new TrackingFragment();
            titleResId = R.string.tracking;
        } else if (itemId == R.id.routesFragment) {
            fragment = new RoutesFragment();
            titleResId = R.string.routes;
        } else if (itemId == R.id.notificationsFragment) {
            fragment = new NotificationsFragment();
            titleResId = R.string.notifications;
        } else if (itemId == R.id.profileFragment) {
            fragment = new ProfileFragment();
            titleResId = R.string.profile;
        }
        
        if (fragment != null) {
            loadFragment(fragment);
            setTitle(titleResId);
            return true;
        }
        
        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
} 