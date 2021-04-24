package com.pkan.official.mess.meals;

import com.pkan.official.customer.order.MealItem;

import java.util.ArrayList;

public class MessMeal {

    String meal_id, mess_id, mess_name, meal_image_link, special_or_regular, item_names;
    int meal_price;

    ArrayList<MealItem> mealItemArrayList;

    public MessMeal(String meal_id, String mess_id, String mess_name,
                    String special_or_regular, String item_names, int meal_price,
                    ArrayList<MealItem> mealItemArrayList) {
        this.meal_id = meal_id;
        this.mess_id = mess_id;
        this.mess_name = mess_name;
        this.special_or_regular = special_or_regular;
        this.item_names = item_names;
        this.meal_price = meal_price;
        this.mealItemArrayList = mealItemArrayList;
    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getMess_id() {
        return mess_id;
    }

    public void setMess_id(String mess_id) {
        this.mess_id = mess_id;
    }

    public String getMess_name() {
        return mess_name;
    }

    public void setMess_name(String mess_name) {
        this.mess_name = mess_name;
    }

    public String getMeal_image_link() {
        return meal_image_link;
    }

    public void setMeal_image_link(String meal_image_link) {
        this.meal_image_link = meal_image_link;
    }

    public String getSpecial_or_regular() {
        return special_or_regular;
    }

    public void setSpecial_or_regular(String special_or_regular) {
        this.special_or_regular = special_or_regular;
    }

    public String getItem_names() {
        return item_names;
    }

    public void setItem_names(String item_names) {
        this.item_names = item_names;
    }

    public int getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(int meal_price) {
        this.meal_price = meal_price;
    }

    public ArrayList<MealItem> getMealItemArrayList() {
        return mealItemArrayList;
    }

    public void setMealItemArrayList(ArrayList<MealItem> mealItemArrayList) {
        this.mealItemArrayList = mealItemArrayList;
    }
}
