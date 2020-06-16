package com.example.vewviewtests;

public class Offer {
    private final String title;
    private final int percentage;
    private final double price;
    private final String img;

    public Offer(String title, int percentage, double price, String img) {
        this.title = title;
        this.percentage = percentage;
        this.price = price;
        this.img = img;
    }

    @Override
    public String toString() {
        return "title = " + title
                + "\nper = " + percentage
                + "\nprice = " + price
                + "\nimg = " + img;
    }
}
