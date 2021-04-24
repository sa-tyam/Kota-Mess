package com.pkan.official.mess.delivery;

import android.util.Log;

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

import java.util.ArrayList;

public class GetOrderList {

    static ArrayList<DeliveryOrder> deliveryOrderArrayList = new ArrayList<>();

    // declare and initialize firebase variables
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // declare interface to track changes in data
    public interface DataStatus {
        void DataIsLoaded(ArrayList<DeliveryOrder> deliveryOrderArrayList);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();
    }

    // other variables used
    static String current_meal_l_or_d, current_meal_date;

    public static void getDataFromDatabase(DataStatus dataStatus, String mess_id) {

        // get lunch or dinners and dates
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        current_meal_l_or_d = snapshot.child("Current Lunch or Dinner")
                                .getValue(String.class);
                        current_meal_date = snapshot.child("Current Date").getValue(String.class);


                        if (current_meal_l_or_d != null && current_meal_date != null) {

                            // for debugging purpose
                            Log.d("current date", current_meal_date);

                            getOrderIds(dataStatus, mess_id);

                        } else {

                            // for debugging purpose
                            Log.d("current date", "not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("dates", error.getDetails());
                    }
                });
    }

    public static void getOrderIds (DataStatus dataStatus, String mess_id) {

        databaseReference.child("Mess").child(mess_id).child("Orders").child(current_meal_date)
                .child(current_meal_l_or_d).child("Home Delivery").child("Orders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String order_id = snapshot.child("Order Id").getValue(String.class);

                if (order_id != null) {

                    // for debugging purpose
                    Log.d("order id", order_id);

                    getOrderDetails(dataStatus, order_id);
                } else {

                    // for debugging purpose
                    Log.e("order id", "not found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("order id", error.getDetails());
            }
        });
    }

    public static void getOrderDetails (DataStatus dataStatus, String order_id) {

        databaseReference.child("Orders").child(order_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String customer_id = snapshot.child("Customer Id").getValue(String.class);

                if (customer_id != null) {

                    // for debugging purpose
                    Log.d("customer id", customer_id);

                    getCustomerDetails(dataStatus, order_id, customer_id);

                } else {

                    // for debugging purpose
                    Log.e("customer id", "not found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("customer id", error.getDetails());
            }
        });
    }

    public static void getCustomerDetails (DataStatus dataStatus, String order_id, String customer_id) {
        databaseReference.child("Customers").child(customer_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "", house_number = "", room_number = "",
                        landmark = "", phone_number = "", area_name = "";

                name = snapshot.child("Profile").child("Name").getValue(String.class);
                house_number = snapshot.child("Address").child("House Number").getValue(String.class);
                room_number = snapshot.child("Address").child("Room Number").getValue(String.class);
                landmark = snapshot.child("Address").child("Landmark").getValue(String.class);
                area_name = snapshot.child("Address").child("Area").getValue(String.class);
                phone_number = snapshot.child("Profile").child("Phone Number")
                        .getValue(String.class);

                if (order_id != null && name != null && house_number != null &&
                        room_number != null && landmark != null &&
                        phone_number != null) {

                    // for debugging purpose
                    Log.d("customer name", name);

                    DeliveryOrder deliveryOrder = new DeliveryOrder(order_id, name,
                            house_number, room_number, landmark, phone_number, area_name);

                    deliveryOrderArrayList.add(deliveryOrder);

                    dataStatus.DataIsLoaded(deliveryOrderArrayList);
                } else {

                    // for debugging purpose
                    Log.e("customer details", "not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("customer detail", error.getDetails());
            }
        });
    }

}
