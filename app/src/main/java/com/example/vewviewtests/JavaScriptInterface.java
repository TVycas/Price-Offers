package com.example.vewviewtests;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;

// An instance of this class will be registered as a JavaScript interface
public class JavaScriptInterface {

    private static final String TAG = JavaScriptInterface.class.getName();
    private ArrayList<Offer> offers = new ArrayList<>();

    @JavascriptInterface
    public void scrapeMaximaHTML(String html) {
        Log.d(TAG, String.valueOf(html.length()));

        MaximaScraper sc = new MaximaScraper(html);
        offers = sc.scrapeOffers();
    }

    ArrayList<Offer> getOffers() {
        return offers;
    }

}
