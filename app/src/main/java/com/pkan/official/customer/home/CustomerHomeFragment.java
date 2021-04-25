package com.pkan.official.customer.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.history.CustomerHistoryDetailActivity;
import com.pkan.official.customer.order.MealItem;
import com.pkan.official.customer.order.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHomeFragment extends Fragment {

    // default generated code
    // start coding from line 64

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHomeFragment newInstance(String param1, String param2) {
        CustomerHomeFragment fragment = new CustomerHomeFragment();
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


    // declare view
    View view;

    // views to be used in fragment

    ViewPager customerHomeViewPager;

    LinearLayout customerHomeCurrentOrderLinearLayout, customerHomeUpcomingOrderLinearLayout;

    TextView customerHomeCurrentOrderTitleTextView, customerHomeNextOrderTitleTextView;

    // view pager adapter
    CustomerHomeViewPagerAdapter customerHomeViewPagerAdapter;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress Dialog
    ProgressDialog progressDialog;

    // variables used in functions
    String current_meal_l_or_d, current_meal_date;
    String upcoming_meal_l_or_d, upcoming_meal_date;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        // initialize the views and variables used in fragment
        initViews();

        // set poster view pager
        setPosterViewPager();

        // disable screen, show progress dialog and set current meal
        startProgressDialog();

        // get header details
        getHeaderDetails();

        return view;
    }

    private void initViews() {

        // initialize the views
        customerHomeViewPager = view.findViewById(R.id.customerHomeViewPager);
        customerHomeCurrentOrderLinearLayout = view.findViewById(R.id
                .customerHomeCurrentOrderLinearLayout);
        customerHomeUpcomingOrderLinearLayout = view.findViewById(R.id
                .customerHomeUpcomingOrderLinearLayout);
        customerHomeCurrentOrderTitleTextView = view.findViewById(R.id
                .customerHomeCurrentOrderTitleTextView);
        customerHomeNextOrderTitleTextView = view.findViewById(R.id
                .customerHomeNextOrderTitleTextView);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setPosterViewPager() {

        // declare and initialize array list that contains poster image links
        ArrayList<String> imageUrls = new ArrayList<String>();

        // get a place holder
        String placeholder = "https://lanecdr.org/wp-content/uploads/2019/08/placeholder.png";
        imageUrls.add(placeholder);
        imageUrls.add(placeholder);

        databaseReference.child("Posters").child("Customers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // for debugging purpose
                        Log.e("poster snapshot", "received");
                        Log.e("child count", String.valueOf(snapshot.getChildrenCount()));

                        // clear imageUrls
                        imageUrls.clear();

                        // add the links to array list
                        for (DataSnapshot imageNode : snapshot.getChildren()) {

                            String url = imageNode.child("Picture Download Link").getValue(String.class);

                            if (url != null) {
                                // for debugging in case of error
                                Log.d("image link", url);

                                imageUrls.add(url);
                            }
                        }

                        // initialize the adapter and add it to view pager
                        customerHomeViewPagerAdapter = new CustomerHomeViewPagerAdapter(getContext(), imageUrls);
                        customerHomeViewPager.setAdapter(customerHomeViewPagerAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getHeaderDetails () {

        // get lunch or dinners and dates
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        current_meal_l_or_d = snapshot.child("Current Lunch or Dinner")
                                .getValue(String.class);
                        current_meal_date = snapshot.child("Current Date").getValue(String.class);
                        upcoming_meal_l_or_d = snapshot.child("Upcoming Lunch or Dinner")
                                .getValue(String.class);
                        upcoming_meal_date = snapshot.child("Upcoming Date").getValue(String.class);


                        if (current_meal_l_or_d != null && current_meal_date != null
                                && upcoming_meal_l_or_d != null && upcoming_meal_date != null) {

                            // for debugging purpose
                            Log.d("current date", current_meal_date);

                            // get the current and upcoming order
                            getCurrentOrder();
                            getUpComingOrder();

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

    private void getCurrentOrder() {
        databaseReference.child("Customers").child(user.getUid()).child("Current Order")
                .child(current_meal_date).child(current_meal_l_or_d)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // check if current order exists

                        if (snapshot.getChildrenCount() > 0) {
                            // current order is present
                            // get current order id

                            for (DataSnapshot orderNode : snapshot.getChildren()) {
                                String order_id = orderNode.child("Order Id")
                                        .getValue(String.class);

                                if (order_id != null) {

                                    // for debugging purpose, in case of error
                                    Log.d("order id", order_id);

                                    // get the order id
                                    getOrder(order_id, "current");

                                } else {
                                    // stop the progress dialog
                                    stopProgressDialog();

                                    // current order is not present

                                }
                            }

                        } else {

                            // stop the progress dialog
                            stopProgressDialog();

                            // current order is not present

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("get current order id", error.getDetails());
                    }
                });
    }

    private void getOrder(String order_id, String current_or_upcoming) {

        // get the order
        databaseReference.child("Orders").child(order_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // for debugging purpose
                        Log.e("order id", order_id);

                        // create the order item for current order

                        String order_id = "", mess_id = "", dish_id = "", customer_id = "",
                                mess_name = "", customer_name = "", order_time = "",
                                address = "", status = "", customer_phone_number = "",
                                mess_or_delivery = "", delivery_phone_number = "",
                                lunch_or_dinner = "", order_date = "", delivered_time = "",
                                security_code = "", review_id = "", meal_image_link = "";

                        ArrayList<MealItem> mealItems = new ArrayList<>();

                        int order_price = -1;

                        // set the values according to data
                        order_id = snapshot.child("Order Id").getValue(String.class);
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        dish_id = snapshot.child("Dish Id").getValue(String.class);
                        customer_id = snapshot.child("Customer Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);
                        customer_name = snapshot.child("Customer Name").getValue(String.class);
                        order_time = snapshot.child("Order Time").getValue(String.class);
                        address = snapshot.child("Address").getValue(String.class);
                        status = snapshot.child("Status").getValue(String.class);
                        customer_phone_number = snapshot.child("Customer Phone Number")
                                .getValue(String.class);
                        mess_or_delivery = snapshot.child("Mess or Delivery").getValue(String.class);
                        delivery_phone_number = snapshot.child("Delivery Phone Number")
                                .getValue(String.class);
                        lunch_or_dinner = snapshot.child("Lunch or Dinner").getValue(String.class);
                        order_date = snapshot.child("Order Date").getValue(String.class);
                        delivered_time = snapshot.child("Delivered Time").getValue(String.class);
                        security_code = snapshot.child("Security Code").getValue(String.class);
                        review_id = snapshot.child("Review Id").getValue(String.class);

                        if (snapshot.hasChild("Order Price")) {
                            order_price = snapshot.child("Order Price").getValue(Integer.class);
                        }
                        meal_image_link = snapshot.child("Meal Image Link").getValue(String.class);

                        // for debugging purpose
                        Log.e("order price", String.valueOf(order_price));

                        // get the items involved in the order meal
                        for (DataSnapshot itemKeyNode : snapshot.child("Items").getChildren()) {
                            String item_name = "", item_quantity = "";

                            item_name = itemKeyNode.child("Name").getValue(String.class);
                            item_quantity = itemKeyNode.child("Amount").getValue(String.class);

                            if (item_name != null && item_quantity != null) {

                                // for debugging purpose in case of error
                                Log.d("item name", item_name);

                                // add item to array list
                                mealItems.add(new MealItem(item_name, item_quantity));
                            }
                        }

                        // for debugging purpose in case of error
                        Log.d("order price", String.valueOf(order_price));

                        // create OrderItem object using these data
                        OrderItem orderItem = new OrderItem(order_id, mess_id, dish_id, customer_id,
                                mess_name, customer_name, order_time, address, status, customer_phone_number,
                                mess_or_delivery, lunch_or_dinner, order_date, order_price, mealItems, meal_image_link);

                        // set extra data if available
                        if (delivery_phone_number != null) {
                            orderItem.setDelivery_phone_number(delivery_phone_number);
                        }
                        if (delivered_time != null) {
                            orderItem.setDelivered_time(delivered_time);
                        }
                        if (security_code != null) {
                            orderItem.setSecurity_code(security_code);
                        }
                        if (review_id != null) {
                            orderItem.setReview_id(review_id);
                        }

                        if (orderItem != null) {

                            if (current_or_upcoming.equals("current")) {
                                setCurrentMeal(orderItem);
                            } else {
                                setUpcomingMeal(orderItem);
                            }
                        } else {

                            // show user something went wrong
                            Toast.makeText(getContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();

                            // enable screen
                            stopProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("get order", error.getDetails());
                    }
                });
    }

    private void setCurrentMeal(@NonNull OrderItem orderItem) {

        // create view object
        View meal_item_view = getLayoutInflater().inflate(R.layout.customer_home_meal_items,
                customerHomeCurrentOrderLinearLayout, false);

        // declare and initialize the views used inside meal item_view
        TextView customerHomeMealItemHeader, customerHomeMealItemMessNameTextView,
                customerHomeMealItemItemNamesTextView, customerHomeMealItemPriceTextView,
                customerHomeMealItemOrderStatus;

        ImageView customerHomeMealItemImageView, customerHomeMealItemCallImageView;

        // initialize the views
        customerHomeMealItemHeader = meal_item_view.findViewById(R.id.customerHomeMealItemHeader);
        customerHomeMealItemMessNameTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemMessNameTextView);
        customerHomeMealItemItemNamesTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemItemNamesTextView);
        customerHomeMealItemPriceTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemPriceTextView);
        customerHomeMealItemOrderStatus = meal_item_view.findViewById(R.id
                .customerHomeMealItemOrderStatus);
        customerHomeMealItemImageView = meal_item_view.findViewById(R.id
                .customerHomeMealItemImageView);
        customerHomeMealItemCallImageView = meal_item_view.findViewById(R.id
                .customerHomeMealItemCallImageView);

        // set the required data
        customerHomeMealItemHeader.setText(orderItem.getLunch_or_dinner() + " for " +
                orderItem.getOrder_date());

        customerHomeMealItemMessNameTextView.setText(orderItem.getMess_name());

        // prepare string for item names
        String items = "";
        if (orderItem.getItemArrayList().size() > 0) {
            items = orderItem.getItemArrayList().get(0).getItem_name();
        }

        for (int i = 1; i < orderItem.getItemArrayList().size(); i++) {
            items += " + " + orderItem.getItemArrayList().get(i).getItem_name();
        }

        customerHomeMealItemItemNamesTextView.setText(items);

        customerHomeMealItemPriceTextView.setText("\u20B9" + " " + orderItem.getOrder_price());

        customerHomeMealItemOrderStatus.setText(orderItem.getStatus());

        // set the current meal image
        Picasso.get()
                .load(orderItem.getMeal_image_link())
                .into(customerHomeMealItemImageView);

        meal_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerHistoryDetailActivity.class);
                intent.putExtra("orderId", orderItem.getOrder_id());
                getContext().startActivity(intent);
            }
        });

        // set the call option when call image view is clicked
        customerHomeMealItemCallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + orderItem.getDelivery_phone_number()));
                startActivity(intent);
            }
        });

        // add this view to the linear layout
        customerHomeCurrentOrderLinearLayout.addView(meal_item_view);

        // enable the screen and stop the progress dialog
        stopProgressDialog();

    }

    private void getUpComingOrder() {

        // check if upcoming order exists
        databaseReference.child("Customers").child(user.getUid()).child("Current Order")
                .child(upcoming_meal_date).child(upcoming_meal_l_or_d)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // check if current order exists

                        if (snapshot.getChildrenCount() > 0) {
                            // current order is present
                            // get current order id

                            for (DataSnapshot orderNode : snapshot.getChildren()) {
                                String order_id = orderNode.child("Order Id")
                                        .getValue(String.class);

                                if (order_id != null) {

                                    // for debugging purpose, in case of error
                                    Log.d("order id", order_id);

                                    // get the order id
                                    getOrder(order_id, "next");


                                } else {
                                    // stop the progress dialog
                                    stopProgressDialog();

                                    // current order is not present

                                }
                            }

                        } else {

                            // stop the progress dialog
                            stopProgressDialog();

                            // current order is not present

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("get current order id", error.getDetails());
                    }
                });
    }

    private void setUpcomingMeal(OrderItem orderItem) {
        // create view object
        View meal_item_view = getLayoutInflater().inflate(R.layout.customer_home_meal_items,
                customerHomeUpcomingOrderLinearLayout, false);

        // declare and initialize the views used inside meal item_view
        TextView customerHomeMealItemHeader, customerHomeMealItemMessNameTextView,
                customerHomeMealItemItemNamesTextView, customerHomeMealItemPriceTextView,
                customerHomeMealItemOrderStatus;

        ImageView customerHomeMealItemImageView, customerHomeMealItemCallImageView;

        // initialize the views
        customerHomeMealItemHeader = meal_item_view.findViewById(R.id.customerHomeMealItemHeader);
        customerHomeMealItemMessNameTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemMessNameTextView);
        customerHomeMealItemItemNamesTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemItemNamesTextView);
        customerHomeMealItemPriceTextView = meal_item_view.findViewById(R.id
                .customerHomeMealItemPriceTextView);
        customerHomeMealItemOrderStatus = meal_item_view.findViewById(R.id
                .customerHomeMealItemOrderStatus);
        customerHomeMealItemImageView = meal_item_view.findViewById(R.id
                .customerHomeMealItemImageView);
        customerHomeMealItemCallImageView = meal_item_view.findViewById(R.id
                .customerHomeMealItemCallImageView);


        // set the required data
        customerHomeMealItemHeader.setText(orderItem.getLunch_or_dinner() + " for " +
                orderItem.getOrder_date());

        customerHomeMealItemMessNameTextView.setText(orderItem.getMess_name());

        // prepare string for item names
        String items = "";
        if (orderItem.getItemArrayList().size() > 0) {
            items = orderItem.getItemArrayList().get(0).getItem_name();
        }

        for (int i = 1; i < orderItem.getItemArrayList().size(); i++) {
            items += " + " + orderItem.getItemArrayList().get(i).getItem_name();
        }

        customerHomeMealItemItemNamesTextView.setText(items);

        customerHomeMealItemPriceTextView.setText("\u20B9" + " " + orderItem.getOrder_price());

        customerHomeMealItemOrderStatus.setText(orderItem.getStatus());

        // set the current meal image
        Picasso.get()
                .load(orderItem.getMeal_image_link())
                .into(customerHomeMealItemImageView);

        meal_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerHistoryDetailActivity.class);
                intent.putExtra("orderId", orderItem.getOrder_id());
                getContext().startActivity(intent);
            }
        });

        // set the call option when call image view is clicked
        customerHomeMealItemCallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + orderItem.getDelivery_phone_number()));
                startActivity(intent);
            }
        });

        // add this view to the linear layout
        customerHomeUpcomingOrderLinearLayout.addView(meal_item_view);

        // enable the screen and stop the progress dialog
        stopProgressDialog();
    }

    private void startProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void stopProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}