package com.pkan.official.mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pkan.official.R;
import com.pkan.official.mess.history.MessHistoryFragment;
import com.pkan.official.mess.home.MessHomeFragment;
import com.pkan.official.mess.profile.MessProfileFragment;

public class MessMainActivity extends AppCompatActivity {

    // views used in activity
    BottomNavigationView messBottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_main);

        // initialize the views and variables
        initViews();

        // set status bar color
        setStatusBarColor();

        // set home fragment as the default selected fragment
        messBottomNavigationBar.setSelectedItemId(R.id.bottom_navigation_bar_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.messMainFrameLayout,
                new MessHomeFragment()).commit();

        // set the bottom navigation bar
        setBottomBar();
    }

    private void initViews () {

        // initialize the views used in activity
        messBottomNavigationBar = findViewById(R.id.messBottomNavigationBar);
    }

    private void setStatusBarColor () {

        // check if android version is greater than or equal to 21
        // it works only for API level 21 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.activity_mess_main_background));
        }

    }

    private void setBottomBar () {

        // set on item selected listener to bottom navigation bar
        messBottomNavigationBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        // initialize the selected fragment on the basis of item selected
                        switch (item.getItemId()) {
                            case R.id.bottom_navigation_bar_history:
                                selectedFragment = new MessHistoryFragment();
                                break;

                            case R.id.bottom_navigation_bar_home:
                                selectedFragment = new MessHomeFragment();
                                break;

                            case R.id.bottom_navigation_bar_profile:
                                selectedFragment = new MessProfileFragment();
                                break;

                        }

                        // render the selected fragment in the frame layout
                        getSupportFragmentManager().beginTransaction().replace(R.id.messMainFrameLayout,
                                selectedFragment).commit();

                        return true;
                    }
                });
    }
}