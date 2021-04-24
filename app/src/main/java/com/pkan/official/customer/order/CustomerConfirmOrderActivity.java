package com.pkan.official.customer.order;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.customer.meals.Meal;
import com.pkan.official.payments.PaymentsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class CustomerConfirmOrderActivity extends AppCompatActivity {

    // views used in activity
    ImageView customerConfirmOrderBackImageView, customerConfirmOrderMealImageView,
            customerConfirmOrderSpecialImageView;

    TextView customerConfirmOrderHeaderTextView, customerConfirmOrderSpecialTextView,
            customerConfirmOrderPriceTextView, customerConfirmOrderCustomerNameTextView,
            customerConfirmOrderCustomerHouseNumberTextView,
            customerConfirmOrderCustomerRoomNumberTextView,
            customerConfirmOrderCustomerAreaTextView, customerConfirmOrderCustomerLandmarkTextView;

    LinearLayout customerConfirmOrderItemsLinearLayout;

    RadioButton customerConfirmOrderHomeDeliveryRadioButton, customerConfirmOrderInMessRadioButton;

    CheckBox customerConfirmOrderUseWalletBalanceCheckBox;

    Button customerConfirmOrderButton;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress Dialog
    ProgressDialog progressDialog;

    // other variables to be used in functions
    String meal_id = "";

    // variables to be used in functions
    String upcoming_lunch_or_dinner = "", upcoming_date = "";

    String mess_id = "", order_id = "", customer_id = "";

    String mess_name = "", customer_name = "", order_time = "", order_status = "",
            customer_phone = "", mess_phone_number = "", mess_or_delivery = "",
            lunch_or_dinner = "", order_date = "", security_code = "", meal_image_link = "",
            address = "", special_or_regular = "";

    int order_price = -1;

    int customer_balance = -1;

    ArrayList<MealItem> mealItemArrayList;

    String customer_area = "", customer_area_id = "", customer_house_number = "",
            customer_room_number = "",
            customer_landmark = "";

    int mess_delivery_home = 1, mess_delivery_in_mess = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_confirm_order);

        // initialize views and variables used in activity
        initViews();

        // set status bar color
        setStatusBarColor();

        // set onClicks
        setOnClicks();

        // disable screen, show progress dialog and get service charge
        startProgressDialog();
        getUpComingLunchOrDinner();
    }

    private void initViews () {

        // initialize views used in activity
        customerConfirmOrderBackImageView = findViewById(R.id.customerConfirmOrderBackImageView);
        customerConfirmOrderMealImageView = findViewById(R.id.customerConfirmOrderMealImageView);
        customerConfirmOrderSpecialImageView = findViewById(R.id.customerConfirmOrderSpecialImageView);
        customerConfirmOrderHeaderTextView = findViewById(R.id.customerConfirmOrderHeaderTextView);
        customerConfirmOrderSpecialTextView = findViewById(R.id.customerConfirmOrderSpecialTextView);
        customerConfirmOrderCustomerNameTextView = findViewById(R.id.customerConfirmOrderCustomerNameTextView);
        customerConfirmOrderPriceTextView = findViewById(R.id.customerConfirmOrderPriceTextView);
        customerConfirmOrderCustomerHouseNumberTextView = findViewById(R.id.customerConfirmOrderCustomerHouseNumberTextView);
        customerConfirmOrderCustomerRoomNumberTextView = findViewById(R.id.customerConfirmOrderCustomerRoomNumberTextView);
        customerConfirmOrderCustomerAreaTextView = findViewById(R.id.customerConfirmOrderCustomerAreaTextView);
        customerConfirmOrderCustomerLandmarkTextView = findViewById(R.id.customerConfirmOrderCustomerLandmarkTextView);
        customerConfirmOrderItemsLinearLayout = findViewById(R.id.customerConfirmOrderItemsLinearLayout);
        customerConfirmOrderHomeDeliveryRadioButton = findViewById(R.id.customerConfirmOrderHomeDeliveryRadioButton);
        customerConfirmOrderInMessRadioButton = findViewById(R.id.customerConfirmOrderInMessRadioButton);
        customerConfirmOrderUseWalletBalanceCheckBox = findViewById(R.id.customerConfirmOrderUseWalletBalanceCheckBox);
        customerConfirmOrderButton = findViewById(R.id.customerConfirmOrderButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerConfirmOrderActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize meal id
        meal_id = getIntent().getStringExtra("mealId");

        if (meal_id != null) {
            // for debugging purpose
            Log.d("meal id", meal_id);
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
                    R.color.activity_customer_order_detail_dark_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        customerConfirmOrderBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // set home delivery as default checked and in mess unchecked
        customerConfirmOrderHomeDeliveryRadioButton.setChecked(true);
        customerConfirmOrderInMessRadioButton.setChecked(false);

        // toggle both buttons when any one is clicked
        customerConfirmOrderHomeDeliveryRadioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (customerConfirmOrderHomeDeliveryRadioButton.isChecked()) {
                    customerConfirmOrderInMessRadioButton.setChecked(false);
                } else {
                    customerConfirmOrderInMessRadioButton.setChecked(true);
                }

            }
        });

        customerConfirmOrderInMessRadioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (customerConfirmOrderInMessRadioButton.isChecked()) {
                    customerConfirmOrderHomeDeliveryRadioButton.setChecked(false);
                } else {
                    customerConfirmOrderHomeDeliveryRadioButton.setChecked(true);
                }
            }
        });
    }


    private void getUpComingLunchOrDinner () {
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String l_or_d = snapshot.child("Upcoming Lunch or Dinner").getValue(String.class);
                String up_date = snapshot.child("Upcoming Date").getValue(String.class);

                if (l_or_d != null && up_date != null) {
                    upcoming_lunch_or_dinner = l_or_d;
                    upcoming_date = up_date;

                    // get meal details
                    getMealDetails();

                } else {

                    // warn user something went wrong and finish activity
                    stopProgressDialog();
                    Toast.makeText(getApplicationContext(), "Something went wrong",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("get lunch or dinner", error.getDetails());

                // warn user something went wrong and finish activity
                stopProgressDialog();
                Toast.makeText(getApplicationContext(), "Something went wrong",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void getMealDetails () {

        // get the meal
        databaseReference.child("Meals").child(meal_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // variables to be used
                        String mealId, mess_id, mess_name, meal_image_link;
                        int meal_price;
                        ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                        // get the required values
                        mealId = meal_id;
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);
                        meal_image_link = snapshot.child("Picture Download Link")
                                .getValue(String.class);
                        special_or_regular = snapshot.child("Special or Normal")
                                .getValue(String.class);

                        meal_price = snapshot.child("Price").getValue(Integer.class);
                        for (DataSnapshot itemNode : snapshot.child("Items").getChildren()) {

                            // create a meal item
                            String item_name = itemNode.child("Name").getValue(String.class);
                            String amount = itemNode.child("Amount").getValue(String.class);

                            MealItem mealItem = new MealItem(item_name, amount);

                            // add this item to array list
                            mealItemArrayList.add(mealItem);
                        }

                        if (meal_id != null && mess_id != null && mess_name != null &&
                                mealItemArrayList.size() > 0) {

                            // get delivery options
                            getDeliveryOptions();

                            // for debugging in case of error
                            Log.d("mess name", mess_name);

                            // create a new meal
                            Meal meal = new Meal(meal_id, mess_id, mess_name, special_or_regular,
                                    meal_price, mealItemArrayList);

                            // set image link if available
                            if (meal_image_link != null) {
                                meal.setMeal_image_link(meal_image_link);
                            }

                            if (meal != null ) {

                                // get values
                                getValues(meal);

                            } else {
                                // stop progress dialog
                                stopProgressDialog();

                                // warn user something went wrong and finish activity
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong :(", Toast.LENGTH_LONG).show();

                                finish();
                            }

                        } else {
                            // warn user something went wrong and finish activity
                            stopProgressDialog();
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging in case of error
                        Log.e("get meal", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();

                        // warn user something went wrong and finish activity
                        Toast.makeText(getApplicationContext(), "Something went wrong :(",
                                Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
    }

    private void getDeliveryOptions () {
        databaseReference.child("Mess").child(mess_id).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("In Mess").getValue(Integer.class) != null) {
                    mess_delivery_in_mess = snapshot.child("In Mess").getValue(Integer.class);
                }
                if (snapshot.child("Home Delivery").getValue(Integer.class) != null) {
                    mess_delivery_home = snapshot.child("Home Delivery").getValue(Integer.class);
                }
                setDeliveryOptions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDeliveryOptions () {
        if (mess_delivery_home == 0) {
            customerConfirmOrderInMessRadioButton.setChecked(true);
            customerConfirmOrderInMessRadioButton.setClickable(false);
            customerConfirmOrderHomeDeliveryRadioButton.setChecked(false);
            customerConfirmOrderHomeDeliveryRadioButton.setClickable(false);
        }
        if (mess_delivery_in_mess == 0) {
            customerConfirmOrderInMessRadioButton.setChecked(false);
            customerConfirmOrderInMessRadioButton.setClickable(false);
            customerConfirmOrderHomeDeliveryRadioButton.setChecked(true);
            customerConfirmOrderHomeDeliveryRadioButton.setClickable(false);
        }
    }

    private void getValues (@NonNull Meal meal) {

        // get data easily available
        order_id = databaseReference.child("Orders").push().getKey();
        mess_id = meal.getMess_id();
        customer_id = user.getUid();
        mess_name = meal.getMess_name();
        customer_phone = user.getPhoneNumber();
        lunch_or_dinner = upcoming_lunch_or_dinner;
        order_date = upcoming_date;
        meal_image_link = meal.getMeal_image_link();
        order_status = "pending";
        order_price = meal.getMeal_price();
        mealItemArrayList = meal.getMealItemArrayList();
        meal_image_link = meal.getMeal_image_link();

        // get current time
        order_time = Calendar.getInstance().getTime().toString();

        // get the security code
        security_code = generateSecurityCode();
        DatabaseReference code_ref = databaseReference.child("Mess").child(mess_id)
                .child("Current Meals").child(upcoming_date).child(upcoming_lunch_or_dinner)
                .child("Security Codes");

        code_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (snapshot.hasChild(security_code)) {
                    security_code = generateSecurityCode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get mess details
        getMessDetails();

    }

    private void getMessDetails () {

        // get mess phone number
        databaseReference.child("Mess").child(mess_id).child("Profile").child("Phone Number")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String phone_number = snapshot.getValue(String.class);

                if (phone_number != null) {

                    // for debugging in case of error
                    Log.d("mess phone", phone_number);

                    mess_phone_number = phone_number;
                }

                // get customer details
                getCustomerDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("mess detail", error.getDetails());

                // warn user something went wrong and finish activity
                stopProgressDialog();
                Toast.makeText(getApplicationContext(), "Something went wrong",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void getCustomerDetails () {

        // get customer name and address
        databaseReference.child("Customers").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get customer balance
                if (snapshot.child("Balance").getValue(Integer.class) != null) {
                    customer_balance = snapshot.child("Balance").getValue(Integer.class);
                }

                customer_area_id = snapshot.child("Address").child("Area Id")
                        .getValue(String.class);
                customer_area = snapshot.child("Address").child("Area").getValue(String.class);
                customer_landmark = snapshot.child("Address").child("Landmark")
                        .getValue(String.class);
                customer_house_number = snapshot.child("Address").child("House Number")
                        .getValue(String.class);
                customer_room_number = snapshot.child("Address").child("Room Number")
                        .getValue(String.class);

                String name = snapshot.child("Profile").child("Name").getValue(String.class);

                String customer_address = customer_area
                        + " landmark : " +  customer_landmark
                        + " house : " + customer_house_number +
                        " room : " + customer_room_number;

                if (name != null && address != null) {

                    // for debugging in case of error
                    Log.d("address", address);

                    customer_name = name;
                    address = customer_address;

                    // set the data in views
                    setDataInViews();
                } else {

                    // warn user something went wrong and finish activity
                    stopProgressDialog();
                    Toast.makeText(getApplicationContext(), "Something went wrong",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("customer detail", error.getDetails());

                // warn user something went wrong and finish activity
                stopProgressDialog();
                Toast.makeText(getApplicationContext(), "Something went wrong",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void setDataInViews() {

        // set header
        customerConfirmOrderHeaderTextView.setText(lunch_or_dinner + " for " + upcoming_date);

        // set special or not
        if (!special_or_regular.toLowerCase().equals("special")) {
            customerConfirmOrderSpecialImageView.setVisibility(View.GONE);
            customerConfirmOrderSpecialTextView.setVisibility(View.GONE);
        }

        // set price
        customerConfirmOrderPriceTextView.setText("\u20B9" + " " +String.valueOf(order_price));

        // set meal items
        for (MealItem mealItem : mealItemArrayList) {
            // create view object
            View view = getLayoutInflater().inflate(
                    R.layout.customer_meal_item_item,
                    customerConfirmOrderItemsLinearLayout, false);

            // declare and initialize textViews used
            TextView name, quantity;
            name = view.findViewById(R.id.customerMealItemItemNameTextView);
            quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

            // set texts
            name.setText(mealItem.getItem_name());
            quantity.setText(mealItem.getQuantity());

            // add this view to linear layout
            customerConfirmOrderItemsLinearLayout.addView(view);
        }

        // set meal image
        Picasso.get()
                .load(meal_image_link)
                .into(customerConfirmOrderMealImageView);

        // set customer address
        customerConfirmOrderCustomerNameTextView.setText(customer_name);
        customerConfirmOrderCustomerRoomNumberTextView.setText(customer_room_number);
        customerConfirmOrderCustomerHouseNumberTextView.setText(customer_house_number);
        customerConfirmOrderCustomerLandmarkTextView.setText(customer_landmark);
        customerConfirmOrderCustomerAreaTextView.setText(customer_area);

        // now add onClick listener to the confirm order button
        customerConfirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBalance();
            }
        });

        // stop the progress dialog
        stopProgressDialog();
    }

    private void checkBalance () {
        if (customerConfirmOrderUseWalletBalanceCheckBox.isChecked()) {
            if (customer_balance >= order_price) {

                // start progress dialog
                startProgressDialog();

                deductBalance();
            } else {

                // make payment
                Intent intent = new Intent(getApplicationContext(), PaymentsActivity.class);

                // calculate the amount to be paid
                float amount = order_price - customer_balance;

                intent.putExtra("amount", amount);

                startActivity(intent);
            }
        } else {

            // make payment
            Intent intent = new Intent(getApplicationContext(), PaymentsActivity.class);
            intent.putExtra("amount", order_price);

            startActivity(intent);
        }
    }

    private void deductBalance () {
        // deduct balance from customer's account
        databaseReference.child("Customers").child(user.getUid()).child("Balance")
                .setValue(customer_balance - order_price)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // stop progress dialog
                stopProgressDialog();

                // alert user that order is placed
                Toast.makeText(getApplicationContext(), "Order Failed",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadData () {

        // get mess or delivery
        getMessOrDelivery();

        // get the order reference
        DatabaseReference order_ref = databaseReference.child("Orders").child(order_id);

        // set the values
        order_ref.child("Order Id").setValue(order_id);
        order_ref.child("Order Price").setValue(order_price);
        order_ref.child("Mess Id").setValue(mess_id);
        order_ref.child("Meal Id").setValue(meal_id);
        order_ref.child("Customer Id").child(customer_id);
        order_ref.child("Mess Name").child(mess_name);
        order_ref.child("Order Time").setValue(order_time);
        order_ref.child("Address").setValue(address);
        order_ref.child("Status").setValue(order_status);
        order_ref.child("Customer Phone Number").setValue(customer_phone);
        order_ref.child("Mess or Delivery").setValue(mess_or_delivery);
        order_ref.child("Mess Phone Number").setValue(mess_phone_number);
        order_ref.child("Lunch or Dinner").setValue(lunch_or_dinner);
        order_ref.child("Order Date").setValue(order_date);
        order_ref.child("Security Code").setValue(security_code);
        order_ref.child("Meal Image Link").setValue(meal_image_link);

        // set the meal items
        for (MealItem mealItem : mealItemArrayList) {
            String item_id =  order_ref.child("Items").push().getKey();
            order_ref.child("Items").child(item_id).child("Name")
                    .setValue(mealItem.getItem_name());
            order_ref.child("Items").child(item_id).child("Amount")
                    .setValue(mealItem.getQuantity());
        }

        // set the order in customer upcoming orders
        databaseReference.child("Customers").child(user.getUid()).child("Current Order")
                .child(upcoming_date).child(upcoming_lunch_or_dinner)
                .child(order_id).child("Order Id").setValue(order_id);
        databaseReference.child("Customers").child(user.getUid()).child("Current Order")
                .child(upcoming_date).child(upcoming_lunch_or_dinner)
                .child(order_id).child("Lunch or Dinner").setValue(lunch_or_dinner);

        // set the order in mess upcoming orders
        DatabaseReference mess_ref = databaseReference.child("Mess").child(mess_id)
                .child("Orders").child(upcoming_date).child(upcoming_lunch_or_dinner)
                .child(mess_or_delivery);

        DatabaseReference mess_order_ref = mess_ref.child("Orders").child(order_id);
        mess_order_ref.child("Order Id").setValue(order_id);
        mess_order_ref.child("Customer Name").setValue(customer_name);
        mess_order_ref.child("House Number").setValue(customer_house_number);
        mess_order_ref.child("Room Number").setValue(customer_room_number);
        mess_order_ref.child("Landmark").setValue(customer_landmark);
        mess_order_ref.child("Customer Mobile Number").setValue(customer_phone);
        mess_order_ref.child("Security Code").setValue(security_code)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // stop progress dialog
                        stopProgressDialog();

                        // alert user that order is placed
                        Toast.makeText(getApplicationContext(), "Order Placed",
                                Toast.LENGTH_LONG).show();

                        // move to home page
                        Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // stop progress dialog
                stopProgressDialog();

                // set balance to previous value
                databaseReference.child("Customers").child(user.getUid()).child("Balance")
                        .setValue(customer_balance);

                // alert user that order is placed
                Toast.makeText(getApplicationContext(), "Order Failed",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    private String generateSecurityCode () {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number;
        number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    private void getMessOrDelivery () {

        // either "Delivery" or "In Mess"
        if (customerConfirmOrderHomeDeliveryRadioButton.isChecked()) {

            // set it to home delivery
            mess_or_delivery = "Delivery";

        } else {

            // set it to in mess
            mess_or_delivery = "In Mess";
        }


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