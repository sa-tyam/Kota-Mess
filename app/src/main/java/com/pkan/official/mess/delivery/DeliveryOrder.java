package com.pkan.official.mess.delivery;

public class DeliveryOrder {
    String order_id, customer_name, house_number, room_number, landmark, phone_number;

    public DeliveryOrder(String order_id, String customer_name, String house_number,
                         String room_number, String landmark, String phone_number) {
        this.order_id = order_id;
        this.customer_name = customer_name;
        this.house_number = house_number;
        this.room_number = room_number;
        this.landmark = landmark;
        this.phone_number = phone_number;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getHouse_number() {
        return house_number;
    }

    public void setHouse_number(String house_number) {
        this.house_number = house_number;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
