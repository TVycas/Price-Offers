package com.example.priceoffers.scrapers;

import android.app.Application;
import android.util.Log;

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

    private final OffersRepository repo;

    private ArrayList<Offer> allOffers = new ArrayList<>();

    private boolean[] finishedIkiScrapers = new boolean[2];
    private boolean maximaScrapeFinished;
    private boolean ikiScrapeFinished;
    private boolean lidlScrapeFinished;

    public WebScrapingControl(Application application, OffersRepository repo) {
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
     * We would only like to call the database when the scraping from every shop is finished.
     * For this we use this helper method which checks whether scraping from all shops has finished.
     */
    private void tryToInsertOffers() {
        Log.d(TAG, "tryToInsertOffers: trying to insert offers. Maxima finished = " + maximaScrapeFinished +
                "; iki finished = " + ikiScrapeFinished + "; Lidl finished = " + lidlScrapeFinished);
        if (maximaScrapeFinished && ikiScrapeFinished && lidlScrapeFinished) {
            repo.insertAll(allOffers);
        }
    }

    /**
     * A method for initializing the scraping off every shop
     */
    public void startScraping() {
        // Create the three delegates for each of the shops to use in the async scrapers
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

        OffersRepository.TaskDelegate maximaDelegate = new OffersRepository.TaskDelegate() {
            @Override
            public void taskCompleted() { /* no-op */ }

            @Override
            public void taskCompleted(List<Offer> maximaOffers) {
                allOffers.addAll(maximaOffers);

                maximaScrapeFinished = true;
                tryToInsertOffers();
            }
        };

        maximaScrapeFinished = false;
        ikiScrapeFinished = false;
        lidlScrapeFinished = false;


        // Scrape both Iki Urls
        new AsyncScrapeTask(ikiDelegate, new IkiScraper(IKI_WEEKLY_URL)).execute();
        new AsyncScrapeTask(ikiDelegate, new IkiScraper(IKI_MONTHLY_URL)).execute();

        // Scrape Lidl
        new AsyncScrapeTask(lidlDelegate, new LidlScraper(LIDL_URL)).execute();

        // Scrape Maxima
        new AsyncScrapeTask(maximaDelegate, new MaximaScraper(MAXIMA_URL)).execute();
    }
}
