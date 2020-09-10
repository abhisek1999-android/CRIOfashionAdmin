package com.sn77.crio.criofashionadmin.date20200627921am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();




        List<String> Furniture = new ArrayList<String>();
            Furniture.add("Kids Stool");
            Furniture.add("Kids Chair");
            Furniture.add("Stools");

        List<String> HomeAccessories = new ArrayList<String>();
        HomeAccessories.add("Cushion");
        HomeAccessories.add("Mosquito Net");
        HomeAccessories.add("Matress");
        HomeAccessories.add("Diwan");
        HomeAccessories.add("Garlands");
        HomeAccessories.add("Mat");
        HomeAccessories.add("Home Decoration");
        HomeAccessories.add("Pillow Covers");
        HomeAccessories.add("Bed Sheets");



        List<String> MansFashion = new ArrayList<String>();
        MansFashion.add("Shirts");
        MansFashion.add("T-Shirt");
        MansFashion.add("Jackets");
        MansFashion.add("Jeans");
        MansFashion.add("Trousers");
        MansFashion.add("Under Garments");
        MansFashion.add("Shorts");


     //   MansFashion.add("Jeggings");

        List<String> fashionGirls = new ArrayList<String>();
        fashionGirls.add("Top");
        fashionGirls.add("Kurti");
        fashionGirls.add("Lehenga");
        fashionGirls.add("Palazzo");
        fashionGirls.add("Leggings");
        fashionGirls.add("Jeggings");
        fashionGirls.add("Hot Pants");
        fashionGirls.add("Skirt");
        fashionGirls.add("One Piece");
        fashionGirls.add("Gawn");
        fashionGirls .add("T-Shirt");
        fashionGirls.add("Jackets");
        fashionGirls.add("Night Wear");
        fashionGirls.add("Saree");
        fashionGirls.add("Scerf");


        List<String> Stationary = new ArrayList<String>();
        Stationary.add("Pen");
        Stationary.add("Pencil");
        Stationary.add("Bag");
        Stationary.add("Educational kit");
        Stationary.add("Office kit");

        List<String> FashionAccessories = new ArrayList<String>();
        FashionAccessories.add("Belt");
        FashionAccessories.add("Gents Wallet");
        FashionAccessories.add("Ladies Purse");
        FashionAccessories.add("Sunglasses");
        FashionAccessories.add("Combo");
        FashionAccessories.add("Jewellery");




        expandableListDetail.put("Home Accessories", HomeAccessories);
        expandableListDetail.put("Fashion Accessories", FashionAccessories);
        expandableListDetail.put("Stationary", Stationary);
        expandableListDetail.put("Furniture", Furniture);
        expandableListDetail.put("Mans Fashion", MansFashion);
        expandableListDetail.put("Woman's Fashion",fashionGirls);


        return expandableListDetail;
    }
}
