package com.example.vewviewtests;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

public class OffersRepository {

    private static final String TAG = OffersRepository.class.getName();
    private Application application;
    private WebScraper webScraper;
    private ArrayList<Offer> offers;

    public OffersRepository(Application application) {
        this.application = application;
        webScraper = new WebScraper(application, this);
        webScraper.startScrapingMaxima();
    }


    public void startScraping() {
        webScraper.startScrapingMaxima();
    }

    //TODO async
    public void updateDB(ArrayList<Offer> offers) {
        Log.d(TAG, "Updating Database");
    }
}
