package com.pkan.official.customer.order;

public class MealItem {
    String item_name;
    String quantity;

    public MealItem(String item_name, String quantity) {
        this.item_name = item_name;
        this.quantity = quantity;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
