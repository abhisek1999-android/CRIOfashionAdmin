package com.sn77.crio.criofashionadmin.date20200627921am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> Jewellery = new ArrayList<String>();
            Jewellery.add("Imitation");
            Jewellery.add("Stone");
            Jewellery.add("Silver");
            Jewellery.add("Silver Plated");
            Jewellery.add("Gold Plated");

        List<String> Shirt = new ArrayList<String>();
            Shirt.add("T-Shirt");
            Shirt.add("Shirt");
            Shirt.add("Polo");
            Shirt.add("Formal Shirt");
            Shirt.add("Casual Shirt");


        List<String> Furniture = new ArrayList<String>();
            Furniture.add("Kids Stool");
            Furniture.add("Kids Chair");

        List<String> Saree = new ArrayList<String>();
           Saree.add("Tant");
           Saree.add("Silk");
           Saree.add("Kanjivaram");
           Saree.add("Benarasi");


        List<String> HomeAccessories = new ArrayList<String>();
        HomeAccessories.add("Cushion");
        HomeAccessories.add("Mosquito Net");
        HomeAccessories.add("Matress");
        HomeAccessories.add("Diwan");
        HomeAccessories.add("Garlands");
        HomeAccessories.add("Mosquito Repellent");



        List<String> Denim = new ArrayList<String>();
        Denim.add("Shirts");
        Denim.add("Jackets");
        Denim.add("Jeans");
        Denim.add("Jeggings");

        List<String> fashionGirls = new ArrayList<String>();
       fashionGirls.add("Top");
       fashionGirls.add("Kurti");
       fashionGirls.add("Lehenga");
       fashionGirls.add("Plazo");
       fashionGirls.add("Leggings");
       fashionGirls.add("One Piece");

       List<String> innerWear=new ArrayList<>();
       innerWear.add("Inner wear");

        expandableListDetail.put("Jewellery", Jewellery);
        expandableListDetail.put("Shirt", Shirt);
        expandableListDetail.put("Home Accessories", HomeAccessories);
        expandableListDetail.put("Furniture", Furniture);
        expandableListDetail.put("Saree", Saree);
        expandableListDetail.put("Denim", Denim);
        expandableListDetail.put("Girls Fashion",fashionGirls);
        expandableListDetail.put("Inner Wear",innerWear);

        return expandableListDetail;
    }
}
