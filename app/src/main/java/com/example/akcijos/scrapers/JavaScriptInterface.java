package com.example.akcijos.scrapers;

import android.webkit.JavascriptInterface;

import com.example.akcijos.database.Offer;

import java.util.ArrayList;

// An instance of this class will be registered as a JavaScript interface
public class JavaScriptInterface {

    private ArrayList<Offer> maximaOffers = new ArrayList<>();

    @JavascriptInterface
    public void scrapeMaximaHTML(String html) {
        MaximaScraper sc = new MaximaScraper(html);
        maximaOffers = sc.scrapeOffers();
    }

    ArrayList<Offer> getMaximaOffers() {
        return maximaOffers;
    }

}
