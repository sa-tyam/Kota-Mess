package com.pkan.official;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.login.LoginActivityPhoneNumber;
import com.pkan.official.mess.MessMainActivity;

public class LandingPageActivity extends AppCompatActivity {

    Button button;

    // shared preference variable to remember user
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "loginAs";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // set status bar color
        setStatusBarColor();

        button = findViewById(R.id.landingPageLoginButton);

        // move to login page when button is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivityPhoneNumber.class);
                startActivity(intent);
                finish();
            }
        });

        // initialize shared preferences
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // get if user was logged in as mess or customer
        String loginAs = sharedPreferences.getString(Name, "");

        // if user is verified, send it to its corresponding page
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            if (loginAs.equals("Customers")) {
                startActivity(new Intent(getApplicationContext(), CustomerMainActivity.class));
                finish();
            }

            if (loginAs.equals("Mess")) {
                startActivity(new Intent(getApplicationContext(), MessMainActivity.class));
                finish();
            }

        }

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
                    R.color.activity_landing_page_background));
        }

    }


}