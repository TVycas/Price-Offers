package com.example.akcijos.scrapers;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.akcijos.database.Offer;

import java.util.ArrayList;

/**
 * An instance of this class will be registered as a JavaScript interface.
 */
public class JavaScriptInterface {

    private ArrayList<Offer> maximaOffers = new ArrayList<>();

    /**
     * The method to start Maxima scraping. This method will be called from a separate WebView thread.
     *
     * @param html The fully loaded maxima html
     */
    @JavascriptInterface
    public void scrapeMaximaHTML(String html) {
        Scraper scraper = new MaximaScraper(html);
        Log.d("JavaScriptInterface", "scrapeMaximaHTML: " + scraper.getShopName() + " scraping started");
        maximaOffers = scraper.scrapeOffers();
        Log.d("JavaScriptInterface", "scrapeMaximaHTML: " + scraper.getShopName() + " scraping finished, loaded " + maximaOffers.size() + " offers.");
    }

    ArrayList<Offer> getMaximaOffers() {
        return maximaOffers;
    }

}
