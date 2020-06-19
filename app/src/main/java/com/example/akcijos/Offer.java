package com.example.akcijos;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offer_table")
public class Offer {

    @NonNull
    @PrimaryKey
    private final String TITLE;

    private final int PERCENTAGE;

    private final double PRICE;

    private final String IMG;

    private final String SHOP_NAME;

    public Offer(String TITLE, int PERCENTAGE, double PRICE, String IMG, String SHOP_NAME) {
        this.TITLE = TITLE;
        this.PERCENTAGE = PERCENTAGE;
        this.PRICE = PRICE;
        this.IMG = IMG;
        this.SHOP_NAME = SHOP_NAME;
    }

    @Override
    public String toString() {
        return "title = " + TITLE
                + "\nper = " + PERCENTAGE
                + "\nprice = " + PRICE
                + "\nimg = " + IMG;
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
}
