package com.pkan.official;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.login.LoginActivityPhoneNumber;
import com.pkan.official.mess.MessMainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LandingPageActivity extends AppCompatActivity {

    Button button;

    // shared preference variable to remember user
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "loginAs";

    SharedPreferences sharedPreferences;

    // firebase variables
    DatabaseReference databaseReference;

    // time management variables
    String current_l_or_d, current_date, upcoming_l_or_d, upcoming_date, next_l_or_d, next_date;

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

        // get time management status
        getTimeManagementStats();

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

    private void getTimeManagementStats () {

        // initialize firebase variables
        databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                current_l_or_d = snapshot.child("Current Lunch or Dinner").getValue(String.class);
                current_date = snapshot.child("Current Date").getValue(String.class);
                upcoming_l_or_d = snapshot.child("Upcoming Lunch or Dinner").getValue(String.class);
                upcoming_date = snapshot.child("Upcoming Date").getValue(String.class);
                next_l_or_d = snapshot.child("Mess Next Lunch or Dinner").getValue(String.class);
                next_date = snapshot.child("Mess Next Date").getValue(String.class);

                if (current_date != null && current_l_or_d != null && upcoming_date != null
                        && upcoming_l_or_d != null && next_date != null && next_l_or_d != null) {

                    // for debugging purpose
                    Log.d("upcoming date", upcoming_date);

                    // update dates
                    updateDates();

                } else {
                    Log.e("some date", "null");

                    // skip to home
                    skipToHome();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("time stats", error.getDetails());

                // skip to home
                skipToHome();
            }
        });
    }

    private void updateDates () {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String system_date = df.format(Calendar.getInstance().getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        String next_system_date = df.format(calendar.getTime());

        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String system_time = tf.format(Calendar.getInstance().getTime());

        try {
            Date current_system_time = tf.parse(system_time);

            if (current_system_time.after(tf.parse("14:00")) &&
                    current_system_time.before(tf.parse("22:00"))) {
                {

                    if (next_l_or_d.equals("Lunch")) {

                        current_l_or_d = upcoming_l_or_d;
                        current_date = upcoming_date;
                        upcoming_l_or_d = next_l_or_d;
                        upcoming_date = next_date;
                        next_l_or_d = "Dinner";
                        next_date = next_date;

                        uploadDates();
                    } else {
                        skipToHome();
                    }


                }
            } else {

                if (next_l_or_d.equals("Dinner")) {

                    current_l_or_d = upcoming_l_or_d;
                    current_date = upcoming_date;
                    upcoming_l_or_d = next_l_or_d;
                    upcoming_date = next_date;
                    next_l_or_d = "Lunch";
                    next_date = next_system_date;

                    uploadDates();
                } else {
                    skipToHome();
                }

            }



        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void uploadDates () {
        DatabaseReference time_ref = databaseReference.child("Time Management Status");

        time_ref.child("Current Lunch or Dinner").setValue(current_l_or_d);
        time_ref.child("Current Date").setValue(current_date);
        time_ref.child("Upcoming Lunch or Dinner").setValue(upcoming_l_or_d);
        time_ref.child("Upcoming Date").setValue(upcoming_date);
        time_ref.child("Mess Next Lunch or Dinner").setValue(next_l_or_d);
        time_ref.child("Mess Next Date").setValue(next_date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // skip to home
                skipToHome();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // skip to home
                skipToHome();

            }
        });

    }

    private void skipToHome () {
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

}