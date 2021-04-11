package com.pkan.official.customer.order;

public class OrderItem {
    String mess_id, dish_id, customer_id, mess_name, customer_name, order_time, address, status,
        customer_phone_number, mess_or_delivery, delivery_phone_number, lunch_or_dinner, order_date,
        delivered_time, security_code, review_id;

    public OrderItem(String mess_id, String dish_id, String customer_id,
                     String mess_name, String customer_name, String order_time,
                     String address, String status, String customer_phone_number,
                     String mess_or_delivery, String lunch_or_dinner, String order_date) {

        this.mess_id = mess_id;
        this.dish_id = dish_id;
        this.customer_id = customer_id;
        this.mess_name = mess_name;
        this.customer_name = customer_name;
        this.order_time = order_time;
        this.address = address;
        this.status = status;
        this.customer_phone_number = customer_phone_number;
        this.mess_or_delivery = mess_or_delivery;
        this.lunch_or_dinner = lunch_or_dinner;
        this.order_date = order_date;

    }

    public String getMess_id() {
        return mess_id;
    }

    public void setMess_id(String mess_id) {
        this.mess_id = mess_id;
    }

    public String getDish_id() {
        return dish_id;
    }

    public void setDish_id(String dish_id) {
        this.dish_id = dish_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMess_name() {
        return mess_name;
    }

    public void setMess_name(String mess_name) {
        this.mess_name = mess_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }

    public String getMess_or_delivery() {
        return mess_or_delivery;
    }

    public void setMess_or_delivery(String mess_or_delivery) {
        this.mess_or_delivery = mess_or_delivery;
    }

    public String getDelivery_phone_number() {
        return delivery_phone_number;
    }

    public void setDelivery_phone_number(String delivery_phone_number) {
        this.delivery_phone_number = delivery_phone_number;
    }

    public String getLunch_or_dinner() {
        return lunch_or_dinner;
    }

    public void setLunch_or_dinner(String lunch_or_dinner) {
        this.lunch_or_dinner = lunch_or_dinner;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDelivered_time() {
        return delivered_time;
    }

    public void setDelivered_time(String delivered_time) {
        this.delivered_time = delivered_time;
    }

    public String getSecurity_code() {
        return security_code;
    }

    public void setSecurity_code(String security_code) {
        this.security_code = security_code;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }
}
