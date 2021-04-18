package com.pkan.official.mess.delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pkan.official.customer.order.OrderItem;
import com.pkan.official.login.LoginActivityVerifyOtp;

public class DeliveryMainActivity extends AppCompatActivity {

    // views used in activity
    TextView deliveryMainActivityMessNameTextView, deliveryMainSubHeaderTextView;

    ImageView deliveryMainHistoryImageView, deliveryMainActivityLogOutImageView;

    LinearLayout deliveryMainScrollViewLinearLayout;

    // firebase variables to be used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables to be used in functions
    String mess_id, mess_name;

    // variable for titles
    String lunch_or_dinner, order_date;

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

    }

    private void initViews () {

        // initialize views
        deliveryMainActivityMessNameTextView = findViewById(R.id.deliveryMainActivityMessNameTextView);
        deliveryMainSubHeaderTextView = findViewById(R.id.deliveryMainSubHeaderTextView);
        deliveryMainHistoryImageView = findViewById(R.id.deliveryMainHistoryImageView);
        deliveryMainActivityLogOutImageView = findViewById(R.id.deliveryMainActivityLogOutImageView);
        deliveryMainScrollViewLinearLayout = findViewById(R.id.deliveryMainScrollViewLinearLayout);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(DeliveryMainActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {
        deliveryMainHistoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send to delivery history page
                Intent intent = new Intent(getApplicationContext(), DeliveryHistoryActivity.class);
                startActivity(intent);
            }
        });

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

                            // get Orders
                            getOrders();
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
            deliveryMainSubHeaderTextView.setText(lunch_or_dinner + " for " + order_date);
        } else {

            // warn user to reload app
            Toast.makeText(getApplicationContext(), "Please Restart App", Toast.LENGTH_LONG);
            finish();
        }
    }

    private void getOrders () {
        Log.e("getOrders", "reached");

        databaseReference.child("Mess").child(mess_id).child("Current Orders")
                .child(lunch_or_dinner).child("Delivery")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot node : snapshot.getChildren()) {
                    // set area name
                    String area_name = node.child("Area Name").getValue(String.class);

                    if (area_name != null) {
                        // set area text view

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(10, 20, 10, 20);

                        TextView textView = new TextView(getApplicationContext());
                        textView.setLayoutParams(layoutParams);
                        textView.setText(area_name);
                        textView.setTextColor(getResources().getColor(R.color
                                .activity_delivery_main_area_name_text));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                        deliveryMainScrollViewLinearLayout.addView(textView);

                        // set orders
                        for (DataSnapshot orderNode : node.child("Orders").getChildren()) {
                            String order_id = "", name = "", house_number = "", room_number = "",
                                    landmark = "", phone_number = "";

                            order_id = orderNode.child("Order Id").getValue(String.class);
                            name = orderNode.child("Customer Name").getValue(String.class);
                            house_number = orderNode.child("House Number").getValue(String.class);
                            room_number = orderNode.child("Room Number").getValue(String.class);
                            landmark = orderNode.child("Landmark").getValue(String.class);
                            phone_number = orderNode.child("Customer Mobile Number")
                                    .getValue(String.class);

                            Log.e("after loop", "reached");
                            Log.e("Customer name", name);
                            Log.e("order id", order_id);
                            Log.e("house number", house_number);
                            Log.e("room number", room_number);
                            Log.e("Landmark", landmark);


                            if (order_id != null && name != null && house_number != null &&
                                    room_number != null && landmark != null &&
                                    phone_number != null) {


                                DeliveryOrder deliveryOrder = new DeliveryOrder(order_id, name,
                                        house_number, room_number, landmark, phone_number);

                                setOrders(deliveryOrder);
                                setOrders(deliveryOrder);
                                setOrders(deliveryOrder);
                                setOrders(deliveryOrder);

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOrders (DeliveryOrder deliveryOrder) {

        Log.e("setOrders", "reached");

        // create view object
        View view = getLayoutInflater().inflate(
                R.layout.delivery_main_scroll_view_item,
                deliveryMainScrollViewLinearLayout, false);

        // declare views used in the item view

        TextView deliveryMainScrollViewItemCustomerNameTextView,
                deliveryMainScrollViewItemHouseNumberTextView,
                deliveryMainScrollViewItemRoomNumberTextView,
                deliveryMainScrollViewItemLandmarkTextView,
                deliverMainScrollViewItemPhoneNumberTextView;

        Button deliveryMainScrollViewItemDeliverButton;

        // initialize the views
        deliveryMainScrollViewItemCustomerNameTextView = view.findViewById(R.id
                .deliveryMainScrollViewItemCustomerNameTextView);
        deliveryMainScrollViewItemHouseNumberTextView = view.findViewById(R.id
                .deliveryMainScrollViewItemHouseNumberTextView);
        deliveryMainScrollViewItemRoomNumberTextView = view.findViewById(R.id
                .deliveryMainScrollViewItemRoomNumberTextView);
        deliveryMainScrollViewItemLandmarkTextView = view.findViewById(R.id
                .deliveryMainScrollViewItemLandmarkTextView);
        deliverMainScrollViewItemPhoneNumberTextView = view.findViewById(R.id
                .deliverMainScrollViewItemPhoneNumberTextView);
        deliveryMainScrollViewItemDeliverButton = view.findViewById(R.id
                .deliveryMainScrollViewItemDeliverButton);

        // set the texts
        deliveryMainScrollViewItemCustomerNameTextView.setText(deliveryOrder.getCustomer_name());
        deliveryMainScrollViewItemHouseNumberTextView.setText(deliveryOrder.getHouse_number());
        deliveryMainScrollViewItemRoomNumberTextView.setText(deliveryOrder.getRoom_number());
        deliveryMainScrollViewItemLandmarkTextView.setText(deliveryOrder.getLandmark());
        deliverMainScrollViewItemPhoneNumberTextView.setText(deliveryOrder.getPhone_number());

        // set on clicks
        deliveryMainScrollViewItemDeliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDelivered(deliveryOrder.getOrder_id());
            }
        });

        // set the call action when phone number text view is clicked
        deliverMainScrollViewItemPhoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ deliveryOrder.getPhone_number()));
                startActivity(intent);
            }
        });

        deliveryMainScrollViewLinearLayout.addView(view);
    }

    private void orderDelivered (String order_id) {
        // for testing
        Log.d("deliver", "clicked");
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