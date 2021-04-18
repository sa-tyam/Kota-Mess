package com.pkan.official.customer.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.pkan.official.customer.meals.Meal;
import com.pkan.official.customer.order.CustomerOrderDetailsActivity;
import com.pkan.official.customer.order.MealItem;

import java.util.ArrayList;

public class CustomerHistoryDetailActivity extends AppCompatActivity {

    // views to be used in activity

    ImageView customerHistoryDetailBackImageView, customerHistoryDetailMealImageView,
            customerHistoryDetailMessImageView, customerHistoryDetailSpecialImageView;

    TextView customerHistoryDetailHeaderTextView, customerHistoryDetailSpecialTextView,
            customerHistoryDetailPriceTextView, customerHistoryDetailMessNameTextView,
            customerHistoryDetailMessAddressTextView;

    LinearLayout customerHistoryDetailItemsLinearLayout;

    RatingBar customerHistoryDetailRatingBar;

    EditText customerHistoryDetailReviewEditText;

    Button customerHistoryDetailSaveReviewButton;

    // firebase variables to be used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // other variables to be used in functions
    String meal_id = "", orderId = "", review_id = "", mess_id = "";
    float rating = 0;
    String review = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history_detail);

        // initialise views and variables
        initViews();

        // set status bar color
        setStatusBarColor();

        // set on clicks
        setOnClicks();

        // disable screen, show progress dialog and set header
        startProgressDialog();
        setHeader();
    }

    private void initViews () {

        // initialise views
        customerHistoryDetailBackImageView = findViewById(R.id
                .customerHistoryDetailBackImageView);
        customerHistoryDetailMealImageView = findViewById(R.id
                .customerHistoryDetailMealImageView);
        customerHistoryDetailMessImageView = findViewById(R.id
                .customerHistoryDetailMessImageView);
        customerHistoryDetailSpecialImageView = findViewById(R.id
                .customerHistoryDetailSpecialImageView);
        customerHistoryDetailHeaderTextView = findViewById(R.id
                .customerHistoryDetailHeaderTextView);
        customerHistoryDetailSpecialTextView = findViewById(R.id
                .customerHistoryDetailSpecialTextView);
        customerHistoryDetailPriceTextView = findViewById(R.id
                .customerHistoryDetailPriceTextView);
        customerHistoryDetailMessNameTextView = findViewById(R.id
                .customerHistoryDetailMessNameTextView);
        customerHistoryDetailMessAddressTextView = findViewById(R.id
                .customerHistoryDetailMessAddressTextView);
        customerHistoryDetailItemsLinearLayout = findViewById(R.id
                .customerHistoryDetailItemsLinearLayout);
        customerHistoryDetailRatingBar = findViewById(R.id
                .customerHistoryDetailRatingBar);
        customerHistoryDetailReviewEditText = findViewById(R.id
                .customerHistoryDetailReviewEditText);
        customerHistoryDetailSaveReviewButton = findViewById(R.id
                .customerHistoryDetailSaveReviewButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerHistoryDetailActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize meal id
        orderId = getIntent().getStringExtra("orderId");

        // for debugging purpose
        Log.d("order id history detail", orderId);
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
                    R.color.activity_customer_history_detail_dark_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        customerHistoryDetailBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setHeader () {

        // order id temporary
        orderId = "\"1\"";

        // get the required values from database
        databaseReference.child("Orders").child(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        meal_id = snapshot.child("Meal Id").getValue(String.class);
                        review_id = snapshot.child("Review Id").getValue(String.class);

                        String l_or_d = snapshot.child("Lunch or Dinner").getValue(String.class);
                        String order_date = snapshot.child("Order Date").getValue(String.class);

                        if (l_or_d != null && order_date != null) {

                            // for debugging purpose
                            Log.d("l_or_d", l_or_d);

                            customerHistoryDetailHeaderTextView.setText(l_or_d + " for " + order_date);
                        }

                        if (meal_id != null) {

                            // for debugging purpose in case of error
                            Log.d("meal id", meal_id);

                            getMealDetails();
                        } else {

                            // show user something went wrong
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();

                            // enable screen
                            stopProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose in case of error
                        Log.e("order details", error.getDetails());

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
                        String mealId, mess_name, meal_image_link, special_or_regular;
                        int meal_price = 0;
                        ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                        // get the required values
                        mealId = meal_id;
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);
                        meal_image_link = snapshot.child("Picture Download Link").getValue(String.class);
                        special_or_regular = snapshot.child("Special or Normal").getValue(String.class);

                        if ( snapshot.child("Price").getValue(Integer.class) != null) {
                            meal_price = snapshot.child("Price").getValue(Integer.class);
                        } else {

                            // show user something went wrong
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();

                            // enable screen
                            stopProgressDialog();
                        }

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
                                setReview();
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
            customerHistoryDetailSpecialImageView.setVisibility(View.GONE);
            customerHistoryDetailSpecialTextView.setVisibility(View.GONE);
        }

        // set meal price
        customerHistoryDetailPriceTextView.setText("\u20B9" + " " +
                String.valueOf(meal.getMeal_price()));

        // set meal items
        for (MealItem mealItem : meal.getMealItemArrayList()) {
            // create view object
            View view = getLayoutInflater().inflate(
                    R.layout.customer_meal_item_item,
                    customerHistoryDetailItemsLinearLayout, false);

            // declare and initialize textviews used
            TextView name, quantity;
            name = view.findViewById(R.id.customerMealItemItemNameTextView);
            quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

            // set texts
            name.setText(mealItem.getItem_name());
            quantity.setText(mealItem.getQuantity());

            // add this view to linear layout
            customerHistoryDetailItemsLinearLayout.addView(view);
        }

        // set mess name and address
        customerHistoryDetailMessNameTextView.setText(meal.getMess_name());

        // get mess address
        databaseReference.child("Mess").child(meal.getMess_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String address = snapshot.child("Profile").child("Address").getValue(String.class);
                        if  (address != null) {

                            // for debugging purpose in case of error
                            Log.d("address", address);

                            // set text view
                            customerHistoryDetailMessAddressTextView.setText(address);
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
    }

    //set review
    private void setReview () {

        DatabaseReference review_ref = databaseReference.child("Reviews");

        if (review_id != null) {

            review_ref.child(review_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child("Rating").getValue(Integer.class) != null) {
                        rating = snapshot.child("Rating").getValue(Integer.class);
                    }

                    // for debugging in case of error
                    Log.d("rating", String.valueOf(rating));

                    review = snapshot.child("Review").getValue(String.class);

                    customerHistoryDetailRatingBar.setRating(rating);
                    customerHistoryDetailRatingBar.setIsIndicator(true);

                    if (review != null) {

                        // for debugging in case of error
                        Log.d("review", review);

                        customerHistoryDetailReviewEditText.setText(review);
                        // disable save button
                        customerHistoryDetailSaveReviewButton.setVisibility(View.GONE);

                        // disable the edit text
                        customerHistoryDetailReviewEditText.setFocusable(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    // for debugging purpose only
                    Log.e("get review", error.getDetails());
                }
            });
        } else {
            // save the review when save review button is clicked
            customerHistoryDetailSaveReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // save review
                    saveReview();
                }
            });
        }
    }

    private void saveReview () {
        DatabaseReference review_ref = databaseReference.child("Reviews");

        rating = customerHistoryDetailRatingBar.getRating();
        review = customerHistoryDetailReviewEditText.getText().toString();

        if (rating > 0) {

            // disable screen and show progress dialog
            startProgressDialog();

            // get a new review id
            review_id = review_ref.push().getKey();

            // set the values
            review_ref.child(review_id).child("Mess Id").setValue(mess_id);
            review_ref.child(review_id).child("Meal Id").setValue(meal_id);
            review_ref.child(review_id).child("Customer Id").setValue(user.getUid());
            review_ref.child(review_id).child("Order Id").setValue(orderId);
            review_ref.child(review_id).child("Rating").setValue(rating);
            review_ref.child(review_id).child("Review").setValue(review);

            // update review in order details
            databaseReference.child("Orders").child(orderId).child("Review Id")
                    .setValue(review_id);

            // update review in meal
            databaseReference.child("Meals").child(meal_id).child("Reviews")
                    .child(review_id).child("Review Id").setValue(review_id);

            // update review in mess details
            databaseReference.child("Mess").child(mess_id).child("Reviews").child(review_id)
                    .child("Review Id").setValue(review_id)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            // alert user review saved
                            Toast.makeText(getApplicationContext(), "review saved",
                                    Toast.LENGTH_LONG).show();

                            // set the review text
                            customerHistoryDetailReviewEditText.setText(review);
                            // disable save button
                            customerHistoryDetailSaveReviewButton.setVisibility(View.GONE);

                            // disable the edit text
                            customerHistoryDetailReviewEditText.setFocusable(false);

                            // stop progress dialog
                            stopProgressDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // stop progress dialog
                    stopProgressDialog();

                    // warn user review not saved
                    Toast.makeText(getApplicationContext(), "Sorry, review not saved",
                            Toast.LENGTH_LONG).show();

                }
            });

        } else {

            // warn user to give ratings first
            Toast.makeText(getApplicationContext(), "give ratings please",
                    Toast.LENGTH_SHORT).show();
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