package com.example.priceoffers.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offer_table")
public class Offer {

    /**
     * The percentage of discount of the offer
     */
    private final int PERCENTAGE;

    @NonNull
    private final String TITLE;
    /**
     * A String containing a link to the image file
     */
    private final String IMG;

    private final double PRICE;
    /**
     * We use integer ids as primary keys because some offers from different shops could have the same name
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String SHOP_NAME;
    /**
     * A variable storing whether or not the this offer is selected by the user
     */
    private boolean isSelected;

    public Offer(@NonNull String TITLE, int PERCENTAGE, double PRICE, String IMG, String SHOP_NAME) {
        this.TITLE = TITLE;
        this.PERCENTAGE = PERCENTAGE;
        this.PRICE = PRICE;
        this.IMG = IMG;
        this.SHOP_NAME = SHOP_NAME;
        this.isSelected = false;
    }

    @NonNull
    @Override
    public String toString() {
        return "title = " + TITLE
                + "\nper = " + PERCENTAGE
                + "\nprice = " + PRICE
                + "\nimg = " + IMG
                + "\nshop = " + SHOP_NAME
                + "\nisSelected = " + isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
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

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
