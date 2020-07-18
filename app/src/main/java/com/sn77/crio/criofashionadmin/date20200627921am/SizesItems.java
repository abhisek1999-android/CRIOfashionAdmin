package com.sn77.crio.criofashionadmin.date20200627921am;

public class SizesItems {

   Long no_of_pices;//this should be chnaged to the database spalling incorrect
   Long price;
    String size;

    public SizesItems(){

    }

    public Long getNo_of_pices() {
        return no_of_pices;
    }

    public void setNo_of_pices(Long no_of_pices) {
        this.no_of_pices = no_of_pices;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public SizesItems(Long no_of_pices, Long price, String size) {
        this.no_of_pices = no_of_pices;
        this.price = price;
        this.size = size;
    }
}
