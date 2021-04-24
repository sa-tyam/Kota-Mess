package com.pkan.official.mess.delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.LandingPageActivity;
import com.pkan.official.R;

import java.util.ArrayList;

import static com.pkan.official.mess.delivery.GetOrderList.deliveryOrderArrayList;

public class DeliveryMainActivity extends AppCompatActivity {

    // views used in activity
    TextView deliveryMainActivityMessNameTextView, deliveryMainHeaderTextView;

    SearchView deliveryMainSearchView;

    RecyclerView deliveryMainRecyclerView;

    ImageView deliveryMainActivityLogOutImageView;



    // firebase variables to be used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables to be used in functions
    String mess_id, mess_name;

    // variable for titles
    String lunch_or_dinner, order_date;

    // recycler view adapter
    DeliveryRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main);

        // initialize views and variables
        initViews();

        // set onClicks
        setOnClicks();

        // get Title Variables
        getTitleVariables();

        // set search view
        setSearchView();
    }

    private void initViews () {

        // initialize the views
        deliveryMainActivityMessNameTextView = findViewById(R.id
                .deliveryMainActivityMessNameTextView);
        deliveryMainHeaderTextView = findViewById(R.id.deliveryMainHeaderTextView);
        deliveryMainSearchView = findViewById(R.id.deliveryMainSearchView);
        deliveryMainRecyclerView = findViewById(R.id.deliveryMainRecyclerView);
        deliveryMainActivityLogOutImageView = findViewById(R.id
                .deliveryMainActivityLogOutImageView);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(DeliveryMainActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {
        deliveryMainActivityLogOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // sign out current user
                FirebaseAuth.getInstance().signOut();

                // send to landing page
                Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getTitleVariables () {
        // for debugging

        Log.e("getTitleVariables", "reached");

        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e("snapshot", "received");

                // fetch values of title variables
                lunch_or_dinner = snapshot.child("Current Lunch or Dinner").getValue(String.class);
                order_date = snapshot.child("Current Date").getValue(String.class);



                if (lunch_or_dinner != null && order_date != null) {
                    // for debugging
                    Log.e("order date", order_date);

                    // get mess Id
                    getMessId();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessId () {
        Log.e("getMessId", "reached");
        databaseReference.child("Delivery Persons").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // get mess name and id
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);

                        if (mess_id != null && mess_name != null ) {
                            Log.e("mess name", mess_name);
                            // set header and sub header
                            setTitles();
                            getData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setTitles () {
        Log.e("setTitles", "reached");

        if (mess_name != null) {
            deliveryMainActivityMessNameTextView.setText(mess_name);
        } else {

            // warn user to reload app
            Toast.makeText(getApplicationContext(), "Please Restart App", Toast.LENGTH_LONG);
            finish();
        }

        if (lunch_or_dinner != null && order_date != null) {
            deliveryMainHeaderTextView.setText(lunch_or_dinner + " for " + order_date);
        } else {

            // warn user to reload app
            Toast.makeText(getApplicationContext(), "Please Restart App", Toast.LENGTH_LONG);
            finish();
        }
    }

    private void getData () {
        GetOrderList.getDataFromDatabase(new GetOrderList.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<DeliveryOrder> deliveryOrderArrayList) {
                setRecyclerView();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        }, mess_id);
    }

    private void setRecyclerView () {
        deliveryMainRecyclerView.setHasFixedSize(true);
        deliveryMainRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false));
        adapter = new DeliveryRecyclerAdapter(getApplicationContext(), deliveryOrderArrayList);
        deliveryMainRecyclerView.setAdapter(adapter);
    }

    private void setSearchView () {
        deliveryMainSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String user_input = s.toLowerCase();
                ArrayList<DeliveryOrder> searchArrayList = new ArrayList<>();

                for (DeliveryOrder item : deliveryOrderArrayList) {
                    if (item.getArea_name().toLowerCase().contains(user_input)) {
                        searchArrayList.add(item);
                    }
                }

                adapter.searchData(searchArrayList);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String user_input = s.toLowerCase();
                ArrayList<DeliveryOrder> searchArrayList = new ArrayList<>();

                for (DeliveryOrder item : deliveryOrderArrayList) {
                    if (item.getArea_name().toLowerCase().contains(user_input)) {
                        searchArrayList.add(item);
                    }
                }

                adapter.searchData(searchArrayList);

                return true;
            }
        });
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