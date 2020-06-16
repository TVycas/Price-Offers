package com.example.vewviewtests;

import android.app.Application;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class OffersRepository {

    private final String TAG = "OffersRepository";
    private Application appContext;
    private JavaScriptInterface javaScriptInterface;
    private ArrayList<Offer> offers;

    public OffersRepository(Application context) {
        this.appContext = context;
    }

    public void startScraping() {
        final WebView wv = new WebView(appContext);
        javaScriptInterface = new JavaScriptInterface();
        wv.addJavascriptInterface(javaScriptInterface, "ANDROID");

        wv.getSettings().setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient() {
            int timesLoaded = 0;

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO times loaded needs to be set programmatically
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
                    wv.evaluateJavascript("javascript:window.ANDROID.scrapeHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            // Scraping finished so we can get the offers
                            offers = javaScriptInterface.getOffers();
                        }
                    });
                }
            }
        });

        wv.loadUrl("https://www.maxima.lt/akcijos");
    }
}
