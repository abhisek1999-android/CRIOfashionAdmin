package com.sn77.crio.criofashionadmin.date20200627921am;

public class PackageDetails {
    String package_width;
    String package_height;
    String package_depth;

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public PackageDetails(String weight) {
        this.weight = weight;
    }

    String weight;


  public  PackageDetails(){

    }

    public String getPackage_width() {
        return package_width;
    }

    public void setPackage_width(String package_width) {
        this.package_width = package_width;
    }

    public String getPackage_height() {
        return package_height;
    }

    public void setPackage_height(String package_height) {
        this.package_height = package_height;
    }

    public String getPackage_depth() {
        return package_depth;
    }

    public void setPackage_depth(String package_depth) {
        this.package_depth = package_depth;
    }

    public PackageDetails(String package_width, String package_height, String package_depth) {
        this.package_width = package_width;
        this.package_height = package_height;
        this.package_depth = package_depth;
    }
}
