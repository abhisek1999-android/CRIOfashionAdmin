package com.sn77.crio.criofashionadmin.date20200627921am;

public class CompanyDetails {

    String company_name;
    String company_email;
    String seller_id;
    String company_phone_number;
    String GST_id;
    String user_image;
    String user_name;
    public CompanyDetails(){

    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getCompany_phone_number() {
        return company_phone_number;
    }

    public void setCompany_phone_number(String company_phone_number) {
        this.company_phone_number = company_phone_number;
    }

    public String getGST_id() {
        return GST_id;
    }

    public void setGST_id(String GST_id) {
        this.GST_id = GST_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public CompanyDetails(String company_name, String company_email, String seller_id, String company_phone_number, String GST_id, String user_image, String user_name) {
        this.company_name = company_name;
        this.company_email = company_email;
        this.seller_id = seller_id;
        this.company_phone_number = company_phone_number;
        this.GST_id = GST_id;
        this.user_image = user_image;
        this.user_name = user_name;
    }
}
