package com.example.vewviewtests;

import android.app.Application;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class WebScraper {

    private static final String TAG = WebScraper.class.getName();
    private final Application application;
    private final OffersRepository repo;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private ArrayList<Offer> offers = new ArrayList<>();

    public WebScraper(Application application, OffersRepository repo) {
        this.application = application;
        this.repo = repo;
    }

    public void startScrapingMaxima() {
        final WebView wv = new WebView(application);
        wv.addJavascriptInterface(javaScriptInterface, "ANDROID");

        wv.getSettings().setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient() {
            int timesLoaded = 0;

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO timesLoaded needs to be set programmatically
                if (timesLoaded < 5) {
                    wv.evaluateJavascript("javascript:document.getElementsByClassName('btn grey')[0].click();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d(TAG, "Loading offer html");
                                }
                            });

                    timesLoaded++;
                } else {
                    wv.evaluateJavascript("javascript:window.ANDROID.scrapeMaximaHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.d(TAG, "Maxima scraping finished");
                                    // Scraping finished so we can get the offers
                                    offers.addAll(javaScriptInterface.getOffers());
                                    repo.insert(offers);
                                }
                            });
                }
            }
        });

        Log.d(TAG, "Scraping starting");
        wv.loadUrl("https://www.maxima.lt/akcijos");

    }
}
