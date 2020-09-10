package com.sn77.crio.criofashionadmin.date20200627921am;

public class SizesItems {

   Long pieces;//this should be chnaged to the database spalling incorrect
  Double max_price;
 Double min_price;
    String size;

    public SizesItems(){

    }

    public SizesItems(Long pieces, Double max_price, Double min_price, String size) {
        this.pieces = pieces;
        this.max_price = max_price;
        this.min_price = min_price;
        this.size = size;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double max_price) {
        this.max_price = max_price;
    }

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double min_price) {
        this.min_price = min_price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
