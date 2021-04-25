package com.pkan.official.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.pkan.official.R;

public class LoginActivityPhoneNumber extends AppCompatActivity {

    EditText loginPhoneEditTextView;
    Button loginSendOtpButton;

    // radio buttons
    RadioButton loginPhoneNumberCustomerRadioButton, loginPhoneNumberMessRadioButton;

    // variable to specify login as customer or mess
    String login_as = "Customers";

    // back button image view
    ImageView loginPhoneNumberBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        initViews();

        // set status bar color
        setStatusBarColor();

        setOnClicks ();

    }

    // initialize the views used in the activity
    private void initViews () {
        loginPhoneEditTextView = findViewById(R.id.loginPhoneEditTextView);
        loginSendOtpButton = findViewById(R.id.loginSendOtpButton);
        loginPhoneNumberCustomerRadioButton = findViewById(R.id.loginPhoneNumberCustomerRadioButton);
        loginPhoneNumberMessRadioButton = findViewById(R.id.loginPhoneNumberMessRadioButton);
        loginPhoneNumberBackImageView = findViewById(R.id.loginPhoneNumberBackImageView);
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
                    R.color.activity_login_phone_number_background));
        }

    }

    // set OnClicks used in the activity
    private void setOnClicks () {
        loginSendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = loginPhoneEditTextView.getText().toString();

                if (phone_number.length() == 10 ) {

                    // adding std code for India, else firebase won't accept the number correctly
                    phone_number = "+91" + phone_number;

                    // Moving to the Verify Otp Activity

                    Intent intent = new Intent(getApplicationContext(), LoginActivityVerifyOtp.class);
                    intent.putExtra("phone_number", phone_number);
                    intent.putExtra("login_as", login_as);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Phone Number",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        loginPhoneNumberCustomerRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPhoneNumberCustomerRadioButton.setChecked(true);
                loginPhoneNumberMessRadioButton.setChecked(false);
                login_as = "Customers";
            }
        });

        loginPhoneNumberMessRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPhoneNumberMessRadioButton.setChecked(true);
                loginPhoneNumberCustomerRadioButton.setChecked(false);
                login_as = "Mess";
            }
        });

        loginPhoneNumberBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

