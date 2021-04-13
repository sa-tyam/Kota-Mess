package com.pkan.official;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.login.LoginActivityPhoneNumber;

public class LandingPageActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        button = findViewById(R.id.landingPageLoginButton);

        // for development purpose, to be removed
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), CustomerMainActivity.class));
            finish();
        }

        // move to login page when button is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivityPhoneNumber.class);
                startActivity(intent);
                finish();
            }
        });

    }


}