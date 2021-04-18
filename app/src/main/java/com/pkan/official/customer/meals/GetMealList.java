package com.pkan.official.customer.meals;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.customer.order.MealItem;

import java.util.ArrayList;

public class GetMealList {

    // static array list of meal
    public static ArrayList<Meal> mealArrayList = new ArrayList<>();

    //static firebase variables to be used in functions
    static FirebaseUser user;
    static DatabaseReference databaseReference;

    // declare interface to track changes in data
    public interface DataStatus {
        void DataIsLoaded (ArrayList<Meal> mealArrayList);
        void DataIsInserted ();
        void  DataIsUpdated ();
        void DataIsDeleted ();
    }

    public static void getDataFromDataBase (DataStatus dataStatus) {

        // clear array list
        mealArrayList.clear();

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // clear the array list
        mealArrayList.clear();

        // get customer area
        databaseReference.child("Customers").child(user.getUid()).child("Address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String customer_area_id = "", customer_city_id = "";
                customer_area_id = snapshot.child("Area Id").getValue(String.class);
                customer_city_id = snapshot.child("City Id").getValue(String.class);

                if (customer_area_id != null && customer_city_id != null) {

                    // for debugging in case of error
                    Log.d("customer area id", customer_area_id);

                    // once we get area id, get mess that deliver in this area
                    getMessList(dataStatus, customer_area_id, customer_city_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging purpose
                Log.d("customer area id", error.getDetails());
            }
        });

    }

    public static void getMessList (DataStatus dataStatus, String area_id, String city_id) {

        // get mess id list
        databaseReference.child("Areas").child(city_id).child(area_id).child("Mess")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get mess ids
                for (DataSnapshot keyNode : snapshot.getChildren()){

                    // get mess id and add it to
                    String mess_id = keyNode.getKey();

                    if (mess_id != null) {

                        // for debugging in case of error
                        Log.d("mess id", mess_id);

                        // get the current meal of the mess with the mess id
                        getLunchOrDinner(dataStatus, mess_id);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("get mess id", error.getDetails());
            }
        });
    }

    public static void getLunchOrDinner (DataStatus dataStatus, String mess_id) {

        // get lunch or dinner from database
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get lunch or dinner
                String lunch_or_dinner = snapshot.child("Upcoming Lunch or Dinner")
                        .getValue(String.class);

                if (lunch_or_dinner != null) {

                    // for debugging in case of error
                    Log.d("lunch or dinner", lunch_or_dinner);

                    // get upcoming meal id of mess
                    verifiedOrNot(dataStatus, lunch_or_dinner, mess_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void verifiedOrNot (DataStatus dataStatus, String lunch_or_dinner, String mess_id) {

        // check if mess is verified
        databaseReference.child("Mess").child(mess_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // check if verified or not
                        int verified = snapshot.child("Verified or Not").getValue(Integer.class);

                        // if verified move ahead
                        if (verified == 1) {
                            getMealId(dataStatus, lunch_or_dinner, mess_id);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void getMealId (DataStatus dataStatus, String lunch_or_dinner, String mess_id) {

        // get upcoming meal id of the mess
        databaseReference.child("Mess").child(mess_id).child("Current Meals")
                .child(lunch_or_dinner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get the meal id
                String meal_id = snapshot.child("Meal Id").getValue(String.class);

                if (meal_id != null) {

                    // for debugging in case of error
                    Log.d("meal id", meal_id);

                    // get the meal
                    getMeal(dataStatus, meal_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("get meal id", error.getDetails());
            }
        });
    }

    public static void getMeal (DataStatus dataStatus, String meal_id) {

        // get the meal
        databaseReference.child("Meals").child(meal_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // variables to be used
                String mealId, mess_id, mess_name, meal_image_link, special_or_regular;
                int meal_price;
                ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                // get the required values
                mealId = meal_id;
                mess_id = snapshot.child("Mess Id").getValue(String.class);
                mess_name = snapshot.child("Mess Name").getValue(String.class);
                meal_image_link = snapshot.child("Picture Download Link").getValue(String.class);
                special_or_regular = snapshot.child("Special or Normal").getValue(String.class);

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

                    // for debugging in case of error
                    Log.d("mess name", mess_name);

                    // create a new meal
                    Meal meal = new Meal(meal_id, mess_id, mess_name, special_or_regular,
                            meal_price, mealItemArrayList);

                    // set image link if available
                    if (meal_image_link != null) {
                        meal.setMeal_image_link(meal_image_link);
                    }

                    // add this meal to array list
                    mealArrayList.add(meal);

                    // notify data is loaded
                    dataStatus.DataIsLoaded(mealArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging in case of error
                Log.e("get meal", error.getDetails());
            }
        });
    }

}
