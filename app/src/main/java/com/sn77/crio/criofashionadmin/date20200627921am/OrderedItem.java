package com.sn77.crio.criofashionadmin.date20200627921am;

public class OrderedItem {
    String customer_name,customer_phone_number,alternative_number,product_id,product_color,product_size,product_image_url
            ,gift_message,message_to_seller,delivery_partner,pickup_date,order_id,date_time;

    public OrderedItem(){

    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }

    public String getAlternative_number() {
        return alternative_number;
    }

    public void setAlternative_number(String alternative_number) {
        this.alternative_number = alternative_number;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_color() {
        return product_color;
    }

    public void setProduct_color(String product_color) {
        this.product_color = product_color;
    }

    public String getProduct_size() {
        return product_size;
    }

    public void setProduct_size(String product_size) {
        this.product_size = product_size;
    }

    public String getProduct_image_url() {
        return product_image_url;
    }

    public void setProduct_image_url(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    public String getGift_message() {
        return gift_message;
    }

    public void setGift_message(String gift_message) {
        this.gift_message = gift_message;
    }

    public String getMessage_to_seller() {
        return message_to_seller;
    }

    public void setMessage_to_seller(String message_to_seller) {
        this.message_to_seller = message_to_seller;
    }

    public String getDelivery_partner() {
        return delivery_partner;
    }

    public void setDelivery_partner(String delivery_partner) {
        this.delivery_partner = delivery_partner;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public OrderedItem(String customer_name, String customer_phone_number, String alternative_number, String product_id, String product_color, String product_size, String product_image_url, String gift_message, String message_to_seller,
                        String delivery_partner, String pickup_date, String order_id, String date_time) {
        this.customer_name = customer_name;
        this.customer_phone_number = customer_phone_number;
        this.alternative_number = alternative_number;
        this.product_id = product_id;
        this.product_color = product_color;
        this.product_size = product_size;
        this.product_image_url = product_image_url;
        this.gift_message = gift_message;
        this.message_to_seller = message_to_seller;
        this.delivery_partner = delivery_partner;
        this.pickup_date = pickup_date;
        this.order_id = order_id;
        this.date_time = date_time;
    }
}
