package com.example.akcijos.scrapers;

import android.app.Application;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.example.akcijos.OffersRepository;
import com.example.akcijos.database.Offer;

import java.util.ArrayList;

public class WebScraper {

    private static final String TAG = WebScraper.class.getName();
    private final Application application;
    private final OffersRepository repo;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private ArrayList<Offer> offers = new ArrayList<>();
    private boolean[] finishedIkiScrapers = new boolean[2];
    private boolean maximaScrapeFinished;
    private boolean ikiScrapeFinished;

    public WebScraper(Application application, OffersRepository repo) {
        this.application = application;
        this.repo = repo;
    }

    private void startScrapingIki(String url) {
        final WebView wv = getWebView();
        ikiScrapeFinished = false;

        wv.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String uri = request.getUrl().toString();
                if (uri.contains("css") || uri.contains("ico") || uri.contains("facebook") || uri.contains("google") || uri.contains("iki_design")) {
                    return new WebResourceResponse("text/javascript", "UTF-8", null);
                }

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                wv.evaluateJavascript("javascript:window.ANDROID.scrapeIkiHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.d(TAG, "Iki scraping finished");
                                // Scraping finished so we can insert the offers to the database.
                                // No race conditions can happen because this code is executed on the main thread
                                offers.addAll(javaScriptInterface.getIkiOffers());

                                // Check if we need to insert the offers.
                                if (checkOtherIkiScraperFinished()) {
                                    ikiScrapeFinished = true;
                                    tryToInsertOffers();
                                }
                            }
                        });
            }
        });

        Log.d(TAG, "Iki scraping starting");
        wv.loadUrl(url);

    }

    // Iki offers are divided between multiple pages so we need to do the same scraping twice and the method needs to know if the entire Iki scraping is finished or not.
    // For this, we use a boolean array where when one of the asynchronous scraping methods finishes, it changes one of the values to true. When all of the values in the array
    // are true, the entire Iki scraping is finished.
    private boolean checkOtherIkiScraperFinished() {
        for (int i = 0; i < finishedIkiScrapers.length; i++) {
            if (!finishedIkiScrapers[i]) {
                finishedIkiScrapers[i] = true;

                // Return true if all of the elements in the array are now true
                return i == finishedIkiScrapers.length - 1;
            }
        }

        return true;
    }

    private void startScrapingMaxima() {
        final WebView wv = getWebView();
        maximaScrapeFinished = false;

        wv.setWebViewClient(new WebViewClient() {
            int timesLoaded = 0;
            int loadsToMake = 5;
            boolean calculateLoadsToMake = true;

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String uri = request.getUrl().toString();
                if (uri.contains("css") || uri.contains("ico") || uri.contains("facebook") || uri.contains("google")) {
                    return new WebResourceResponse("text/javascript", "UTF-8", null);
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Calculate how many button clicks is needed to load the entire offers page for Maxima
                if (calculateLoadsToMake) {
                    wv.evaluateJavascript("javascript:document.getElementById('items_cnt').textContent;", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            value = value.replace("\"", "");
                            int itemsToLoad = Integer.parseInt(value);

                            // 45 is the number of offers loaded in a single step (button click)
                            loadsToMake = (itemsToLoad / 45) + 1;
                            calculateLoadsToMake = false;
                            Log.d(TAG, "onReceiveValue: loads to make for Maxima" + loadsToMake);
                        }
                    });
                }

                if (timesLoaded < loadsToMake) {
                    wv.evaluateJavascript("javascript:document.getElementsByClassName('btn grey')[0].click();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d(TAG, "Maxima: loading additional offer html");
                                }
                            });

                    timesLoaded++;
                } else {
                    wv.evaluateJavascript("javascript:window.ANDROID.scrapeMaximaHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.d(TAG, "Maxima scraping finished");
                                    // Scraping finished so we can insert the offers to the database
                                    // No race conditions can happen because this code is executed on the main thread
                                    offers.addAll(javaScriptInterface.getMaximaOffers());
                                    maximaScrapeFinished = true;
                                    tryToInsertOffers();
                                }
                            });
                }
            }
        });

        Log.d(TAG, "Maxima scraping starting");
        wv.loadUrl("https://www.maxima.lt/akcijos");

    }

    private void tryToInsertOffers() {
        Log.d(TAG, "tryToInsertOffers: trying to insert offers. Maxima finished = " + maximaScrapeFinished + "; iki finished = " + ikiScrapeFinished);
        if (maximaScrapeFinished && ikiScrapeFinished) {
            repo.insert(offers);
        }
    }

    private WebView getWebView() {
        final WebView wv = new WebView(application);
        wv.addJavascriptInterface(javaScriptInterface, "ANDROID");

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(false);
        return wv;
    }

    public void startScraping() {
        startScrapingIki("https://iki.lt/akcija_tag/tag_sia-savaite/?ipage=9?ipage=9&itag=&per_page=1000");
        startScrapingIki("https://iki.lt/akcija_tag/tag_menesio-akcija/?ipage=48&itag=&per_page=1000");
        startScrapingMaxima();
    }
}
