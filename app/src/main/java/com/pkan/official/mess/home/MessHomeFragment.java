package com.pkan.official.mess.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.pkan.official.customer.order.MealItem;
import com.pkan.official.mess.meals.MessMeal;
import com.pkan.official.mess.meals.MessSelectMealActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessHomeFragment extends Fragment {

    // default generated code

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessHomeFragment newInstance(String param1, String param2) {
        MessHomeFragment fragment = new MessHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // view to be used in fragment
    View view;

    // views to be used in fragment's view
    ImageView messHomeNextMealSpecialImageView;

    TextView messHomeHeaderTextView, messHomeCurrentMealStatsTotalInMessTextView,
            messHomeCurrentMealStatsTotalHomeDeliveryTextView, messHomeUpcomingMealHeaderTextView,
            messHomeUpcomingMealStatsTotalOrdersReceivedTextView,
            messHomeUpcomingMealStatsTotalInMessTextView, messHomeNextMealHeaderTextView,
            messHomeUpcomingMealStatsTotalHomeDeliveryTextView, messHomeNextMealNotSetTextView,
            messHomeNextMealSpecialTextView, messHomeUpcomingMealItemsHeader,
            messHomeNextMealItemsHeader, messHomeNextMealPriceTextView;

    EditText messHomeVerificationCodeEditText;

    Button messHomeAttendButton, messHomeNextMealSetMealButton;

    LinearLayout messHomeCurrentMealItemsLinearLayout, messHomeNextMealItemsLinearLayout;

    Switch messHomeReceiveOrdersSwitch;


    // firebase variables used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables used for current meal
    String current_meal_l_or_d, current_meal_date;
    long current_total_in_mess = -1, current_total_home_delivery = -1;

    // variables for upcoming meal
    String upcoming_meal_l_or_d, upcoming_meal_date, upcoming_meal_id;
    long upcoming_meal_total_order = -1, upcoming_meal_total_in_mess = -1,
            upcoming_meal_total_home_delivery = -1;
    int upcoming_meal_switch = 1;

    // variable for next meal
    String next_meal_l_or_d, next_meal_date, next_meal_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_mess_home, container, false);

        // initialize views and variables
        initViews();

        // set on clicks
        setOnClicks();

        // start progress dialog
        startProgressDialog();

        // get header details
        getHeaderDetails();

        return view;
    }

    private void initViews () {

        // initialize views
        messHomeNextMealSpecialImageView = view.findViewById(R.id
                .messHomeNextMealSpecialImageView);
        messHomeHeaderTextView = view.findViewById(R.id.messHomeHeaderTextView);
        messHomeCurrentMealStatsTotalInMessTextView = view.findViewById(R
                .id.messHomeCurrentMealStatsTotalInMessTextView);
        messHomeCurrentMealStatsTotalHomeDeliveryTextView = view.findViewById(R
                .id.messHomeCurrentMealStatsTotalHomeDeliveryTextView);
        messHomeUpcomingMealHeaderTextView = view.findViewById(R.id
                .messHomeUpcomingMealHeaderTextView);
        messHomeUpcomingMealStatsTotalOrdersReceivedTextView = view.findViewById(R
                .id.messHomeUpcomingMealStatsTotalOrdersReceivedTextView);
        messHomeUpcomingMealStatsTotalInMessTextView = view.findViewById(R
                .id.messHomeUpcomingMealStatsTotalInMessTextView);
        messHomeNextMealHeaderTextView = view.findViewById(R.id
                .messHomeNextMealHeaderTextView);
        messHomeUpcomingMealStatsTotalHomeDeliveryTextView = view.findViewById(R.id
                .messHomeUpcomingMealStatsTotalHomeDeliveryTextView);
        messHomeNextMealNotSetTextView = view.findViewById(R.id
                .messHomeNextMealNotSetTextView);
        messHomeNextMealSpecialTextView = view.findViewById(R.id
                .messHomeNextMealSpecialTextView);
        messHomeUpcomingMealItemsHeader = view.findViewById(R.id
                .messHomeUpcomingMealItemsHeader);
        messHomeNextMealItemsHeader = view.findViewById(R.id
                .messHomeNextMealItemsHeader);
        messHomeVerificationCodeEditText = view.findViewById(R.id
                .messHomeVerificationCodeEditText);
        messHomeAttendButton = view.findViewById(R.id.messHomeAttendButton);
        messHomeNextMealSetMealButton = view.findViewById(R.id
                .messHomeNextMealSetMealButton);
        messHomeCurrentMealItemsLinearLayout = view.findViewById(R.id
                .messHomeCurrentMealItemsLinearLayout);
        messHomeNextMealItemsLinearLayout = view.findViewById(R.id
                .messHomeNextMealItemsLinearLayout);
        messHomeReceiveOrdersSwitch = view.findViewById(R.id.messHomeReceiveOrdersSwitch);
        messHomeNextMealPriceTextView = view.findViewById(R.id.messHomeNextMealPriceTextView);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progress Dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {

        messHomeReceiveOrdersSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleSwitch();
            }
        });

        // attend customer when attend button is clicked
        messHomeAttendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendCustomer();
            }
        });

        // set next meal when set meal button is clicked
        messHomeNextMealSetMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessSelectMealActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void getHeaderDetails () {
        // for debugging purpose
        Log.e("header details", "reached");

        // get lunch or dinners and dates
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // for debugging purpose
                Log.e("header snap shot", "received");

                current_meal_l_or_d = snapshot.child("Current Lunch or Dinner")
                        .getValue(String.class);
                current_meal_date = snapshot.child("Current Date").getValue(String.class);
                upcoming_meal_l_or_d = snapshot.child("Upcoming Lunch or Dinner")
                        .getValue(String.class);
                upcoming_meal_date = snapshot.child("Upcoming Date").getValue(String.class);
                next_meal_l_or_d = snapshot.child("Mess Next Lunch or Dinner")
                        .getValue(String.class);
                next_meal_date = snapshot.child("Mess Next Date").getValue(String.class);

                if (current_meal_l_or_d != null && current_meal_date != null
                        && upcoming_meal_l_or_d != null && upcoming_meal_date != null
                        && next_meal_l_or_d != null && next_meal_date != null) {

                    // for debugging purpose
                    Log.d("current date", current_meal_date);
                    Log.d("next date", next_meal_date);

                    // now get meal ids
                    getMealIds();


                } else {

                    // for debugging purpose
                    Log.e("dates", "not found");

                    // stop progress dialog
                    stopProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("dates", error.getDetails());

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void getMealIds () {
        // for debugging purpose
        Log.e("get meal id", "reached");

        databaseReference.child("Mess").child(user.getUid()).child("Current Meals")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // for debugging purpose
                        Log.e("meal snap shot", "received");

                        upcoming_meal_id = snapshot.child(upcoming_meal_date)
                                .child(upcoming_meal_l_or_d).child("Meal Id").getValue(String.class);

                        if (snapshot.child(upcoming_meal_date).child(upcoming_meal_l_or_d)
                                .child("Available").getValue(Integer.class) != null) {
                            upcoming_meal_switch = snapshot.child(upcoming_meal_date)
                                    .child(upcoming_meal_l_or_d).child("Available").getValue(Integer.class);
                        }

                        // set switch
                        if (upcoming_meal_switch == 0) {
                            messHomeReceiveOrdersSwitch.setChecked(false);
                        } else {
                            messHomeReceiveOrdersSwitch.setChecked(true);
                        }

                        next_meal_id = snapshot.child(next_meal_date).child(next_meal_l_or_d)
                                .child("Meal Id").getValue(String.class);

                        if (upcoming_meal_id != null) {

                            // for debugging purpose
                            Log.d("upcoming meal id", upcoming_meal_id);

                            // get upcoming meal details
                            getUpcomingMealDetails();

                        } else {

                            // for debugging purpose
                            Log.e("upcoming meal id", "not found");

                        }

                        if (next_meal_id != null) {

                            // get next meal details
                            getNextMealDetails();
                        } else {

                            // for debugging purpose
                            Log.e("next meal id", "not found");
                        }

                        // get current order stats
                        getCurrentOrderStats();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("get meal ids", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void getCurrentOrderStats () {

        // for debugging purpose
        Log.e("current order", "reached");

        databaseReference.child("Mess").child(user.getUid()).child("Orders")
                .child(current_meal_date).child(current_meal_l_or_d)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // for debugging purpose
                Log.e("current order snap shot", "received");

                current_total_home_delivery = snapshot.child("Home Delivery").child("Orders")
                        .getChildrenCount();

                current_total_in_mess = snapshot.child("In Mess").child("Orders")
                        .getChildrenCount();

                // for debugging purpose
                Log.d("current home del", String.valueOf(current_total_home_delivery));
                Log.d("current in mess", String.valueOf(current_total_in_mess));

                // get upcoming order stats
                getUpcomingOrderStats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("current stats", error.getDetails());

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void getUpcomingOrderStats () {

        // for debugging purpose
        Log.e("upcoming order", "reached");

        databaseReference.child("Mess").child(user.getUid()).child("Orders")
                .child(upcoming_meal_date).child(upcoming_meal_l_or_d)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // for debugging purpose
                        Log.e("upcoming snap shot", "received");

                        upcoming_meal_total_home_delivery = snapshot.child("Home Delivery")
                                .child("Orders").getChildrenCount();

                        upcoming_meal_total_in_mess = snapshot.child("In Mess").child("Orders")
                                .getChildrenCount();

                        upcoming_meal_total_order = upcoming_meal_total_home_delivery +
                                upcoming_meal_total_in_mess;

                        // for debugging purpose
                        Log.d("upcoming home del",
                                String.valueOf(upcoming_meal_total_home_delivery));
                        Log.d("upcoming in mess", String.valueOf(upcoming_meal_total_in_mess));

                        // set texts
                        setTexts();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("current stats", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void getUpcomingMealDetails () {

        // for debugging purpose
        Log.e("up meal details", "reached");

        databaseReference.child("Mess").child(user.getUid()).child("Meals").child(upcoming_meal_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // for debugging purpose
                Log.e("up meal snap shot", "received");

                DataSnapshot mealNode = snapshot;

                String mess_id, mess_name, meal_image_link, special_or_regular, item_names = "";
                int meal_price = -1;
                ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                mess_id = mealNode.child("Mess Id").getValue(String.class);
                mess_name = mealNode.child("Mess Name").getValue(String.class);
                meal_image_link = mealNode.child("Picture Download Link").getValue(String.class);
                special_or_regular = mealNode.child("Special or Normal").getValue(String.class);

                if (mealNode.child("Price").getValue(Integer.class) != null) {
                    meal_price = mealNode.child("Price").getValue(Integer.class);
                }

                for ( DataSnapshot itemNode :  mealNode.child("Items").getChildren()) {
                    String item_id = itemNode.getKey();
                    String name = itemNode.child("Name").getValue(String.class);
                    String amount = itemNode.child("Amount").getValue(String.class);

                    // check if name and amount are not null
                    if (name != null && amount != null) {

                        // for debugging purpose
                        Log.d("item name", name);

                        MealItem item = new MealItem(name, amount);
                        mealItemArrayList.add(item);

                        item_names = item_names +" " +  name;
                    } else {
                        // for debugging purpose
                        Log.e("item name", "not found");
                    }
                }

                if (upcoming_meal_id != null && mess_id != null && meal_price > 0) {

                    MessMeal messMeal = new MessMeal(upcoming_meal_id, mess_id, mess_name,
                            special_or_regular, item_names, meal_price, mealItemArrayList);

                    if (messMeal != null) {
                        setUpcomingMeal(messMeal);
                    }
                }

                // stop progress dialog
                stopProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("meal detail", error.getDetails());

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void setUpcomingMeal (MessMeal meal) {

        // set meal items
        for (MealItem mealItem : meal.getMealItemArrayList()) {
            View view = getLayoutInflater().inflate(
                    R.layout.customer_meal_item_item,
                    messHomeCurrentMealItemsLinearLayout, false);

            // declare and initialize textViews used
            TextView name, quantity;
            name = view.findViewById(R.id.customerMealItemItemNameTextView);
            quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

            // set texts
            name.setText(mealItem.getItem_name());
            quantity.setText(mealItem.getQuantity());

            // add this view to linear layout
            messHomeCurrentMealItemsLinearLayout.addView(view);
        }

        // stop progress dialog
        startProgressDialog();
    }

    private void getNextMealDetails () {
        databaseReference.child("Mess").child(user.getUid()).child("Meals").child(next_meal_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        DataSnapshot mealNode = snapshot;

                        String mess_id, mess_name, meal_image_link, special_or_regular, item_names = "";
                        int meal_price = -1;
                        ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                        mess_id = mealNode.child("Mess Id").getValue(String.class);
                        mess_name = mealNode.child("Mess Name").getValue(String.class);
                        meal_image_link = mealNode.child("Picture Download Link").getValue(String.class);
                        special_or_regular = mealNode.child("Special or Normal").getValue(String.class);

                        if (mealNode.child("Price").getValue(Integer.class) != null) {
                            meal_price = mealNode.child("Price").getValue(Integer.class);
                        }

                        for ( DataSnapshot itemNode :  mealNode.child("Items").getChildren()) {
                            String item_id = itemNode.getKey();
                            String name = itemNode.child("Name").getValue(String.class);
                            String amount = itemNode.child("Amount").getValue(String.class);

                            // check if name and amount are not null
                            if (name != null && amount != null) {

                                // for debugging purpose
                                Log.d("item name", name);

                                MealItem item = new MealItem(name, amount);
                                mealItemArrayList.add(item);

                                item_names = item_names +" " +  name;
                            } else {
                                // for debugging purpose
                                Log.e("item name", "not found");
                            }
                        }

                        if (next_meal_id != null && mess_id != null && meal_price > 0) {

                            MessMeal messMeal = new MessMeal(next_meal_id, mess_id, mess_name,
                                    special_or_regular, item_names, meal_price, mealItemArrayList);

                            if (messMeal != null) {
                                setNextMeal(messMeal);
                            }
                        }

                        // stop progress dialog
                        stopProgressDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // for debugging purpose
                        Log.e("meal detail", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void setNextMeal (MessMeal meal) {
        if (meal != null) {

            // set visibility of button to gone
            messHomeNextMealSetMealButton.setVisibility(View.GONE);

            // set visibility of not set to gone
            messHomeNextMealNotSetTextView.setVisibility(View.GONE);

            // set special or not
            if (meal.getSpecial_or_regular().toLowerCase().equals("special")) {
                messHomeNextMealSpecialImageView.setVisibility(View.VISIBLE);
                messHomeNextMealSpecialTextView.setVisibility(View.VISIBLE);
            }

            // set price
            messHomeNextMealPriceTextView.setText("Rs " + String.valueOf(meal.getMeal_price()));
            messHomeNextMealPriceTextView.setVisibility(View.VISIBLE);

            messHomeNextMealItemsHeader.setVisibility(View.VISIBLE);

            for (MealItem mealItem : meal.getMealItemArrayList()) {
                View view = getLayoutInflater().inflate(
                        R.layout.customer_meal_item_item,
                        messHomeNextMealItemsLinearLayout, false);

                // declare and initialize textViews used
                TextView name, quantity;
                name = view.findViewById(R.id.customerMealItemItemNameTextView);
                quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

                // set texts
                name.setText(mealItem.getItem_name());
                quantity.setText(mealItem.getQuantity());

                // add this view to linear layout
                messHomeNextMealItemsLinearLayout.addView(view);
            }

        }
    }

    private void setTexts() {
        if (current_meal_date != null && current_meal_l_or_d != null) {
            messHomeHeaderTextView.setText(current_meal_l_or_d + " for " + current_meal_date);
        } else {
            messHomeHeaderTextView.setText("");
        }

        messHomeCurrentMealStatsTotalHomeDeliveryTextView.setText(String
                .valueOf(current_total_home_delivery));
        messHomeCurrentMealStatsTotalInMessTextView.setText(String.valueOf(current_total_in_mess));

        if (upcoming_meal_date != null && upcoming_meal_l_or_d != null) {
            messHomeUpcomingMealHeaderTextView.setText(upcoming_meal_l_or_d + " for " + upcoming_meal_date);
        } else {
            messHomeUpcomingMealHeaderTextView.setText("");
        }

        messHomeUpcomingMealStatsTotalOrdersReceivedTextView
                .setText(String.valueOf(upcoming_meal_total_order));
        messHomeUpcomingMealStatsTotalHomeDeliveryTextView
                .setText(String.valueOf(upcoming_meal_total_home_delivery));
        messHomeUpcomingMealStatsTotalInMessTextView
                .setText(String.valueOf(upcoming_meal_total_in_mess));

        if (next_meal_date != null && next_meal_l_or_d != null) {
            messHomeNextMealHeaderTextView.setText(next_meal_l_or_d + " for " + next_meal_date);
        } else {
            messHomeNextMealHeaderTextView.setText("");
        }

        // stop progress dialog
        stopProgressDialog();
    }

    private void toggleSwitch () {
        if (messHomeReceiveOrdersSwitch.isChecked()) {
            upcoming_meal_switch = 1;
        } else {
            upcoming_meal_switch = 0;
        }
        changeAvailability();
    }

    private void changeAvailability () {
        databaseReference.child("Mess").child(user.getUid()).child("Current Meals")
                .child(upcoming_meal_date).child(upcoming_meal_l_or_d).child("Available")
                .setValue(upcoming_meal_switch);
    }

    private void attendCustomer () {

        String customer_security_code = messHomeVerificationCodeEditText.getText().toString();

        databaseReference.child("Mess").child(user.getUid()).child("Current Meals")
                .child(current_meal_date).child(current_meal_l_or_d).child("Security Codes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(customer_security_code)) {

                    // check if unattended
                    if (snapshot.child(customer_security_code).child("Status")
                            .getValue(String.class).toLowerCase().equals("pending")) {

                        // set customer attended
                        setCustomerAttended(snapshot.child(customer_security_code).child("Order Id")
                                .getValue(String.class), customer_security_code);


                    } else {

                        // show alert dialog stating already attended
                        showAlertDialog("Already attended");

                    }

                } else {

                    // show alert dialog stating security code does not match
                    showAlertDialog("security code does not match");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("attend customer", error.getDetails());
            }
        });
    }

    private void setCustomerAttended (String orderId, String security_code) {

        // get the current time
        Date currentTime = Calendar.getInstance().getTime();

        // mark order status as attended
        databaseReference.child("Mess").child(user.getUid()).child("Current Meals")
                .child(current_meal_date).child(current_meal_l_or_d).child("Security Codes")
                .child(security_code).child("Status").setValue("attended at "
                + currentTime.toString());

        // mark order status as attended
        databaseReference.child("Orders").child(orderId).child("Status").setValue("attended at "
                + currentTime.toString());
        databaseReference.child("Orders").child(orderId).child("Delivered Time")
                .setValue(currentTime.toString());

        // alert mess that customer is attended
        Toast.makeText(getContext(), "Attended", Toast.LENGTH_LONG).show();

    }

    private void showAlertDialog ( String message) {
        // set the alert dialog
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
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