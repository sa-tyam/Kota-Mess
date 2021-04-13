package com.pkan.official.customer.history;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkan.official.customer.order.MealItem;
import com.pkan.official.customer.order.OrderItem;

import java.util.ArrayList;

public class GetCustomerHistoryList {

    // static order item list
    static ArrayList<OrderItem> orderItemArrayList = new ArrayList<>();

    // declare and initialize firebase variables
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    // declare interface to track changes in data
    public interface DataStatus {
        void DataIsLoaded (ArrayList<OrderItem> orderItemArrayList);
        void DataIsInserted ();
        void  DataIsUpdated ();
        void DataIsDeleted ();
    }

    public static void getDataFromDatabase (final DataStatus dataStatus) {

        // clear the order array list
        orderItemArrayList.clear();

        // order ids array list
        ArrayList<String> orderIdArrayList = new ArrayList<>();

        // fetch data from firebase
        databaseReference.child("Customers").child(user.getUid()).child("History")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keyNode : snapshot.getChildren()) {

                    // for debugging purpose in case of error
                    Log.d("order id", keyNode.getKey());

                    // add order id to array list
                    orderIdArrayList.add(keyNode.getKey());
                }


                // fetch the required orders from database
                // traverse the list in reverse order to get latest order first
                for (int i = orderIdArrayList.size() - 1; i >= 0; i-- ) {

                    // for debugging purpose
                    Log.d("id in loop", String.valueOf(orderIdArrayList.get(i)));

                    databaseReference.child("Orders").child(orderIdArrayList.get(i))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {



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
                                    security_code = String.valueOf(snapshot.child("Security Code").getValue(Integer.class));
                                    review_id = snapshot.child("Review Id").getValue(String.class);
                                    order_price = snapshot.child("Order Price").getValue(Integer.class);
                                    meal_image_link = snapshot.child("Meal Image Link").getValue(String.class);

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
                                    if (security_code != null){
                                        orderItem.setSecurity_code(security_code);
                                    }
                                    if (review_id != null) {
                                        orderItem.setReview_id(review_id);
                                    }

                                    // add this object to array list
                                    orderItemArrayList.add(orderItem);

                                    // notify data is loaded
                                    dataStatus.DataIsLoaded(orderItemArrayList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    // for debugging purpose in case of error
                                    Log.e("get order item", error.getDetails());
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose in case of error
                Log.e("get order ids", error.getDetails());
            }
        });
    }

}
