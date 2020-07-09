package com.example.priceoffers.scrapers;

import android.app.Application;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.example.priceoffers.R;
import com.example.priceoffers.database.Offer;
import com.example.priceoffers.repositories.OffersRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for issuing web scraping tasks to acquire offer information
 */
public class WebScrapingControl {

    private static final String TAG = WebScrapingControl.class.getName();

    private final String MAXIMA_URL;
    private final String IKI_MONTHLY_URL;
    private final String IKI_WEEKLY_URL;
    private final String LIDL_URL;

    private final Application application;
    private final OffersRepository repo;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface();

    private ArrayList<Offer> allOffers = new ArrayList<>();

    private boolean[] finishedIkiScrapers = new boolean[2];
    private boolean maximaScrapeFinished;
    private boolean ikiScrapeFinished;
    private boolean lidlScrapeFinished;

    public WebScrapingControl(Application application, OffersRepository repo) {
        this.application = application;
        this.repo = repo;

        // Read url constants
        this.IKI_WEEKLY_URL = application.getString(R.string.iki_weekly_url);
        this.IKI_MONTHLY_URL = application.getString(R.string.iki_monthly_url);
        this.MAXIMA_URL = application.getString(R.string.maxima_url);
        this.LIDL_URL = application.getString(R.string.lidl_url);
    }

    /**
     * Iki offers are divided between multiple pages and so we need to do the same scraping twice and the scraping method needs to know if the entire Iki scraping is finished or not.
     * For this, we use a boolean array and when one of the asynchronous scraping methods finishes, it changes one of the values to true. When all of the values in the array
     * are true, the entire Iki scraping is finished.
     *
     * @return true if all of the async Iki scrapers have finished
     */
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

    /**
     * Maxima scraping requires javascript as several button clicks are needed to load all available offers.
     * For this, a headless WebView and a javascript interface is used to simulate button clicks and acquire the final html for scraping.
     */
    private void startScrapingMaxima() {
        final WebView wv = getWebView();

        wv.setWebViewClient(new WebViewClient() {
            int timesLoaded = 0;
            int loadsToMake = 5;
            boolean calculateLoadsToMake = true;

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // For faster loading, intercept unnecessary uri request and replace them with empty ones
                String uri = request.getUrl().toString();
                if (uri.contains("css") || uri.contains("ico") || uri.contains("facebook") || uri.contains("google")) {
                    return new WebResourceResponse("text/javascript", "UTF-8", null);
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Calculate how many button clicks are needed to load the entire offers page for Maxima.
                // As onPageFinished will be called multiple times, we need a boolean to only calculate the number of
                // loads once
                if (calculateLoadsToMake) {
                    wv.evaluateJavascript("javascript:document.getElementById('items_cnt').textContent;", value -> {
                        value = value.replace("\"", "");
                        int itemsToLoad = Integer.parseInt(value);

                        // 45 is the number of offers loaded in a single step (button click)
                        loadsToMake = (itemsToLoad / 45) + 1;
                        // We only need to get this number once
                        calculateLoadsToMake = false;
                        Log.d(TAG, "onReceiveValue: loads to make for Maxima " + loadsToMake);
                    });
                }

                // Keep pressing the "Load more offers" button until all of the offers are loaded
                if (timesLoaded < loadsToMake) {
                    wv.evaluateJavascript("javascript:document.getElementsByClassName('btn grey')[0].click();",
                            s -> Log.d(TAG, "Maxima: loading additional offer html (" + timesLoaded + "/" + loadsToMake + ")"));

                    timesLoaded++;
                } else {
                    // Once all of the offers are loaded, use the javascript-android interface to get the final html value and start scraping
                    wv.evaluateJavascript("javascript:window.ANDROID.scrapeMaximaHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');",
                            value -> {
                                // Scraping finished so we can add the offers to the local offers list
                                // No race conditions can happen because this code is executed on the main thread
                                allOffers.addAll(javaScriptInterface.getMaximaOffers());
                                maximaScrapeFinished = true;
                                tryToInsertOffers();
                            });
                }
            }
        });

        Log.d(TAG, "Maxima scraping starting");
        wv.loadUrl(MAXIMA_URL);
    }

    /**
     * We would only like to call the database when the scraping from every shop is finished.
     * For this we use this helper method which checks whether scraping from all shops has finished.
     */
    private void tryToInsertOffers() {
        Log.d(TAG, "tryToInsertOffers: trying to insert offers. Maxima finished = " + maximaScrapeFinished +
                "; iki finished = " + ikiScrapeFinished + "; Lidl finished = " + lidlScrapeFinished);
        if (maximaScrapeFinished && ikiScrapeFinished && lidlScrapeFinished) {
            repo.insert(allOffers);
        }
    }

    /**
     * A helper method to set up the headless WebView used to load the web page. It adds a javaScript-Android interface, enables javaScript, and
     * disables image loading
     *
     * @return A WebView for offer scraping
     */
    private WebView getWebView() {
        final WebView wv = new WebView(application);

        wv.addJavascriptInterface(javaScriptInterface, "ANDROID");

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(false);
        return wv;
    }

    /**
     * A method for initializing the scraping off every shop
     */
    public void startScraping() {
        // Create the two delegates for Iki and Lidl async scrapers
        OffersRepository.TaskDelegate ikiDelegate = new OffersRepository.TaskDelegate() {
            @Override
            public void taskCompleted() { /* no-op */ }

            @Override
            public void taskCompleted(List<Offer> ikiOffers) {
                allOffers.addAll(ikiOffers);

                // Check if we need to insert the offers.
                if (checkOtherIkiScraperFinished()) {
                    ikiScrapeFinished = true;
                    tryToInsertOffers();
                }
            }
        };

        OffersRepository.TaskDelegate lidlDelegate = new OffersRepository.TaskDelegate() {
            @Override
            public void taskCompleted() { /* no-op */ }

            @Override
            public void taskCompleted(List<Offer> lidlOffers) {
                allOffers.addAll(lidlOffers);

                lidlScrapeFinished = true;
                tryToInsertOffers();
            }
        };

        maximaScrapeFinished = false;
        ikiScrapeFinished = false;
        lidlScrapeFinished = false;

        // No need to run any JavaScript for Iki and Lidl scraping so we can use Jsoup for connection instead of WebViews.

        // Scrape both Iki Urls
        new AsyncScrapeTask(ikiDelegate, new IkiScraper(IKI_WEEKLY_URL)).execute();
        new AsyncScrapeTask(ikiDelegate, new IkiScraper(IKI_MONTHLY_URL)).execute();

        // Scrape Lidl
        new AsyncScrapeTask(lidlDelegate, new LidlScraper(LIDL_URL)).execute();

        // Scrape Maxima
        startScrapingMaxima();
    }
}
