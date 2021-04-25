package com.pkan.official.customer.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.customer.plans.CustomerPlanSelectActivity;

import java.util.ArrayList;
import java.util.Random;

public class CustomerProfileEditActivity extends AppCompatActivity {

    // views used in the activity

    EditText customerProfileEditNameEditText, customerProfileEditHouseNumberEditText,
            customerProfileEditRoomNumberEditText, customerProfileEditLandmarkEditText;

    Spinner customerProfileEditGenderSpinner, customerProfileEditCitySpinner,
            customerProfileEditAreaSpinner;

    Button customerProfileEditSaveButton;

    ImageView customerProfileEditBackImageView;

    // Firebase variables to be used in functions

    FirebaseUser user;
    DatabaseReference databaseReference;

    // variables used to store user data
    String name, gender, city, area, house_number, room_number, landmark, city_id, area_id;

    // Array List for Spinners
    ArrayList<String> genderArrayList, cityArrayList, areaArrayList;

    // extra array lists required

    ArrayList<String> cityIdArrayList, areaIdArrayList;

    // Progress Dialog
    ProgressDialog progressDialog;

    // variable to see if user is new or existing
    String new_user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_edit);

        // initialize the views used in activity
        initViews ();

        // disable functions and show progress dialog till cities are loaded from database
        startProgressDialog();

        // check if new Customer or existing
        checkIfNew();

        // set the spinner items
        setSpinners();

        // set onClicks for various buttons present in activity
        setOnClicks ();
    }

    private void initViews() {

        // initialising views used in activity

        customerProfileEditNameEditText = findViewById(R.id.customerProfileEditNameEditText);
        customerProfileEditHouseNumberEditText = findViewById(R.id.customerProfileEditHouseNumberEditText);
        customerProfileEditRoomNumberEditText = findViewById(R.id.customerProfileEditRoomNumberEditText);
        customerProfileEditLandmarkEditText = findViewById(R.id.customerProfileEditLandmarkEditText);
        customerProfileEditGenderSpinner = findViewById(R.id.customerProfileEditGenderSpinner);
        customerProfileEditCitySpinner = findViewById(R.id.customerProfileEditCitySpinner);
        customerProfileEditAreaSpinner = findViewById(R.id.customerProfileEditAreaSpinner);
        customerProfileEditSaveButton = findViewById(R.id.customerProfileEditSaveButton);
        customerProfileEditBackImageView = findViewById(R.id.customerProfileEditBackImageView);

        // initializing firebase variables

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize array lists
        genderArrayList = new ArrayList<>();
        cityArrayList = new ArrayList<>();
        areaArrayList = new ArrayList<>();
        cityIdArrayList = new ArrayList<>();
        areaIdArrayList = new ArrayList<>();

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerProfileEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
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
                    R.color.activity_customer_profile_edit_background));
        }

    }

    private void checkIfNew () {
        databaseReference.child("Customers").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Balance")) {
                    new_user = "";
                } else {
                    new_user = "newUser";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSpinners () {
        // set the gender spinner
        setGenderSpinner ();

        // set the city spinner
        setCitySpinner();
    }

    private void setGenderSpinner () {
        // add the available options for gender
        genderArrayList.add("Female");
        genderArrayList.add("Male");
        genderArrayList.add("Do not Specify");

        // adapter for gender spinner using gender array list
        ArrayAdapter genderAdapter = new ArrayAdapter(getApplicationContext(), R.layout.profile_spinners_item, genderArrayList);

        customerProfileEditGenderSpinner.setAdapter(genderAdapter);

        // set the events to perform when item in the spinner is selected
        customerProfileEditGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // here 'i' is the position which was clicked
                gender = genderArrayList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // in case no item is selected, select the first item by default
                gender = genderArrayList.get(0);
            }
        });
    }

    private void setCitySpinner () {
        // clear the existing cities in the city array list
        cityArrayList.clear();
        cityIdArrayList.clear();

        // get the list of cities available from database
        databaseReference.child("City").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot keyNode : snapshot.getChildren()) {

                    // add the available cities and its id to the array lists
                    cityIdArrayList.add(keyNode.getKey());
                    cityArrayList.add(keyNode.child("Name").getValue(String.class));
                }

                // list is complete, now create adapter using it
                // layout used is present in the res/layout/ directory with name profile_spinner_item
                ArrayAdapter cityAdapter = new ArrayAdapter(getApplicationContext(),
                        R.layout.profile_spinners_item, cityArrayList);

                customerProfileEditCitySpinner.setAdapter(cityAdapter);

                // now we can stop the progress dialog as city spinner is ready
                stopProgressDialog();

                // set the events to perform when item in the spinner is selected
                customerProfileEditCitySpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int i, long l) {
                        // here 'i' is the position which was selected
                        city = cityArrayList.get(i);
                        city_id = cityIdArrayList.get(i);

                        // since the city is selected, we can now set the corresponding areas
                        // disable the screen and start progress dialog till areas are loaded
                        startProgressDialog();

                        // set the area spinner now
                        setAreaSpinner(city_id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // if nothing is selected, select first item by default
                        city = cityArrayList.get(0);
                        city_id = cityIdArrayList.get(0);

                        startProgressDialog();
                        setAreaSpinner(city_id);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging in case of errors
                Log.e("setCitySpinner", error.getDetails());
            }
        });
    }

    private void setAreaSpinner (String city_id) {

        // clear the existing values in the area array lists
        areaArrayList.clear();
        areaIdArrayList.clear();

        // fetch the areas available for the given city
        databaseReference.child("Areas").child(city_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot keyNode : snapshot.getChildren()) {

                    // add the areas and its ids to the array lists
                    areaIdArrayList.add(keyNode.getKey());
                    areaArrayList.add(keyNode.child("Name").getValue(String.class));
                }

                // now the list is ready, create an dapter using it
                ArrayAdapter areaAdapter = new ArrayAdapter(getApplicationContext(),
                        R.layout.profile_spinners_item, areaArrayList);

                customerProfileEditAreaSpinner.setAdapter(areaAdapter);

                // area spinner is ready, so now we can stop the progress dialog
                stopProgressDialog();

                // set the events to perform when item in the spinner is selected
                customerProfileEditAreaSpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int i, long l) {

                        // her 'i' is the position which was selected
                        area = areaArrayList.get(i);
                        area_id = areaIdArrayList.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // in case no item is selected, select the first item by default
                        area = areaArrayList.get(0);
                        area_id = areaIdArrayList.get(0);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging in case of errors
                Log.e("setAreaSpinner", error.getDetails());
            }
        });
    }

    private void setOnClicks() {

        // close this activity when back button image view is pressed
        customerProfileEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // save the data on the database when save button is pressed
        customerProfileEditSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSave();
            }
        });
    }

    private void checkForSave () {

        // getting values from the form
        name = customerProfileEditNameEditText.getText().toString();
        house_number = customerProfileEditHouseNumberEditText.getText().toString();
        room_number = customerProfileEditRoomNumberEditText.getText().toString();
        landmark = customerProfileEditLandmarkEditText.getText().toString();

        // check if all the fields contain data
        if (name.length() == 0 || gender.length() == 0 || city.length() == 0 || area.length() == 0
            || house_number.length() == 0 || room_number.length() == 0 || landmark.length() == 0) {

            // show user that form is incomplete
            Toast.makeText(getApplicationContext(), "Fill the details", Toast.LENGTH_LONG).show();

        } else {
            saveData();
        }

    }

    private void saveData () {

        // disable screen activity till data is saved
        startProgressDialog();

        // set the profile part of data to the database
        DatabaseReference profile_ref = databaseReference.child("Customers").child(user.getUid()).child("Profile");
        profile_ref.child("Name").setValue(name);
        profile_ref.child("Gender").setValue(gender);
        profile_ref.child("Phone Number").setValue(user.getPhoneNumber());

        // set the address part of data to database
        DatabaseReference address_ref = databaseReference.child("Customers").child(user.getUid()).child("Address");
        address_ref.child("City").setValue(city);
        address_ref.child("City Id").setValue(city_id);
        address_ref.child("Area").setValue(area);
        address_ref.child("Area Id").setValue(area_id);
        address_ref.child("House Number").setValue(house_number);
        address_ref.child("Room Number").setValue(room_number);

        if (new_user.length() > 0) {
            // create a referral code
            String referral_code = getReferralCode();
            profile_ref.child("Referral Code").setValue(referral_code);

            // set the initial balance of the user to 0
            databaseReference.child("Customers").child(user.getUid()).child("Balance")
                    .setValue(0);
        }

        address_ref.child("Landmark").setValue(landmark).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // enable the screen once data is saved
                stopProgressDialog();

                Intent intent;
                if (new_user.length() > 0) {
                    // the user is new, so send it to select plan activity
                    intent = new Intent(getApplicationContext(), CustomerPlanSelectActivity.class);
                    intent.putExtra("newUser", "new_user");
                } else {

                    // it is existing customer, so send it to home page
                    intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                }

                // start new activity and finish the current
                startActivity(intent);
                finish();
            }
        });

    }

    private String getReferralCode () {

        // generate a random integer between 0 to 100000
        Random random = new Random();
        int referral_code = (int) ((random.nextFloat())*10000);
        String uid = user.getUid();

        String temp = uid.substring(0, 5);

        // return the string after concatenating the random number
        return temp + String.valueOf(referral_code);
    }

    private void startProgressDialog () {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void stopProgressDialog () {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}

