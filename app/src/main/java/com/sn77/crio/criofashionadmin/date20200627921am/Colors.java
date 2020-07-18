package com.sn77.crio.criofashionadmin.date20200627921am;

public class Colors {

    String color_code;
    String color_image_url;
    String color_name;

    public Colors(){

    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getColor_image_url() {
        return color_image_url;
    }

    public void setColor_image_url(String color_image_url) {
        this.color_image_url = color_image_url;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public Colors(String color_code, String color_image_url, String color_name) {
        this.color_code = color_code;
        this.color_image_url = color_image_url;
        this.color_name = color_name;
    }
}
