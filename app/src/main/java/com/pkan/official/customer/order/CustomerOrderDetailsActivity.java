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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.meals.GetMealList;
import com.pkan.official.customer.meals.Meal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerOrderDetailsActivity extends AppCompatActivity {


    // views used in activity
    ImageView customerOrderDetailBackImageView, customerOrderDetailMealImageView,
            customerOrderDetailSpecialImageView, customerOrderDetailMessImageView;

    TextView customerOrderDetailHeaderTextView, customerOrderDetailSpecialTextView,
            customerOrderDetailPriceTextView, customerOrderDetailMessNameTextView,
            customerOrderDetailMessAddressTextView;

    Button customerOrderDetailPlaceOrderButton;

    LinearLayout customerOrderDetailItemsLinearLayout, customerOrderDetailReviewLinearLayout;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress Dialog
    ProgressDialog progressDialog;

    // other variables to be used in functions
    String meal_id = "";

    // array list of meal Review Ids
    ArrayList<String> mealReviewArrayList;

    // service charge
    int service_charge = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);

        // initialize views and variables
        initViews();

        // set status bar color to dark background
        setStatusBarColor();

        // set onClick on buttons
        setOnClicks();

        // disable screen, show progress dialog and set header
        startProgressDialog();
        getServiceCharge();

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

    private void initViews () {

        // initialize views used in activity
        customerOrderDetailBackImageView = findViewById(R.id
                .customerOrderDetailBackImageView);
        customerOrderDetailMealImageView = findViewById(R.id
                .customerOrderDetailMealImageView);
        customerOrderDetailSpecialImageView = findViewById(R.id
                .customerOrderDetailSpecialImageView);
        customerOrderDetailMessImageView = findViewById(R.id
                .customerOrderDetailMessImageView);
        customerOrderDetailHeaderTextView = findViewById(R.id
                .customerOrderDetailHeaderTextView);
        customerOrderDetailSpecialTextView = findViewById(R.id
                .customerOrderDetailSpecialTextView);
        customerOrderDetailPriceTextView = findViewById(R.id
                .customerOrderDetailPriceTextView);
        customerOrderDetailMessNameTextView = findViewById(R.id
                .customerOrderDetailMessNameTextView);
        customerOrderDetailMessAddressTextView = findViewById(R.id
                .customerOrderDetailMessAddressTextView);
        customerOrderDetailPlaceOrderButton = findViewById(R.id
                .customerOrderDetailPlaceOrderButton);
        customerOrderDetailItemsLinearLayout = findViewById(R.id
                .customerOrderDetailItemsLinearLayout);
        customerOrderDetailReviewLinearLayout = findViewById(R.id
                .customerOrderDetailReviewLinearLayout);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerOrderDetailsActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize meal id
        meal_id = getIntent().getStringExtra("mealId");

        // for debugging purpose
        Log.d("meal id", meal_id);

        // initialize meal review array list
        mealReviewArrayList = new ArrayList<>();
    }

    private void setOnClicks () {

        // set on click on back image view
        customerOrderDetailBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // simply finish the activity
                finish();
            }
        });

        // set on click on button
        customerOrderDetailPlaceOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // move to confirm order activity
                Intent intent = new Intent(getApplicationContext(),
                        CustomerConfirmOrderActivity.class);
                intent.putExtra("mealId", meal_id);
                startActivity(intent);
            }
        });
    }

    public  void getServiceCharge () {
        databaseReference.child("Service Charge").child("App Charge")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue(Integer.class) != null) {
                            service_charge = snapshot.getValue(Integer.class);
                        }

                        // for debugging purpose
                        Log.d("service charge", String.valueOf(service_charge));

                        setHeader();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // for debugging purpose
                        Log.d("service charge", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void setHeader () {

        // get the required values from database
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String lunch_or_dinner = snapshot.child("Upcoming Lunch or Dinner")
                        .getValue(String.class);
                String upcoming_date = snapshot.child("Upcoming Date").getValue(String.class);

                if (lunch_or_dinner != null && upcoming_date != null) {

                    // for debugging purpose in case of error
                    Log.d("lunch or dinner", lunch_or_dinner);

                    // set the header text
                    customerOrderDetailHeaderTextView.setText(lunch_or_dinner +
                            " for " + upcoming_date);

                    // get meal details
                    getMealDetails();

                } else {

                    // stop progress dialog
                    stopProgressDialog();

                    // warn user something went wrong and finish activity
                    Toast.makeText(getApplicationContext(), "Something went wrong :(",
                            Toast.LENGTH_LONG).show();

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose in case of error
                Log.e("lunch or dinner", error.getDetails());

                // stop progress dialog
                stopProgressDialog();

                // warn user something went wrong and finish activity
                Toast.makeText(getApplicationContext(), "Something went wrong :(",
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
                        String mealId, mess_id, mess_name, meal_image_link, special_or_regular;
                        int meal_price = 50;
                        ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                        // get the required values
                        mealId = meal_id;
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);
                        meal_image_link = snapshot.child("Picture Download Link").getValue(String.class);
                        special_or_regular = snapshot.child("Special or Normal").getValue(String.class);

                        if (snapshot.child("Price").getValue(Integer.class) != null) {
                            meal_price = snapshot.child("Price").getValue(Integer.class);
                        }
                        Log.d("meal price", String.valueOf(meal_price));

                        meal_price += service_charge;

                        for (DataSnapshot itemNode : snapshot.child("Items").getChildren()) {

                            // create a meal item
                            String item_name = itemNode.child("Name").getValue(String.class);
                            String amount = itemNode.child("Amount").getValue(String.class);

                            MealItem mealItem = new MealItem(item_name, amount);

                            // add this item to array list
                            mealItemArrayList.add(mealItem);
                        }

                        // get the review ids of the meal
                        for (DataSnapshot reviewNode : snapshot.child("Reviews").getChildren()) {

                            // get review id
                            String reviewId = reviewNode.child("Review Id").getValue(String.class);

                            if (reviewId != null) {

                                // for debugging purpose in case of error
                                Log.d("review id", reviewId);

                                // add this id to review id array list
                                mealReviewArrayList.add(reviewId);
                            }
                        }

                        if (meal_price > 0 && meal_id != null && mess_id != null && mess_name != null &&
                                mealItemArrayList.size() > 0) {

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
                                setMealDetails(meal);
                            } else {
                                // stop progress dialog
                                stopProgressDialog();

                                // warn user something went wrong and finish activity
                                Toast.makeText(getApplicationContext(), "Something went wrong :(",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            }

                        } else {

                            // show user something went wrong
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();

                            // enable screen
                            stopProgressDialog();

                            // finish
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

    // set meal details
    private void setMealDetails (Meal meal) {

        // set the texts
        if (!meal.getSpecial_or_regular().toLowerCase().equals("special")) {
            customerOrderDetailSpecialImageView.setVisibility(View.GONE);
            customerOrderDetailSpecialTextView.setVisibility(View.GONE);
        }

        // set meal price
        customerOrderDetailPriceTextView.setText("\u20B9" + " " +
                String.valueOf(meal.getMeal_price()));

        // set meal items
        for (MealItem mealItem : meal.getMealItemArrayList()) {
            // create view object
            View view = getLayoutInflater().inflate(
                    R.layout.customer_meal_item_item,
                    customerOrderDetailItemsLinearLayout, false);

            // declare and initialize textViews used
            TextView name, quantity;
            name = view.findViewById(R.id.customerMealItemItemNameTextView);
            quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

            // set texts
            name.setText(mealItem.getItem_name());
            quantity.setText(mealItem.getQuantity());

            // add this view to linear layout
            customerOrderDetailItemsLinearLayout.addView(view);
        }

        // set meal image
        Picasso.get()
                .load(meal.getMeal_image_link())
                .into(customerOrderDetailMealImageView);

        // set mess name and address
        customerOrderDetailMessNameTextView.setText(meal.getMess_name());

        // get mess address and image
        databaseReference.child("Mess").child(meal.getMess_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String address = snapshot.child("Profile").child("Address").getValue(String.class);
                if  (address != null) {

                    // for debugging purpose in case of error
                    Log.d("address", address);

                    // set text view
                    customerOrderDetailMessAddressTextView.setText(address);
                }

                // get mess image link and set it in image view
                String mess_image_link = snapshot.child("Profile").child("Mess Image")
                        .getValue(String.class);

                if (mess_image_link != null) {
                    Picasso.get()
                            .load(mess_image_link)
                            .into(customerOrderDetailMessImageView);
                }

                // stop the progress dialog and enable the screen
                stopProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("address", error.getDetails());
            }
        });

        getReviews();
    }

    private void getReviews () {

        // for debugging purpose in case of error
        Log.d("review count", String.valueOf(mealReviewArrayList.size()));

        // reviews reference
        DatabaseReference review_ref = databaseReference.child("Reviews");

        // set all the reviews in review array list
        for (String reviewId : mealReviewArrayList) {
            review_ref.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String review = snapshot.child("Review").getValue(String.class);
                    float rating = 0;

                    if (snapshot.child("Rating").getValue(Float.class) != null) {
                        rating = snapshot.child("Rating").getValue(Float.class);
                    }

                    // for debugging purpose
                    Log.d("rating", String.valueOf(rating));

                    // set the review
                    setReviews(review, rating);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setReviews (String review, float rating) {

        // check if rating is greater than 0
        if (rating > 0) {

            // create a new review view object
            View view = getLayoutInflater().inflate(
                    R.layout.mess_review_item,
                    customerOrderDetailReviewLinearLayout, false);

            // declare and initialize the views used
            TextView messReviewItemReviewTextView = view.findViewById(R.id
                    .messReviewItemReviewTextView);
            RatingBar messReviewItemRatingBar = view.findViewById(R.id.messReviewItemRatingBar);

            messReviewItemRatingBar.setRating(rating);
            messReviewItemRatingBar.setIsIndicator(true);

            if (review != null) {
                messReviewItemReviewTextView.setText(review);
            }

            // add this view to linear layout
            customerOrderDetailReviewLinearLayout.addView(view);
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