package com.pkan.official.mess.meals;

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

public class GetMessMealList {

    // array list to be used in class
    static ArrayList<MessMeal> messMealArrayList = new ArrayList<>();


    //static firebase variables to be used in functions
    static FirebaseUser user;
    static DatabaseReference databaseReference;

    public interface DataStatus {
        void DataIsLoaded (ArrayList<MessMeal> messMealArrayList);
        void DataIsInserted ();
        void  DataIsUpdated ();
        void DataIsDeleted ();
    }

    public static void getDataFromDatabase (DataStatus dataStatus) {

        // clear array list
        messMealArrayList.clear();

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // get the meal list
        databaseReference.child("Mess").child(user.getUid()).child("Meals")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // iterate through all meals
                for (DataSnapshot mealNode : snapshot.getChildren()) {
                    String meal_id = mealNode.getKey();

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

                    if (meal_id != null && mess_id != null && meal_price > 0) {

                        MessMeal messMeal = new MessMeal(meal_id, mess_id, mess_name,
                                special_or_regular, item_names, meal_price, mealItemArrayList);

                        if (messMeal != null) {
                            messMealArrayList.add(messMeal);
                        }
                    }

                }
                dataStatus.DataIsLoaded(messMealArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("get mess meals", error.getDetails());
            }
        });
    }

}
