package com.example.priceoffers.scrapers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.priceoffers.database.Offer;
import com.example.priceoffers.repositories.OffersRepository;

import java.util.ArrayList;
import java.util.List;

public class AsyncScrapeTask extends AsyncTask<Void, Void, List<Offer>> {

    private static final String TAG = AsyncScrapeTask.class.getName();
    private OffersRepository.TaskDelegate delegate;
    private Scraper scraper;

    AsyncScrapeTask(OffersRepository.TaskDelegate delegate, Scraper scraper) {
        this.delegate = delegate;
        this.scraper = scraper;
    }

    @Override
    protected List<Offer> doInBackground(Void... voids) {
        Log.d(TAG, scraper.getShopName() + " scraping started");

        ArrayList<Offer> offers = scraper.scrapeOffers();

        Log.i(TAG, "doInBackground: " + scraper.getShopName() + " scraping finished, loaded " + offers.size() + " offers.");

        return offers;
    }

    @Override
    protected void onPostExecute(List<Offer> offers) {
        delegate.taskCompleted(offers);
        super.onPostExecute(offers);
    }
}
