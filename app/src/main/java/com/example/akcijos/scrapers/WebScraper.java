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
import com.example.akcijos.R;
import com.example.akcijos.database.Offer;

import java.util.ArrayList;
import java.util.List;

public class WebScraper {

    private static final String TAG = WebScraper.class.getName();
    private final String MAXIMA_URL;
    private final String IKI_MONTHLY_URL;
    private final String IKI_WEEKLY_URL;
    private final String LIDL_URL;
    private final Application application;
    private final OffersRepository repo;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private ArrayList<Offer> offers = new ArrayList<>();
    private boolean[] finishedIkiScrapers = new boolean[2];
    private boolean maximaScrapeFinished;
    private boolean ikiScrapeFinished;
    private boolean lidlScrapeFinished;

    public WebScraper(Application application, OffersRepository repo) {
        this.application = application;
        this.repo = repo;

        // Read url constants
        this.IKI_WEEKLY_URL = application.getString(R.string.iki_weekly_url);
        this.IKI_MONTHLY_URL = application.getString(R.string.iki_monthly_url);
        this.MAXIMA_URL = application.getString(R.string.maxima_url);
        this.LIDL_URL = application.getString(R.string.lidl_url);
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
                            Log.d(TAG, "onReceiveValue: loads to make for Maxima " + loadsToMake);
                        }
                    });
                }

                if (timesLoaded < loadsToMake) {
                    wv.evaluateJavascript("javascript:document.getElementsByClassName('btn grey')[0].click();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d(TAG, "Maxima: loading additional offer html (" + timesLoaded + "/" + loadsToMake + ")");
                                }
                            });

                    timesLoaded++;
                } else {
                    wv.evaluateJavascript("javascript:window.ANDROID.scrapeMaximaHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
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
        wv.loadUrl(MAXIMA_URL);

    }

    private void tryToInsertOffers() {
        Log.d(TAG, "tryToInsertOffers: trying to insert offers. Maxima finished = " + maximaScrapeFinished + "; iki finished = " + ikiScrapeFinished);
        if (maximaScrapeFinished && ikiScrapeFinished && lidlScrapeFinished) {
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
        OffersRepository.TaskDelegate ikiDelegate = new OffersRepository.TaskDelegate() {
            @Override
            public void taskCompleted() {
            }

            @Override
            public void taskCompleted(List<Offer> ikiOffers) {
                offers.addAll(ikiOffers);

                // Check if we need to insert the offers.
                if (checkOtherIkiScraperFinished()) {
                    ikiScrapeFinished = true;
                    tryToInsertOffers();
                }
            }
        };

        OffersRepository.TaskDelegate lidlDelegate = new OffersRepository.TaskDelegate() {
            @Override
            public void taskCompleted() {
            }

            @Override
            public void taskCompleted(List<Offer> lidlOffers) {
                offers.addAll(lidlOffers);

                // Check if we need to insert the offers.
                if (checkOtherIkiScraperFinished()) {
                    lidlScrapeFinished = true;
                    tryToInsertOffers();
                }
            }
        };

        maximaScrapeFinished = false;
        ikiScrapeFinished = false;
        lidlScrapeFinished = false;

        // No need to run any JavaScript for Iki and Lidl scraping so we can use Jsoup for connection instead of WebViews.
        new IkiScraper(ikiDelegate).execute(IKI_WEEKLY_URL);
        new IkiScraper(ikiDelegate).execute(IKI_MONTHLY_URL);
        new LidlScraper(lidlDelegate).execute(LIDL_URL);

        startScrapingMaxima();
    }
}
