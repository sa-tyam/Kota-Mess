package com.pkan.official.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pkan.official.R;
import com.pkan.official.customer.history.CustomerHistoryFragment;
import com.pkan.official.customer.home.CustomerHomeFragment;
import com.pkan.official.customer.profile.CustomerProfileFragment;

public class CustomerMainActivity extends AppCompatActivity {

    // views used in activity
    BottomNavigationView customerBottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // initialize the views and variables
        initViews ();

        // set home as default selected fragment
        customerBottomNavigationBar.setSelectedItemId(R.id.bottom_navigation_bar_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.customerMainFrameLayout,
                new CustomerHomeFragment()).commit();

        // set bottom navigation bar and fragments
        setNavigationBar();
    }

    private void initViews () {

        // initialize the views
        customerBottomNavigationBar = findViewById(R.id.customerBottomNavigationBar);
    }

    private void setNavigationBar () {

        // set on item selected listener to bottom navigation bar
        customerBottomNavigationBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // initialize the selected fragment on the basis of item selected
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_bar_history:
                        selectedFragment = new CustomerHistoryFragment();
                        break;

                    case R.id.bottom_navigation_bar_home:
                        selectedFragment = new CustomerHomeFragment();
                        break;

                    case R.id.bottom_navigation_bar_profile:
                        selectedFragment = new CustomerProfileFragment();
                        break;

                }

                // render the selected fragment in the frame layout
                getSupportFragmentManager().beginTransaction().replace(R.id.customerMainFrameLayout,
                        selectedFragment).commit();

                return true;
            }
        });
    }
}