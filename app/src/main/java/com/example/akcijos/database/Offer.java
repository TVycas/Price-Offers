package com.example.akcijos.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "offer_table")
public class Offer {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String TITLE;

    private final int PERCENTAGE;

    private final double PRICE;

    private final String IMG;

    private final String SHOP_NAME;

    private boolean isSelected;

    public Offer(String TITLE, int PERCENTAGE, double PRICE, String IMG, String SHOP_NAME) {
        this.TITLE = TITLE;
        this.PERCENTAGE = PERCENTAGE;
        this.PRICE = PRICE;
        this.IMG = IMG;
        this.SHOP_NAME = SHOP_NAME;
        this.isSelected = false;
    }

    @Ignore
    public Offer(int id, String TITLE, int PERCENTAGE, double PRICE, String IMG, String SHOP_NAME) {
        this.id = id;
        this.TITLE = TITLE;
        this.PERCENTAGE = PERCENTAGE;
        this.PRICE = PRICE;
        this.IMG = IMG;
        this.SHOP_NAME = SHOP_NAME;
        this.isSelected = false;
    }

    @Override
    public String toString() {
        return "title = " + TITLE
                + "\nper = " + PERCENTAGE
                + "\nprice = " + PRICE
                + "\nimg = " + IMG
                + "\nshop = " + SHOP_NAME
                + "\nisSelected = " + isSelected;
    }

    public String getTITLE() {
        return TITLE;
    }

    public double getPRICE() {
        return PRICE;
    }

    public int getPERCENTAGE() {
        return PERCENTAGE;
    }

    public String getIMG() {
        return IMG;
    }

    public String getSHOP_NAME() {
        return SHOP_NAME;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
