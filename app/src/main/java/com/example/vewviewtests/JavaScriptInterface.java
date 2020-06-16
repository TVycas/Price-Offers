package com.example.vewviewtests;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;

// An instance of this class will be registered as a JavaScript interface
public class JavaScriptInterface {

    private final String TAG = "JavaScriptInterface";
    private ArrayList<Offer> offers = new ArrayList<>();

    @JavascriptInterface
    public void scrapeHTML(String html) {
        Log.d(TAG, String.valueOf(html.length()));

        Scraper sc = new Scraper(html);
        offers = sc.scrapeOffers();
    }

    ArrayList<Offer> getOffers() {
        return offers;
    }

}
