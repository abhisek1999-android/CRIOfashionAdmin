package com.sn77.crio.criofashionadmin.date20200627921am;

public class ReturnedItem {

    String order_id,date_time,delivery_partner,pickup_date,return_id;

    public ReturnedItem(){}

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

    public String getReturn_id() {
        return return_id;
    }

    public void setReturn_id(String return_id) {
        this.return_id = return_id;
    }

    public ReturnedItem(String order_id, String date_time, String delivery_partner, String pickup_date, String return_id) {
        this.order_id = order_id;
        this.date_time = date_time;
        this.delivery_partner = delivery_partner;
        this.pickup_date = pickup_date;
        this.return_id = return_id;
    }
}
