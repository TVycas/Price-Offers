package com.example.vewviewtests;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class OffersRepository {

    private static final String TAG = OffersRepository.class.getName();
    //TODO do we need an application global variable?
//    private Application application;
    private WebScraper webScraper;
    private OfferDao offerDao;
    private LiveData<List<Offer>> allOffers;

    public OffersRepository(Application application) {
//        this.application = application;

        OfferRoomDatabase db = OfferRoomDatabase.getDatabase(application);
        offerDao = db.offerDao();
        allOffers = offerDao.getAllOffers();

        webScraper = new WebScraper(application, this);
        webScraper.startScrapingMaxima();
    }

    LiveData<List<Offer>> getAllOffers() {
        return allOffers;
    }


    public void startScraping() {
        webScraper.startScrapingMaxima();
    }

    public void insert(List<Offer> offers) {
        new insertAsyncTask(offerDao).execute(offers);
    }

//    public void update(Offer offer)  {
//        new updateOfferAsyncTask(offerDao).execute(offer);
//    }

//    public void deleteAll()  {
//        new deleteAllOffersAsyncTask(offerDao).execute();
//    }

    // Must run off main thread
//    public void deleteWord(Offer offer) {
//        new deleteOfferAsyncTask(offerDao).execute(offer);
//    }
    //TODO async
    public void updateDB(ArrayList<Offer> offers) {
        Log.d(TAG, "Updating Database");
    }

    private class insertAsyncTask extends AsyncTask<List<Offer>, Void, Void> {
        private OfferDao asyncTaskDao;

        public insertAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(List<Offer>... lists) {
            for (Offer offer : lists[0]) {
                asyncTaskDao.insert(offer);
            }
            return null;
        }
    }
}
