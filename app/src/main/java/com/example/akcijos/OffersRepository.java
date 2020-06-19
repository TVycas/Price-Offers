package com.example.akcijos;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.akcijos.database.Offer;
import com.example.akcijos.database.OfferDao;
import com.example.akcijos.database.OfferRoomDatabase;
import com.example.akcijos.scrapers.WebScraper;

import java.util.List;

public class OffersRepository {

    private static final String TAG = OffersRepository.class.getName();
    private WebScraper webScraper;
    private OfferDao offerDao;
    private LiveData<List<Offer>> allOffers;
    private LiveData<List<Offer>> selectedOffers;

    public OffersRepository(Application application) {
        OfferRoomDatabase db = OfferRoomDatabase.getDatabase(application);
        offerDao = db.offerDao();

        allOffers = offerDao.getAllOffers();
        selectedOffers = offerDao.getSelectedOffers();
        webScraper = new WebScraper(application, this);
    }

    LiveData<List<Offer>> getAllOffers() {
        return allOffers;
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }


    public void startScraping() {
        webScraper.startScrapingMaxima();
    }

    public void insert(List<Offer> offers) {
        new insertAsyncTask(offerDao).execute(offers);
    }

    public void update(Offer offer) {
        new updateOfferAsyncTask(offerDao).execute(offer);
    }

//    public void deleteAll()  {
//        new deleteAllOffersAsyncTask(offerDao).execute();
//    }

    // Must run off main thread
//    public void deleteWord(Offer offer) {
//        new deleteOfferAsyncTask(offerDao).execute(offer);
//    }

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

    private class updateOfferAsyncTask extends AsyncTask<Offer, Void, Void> {
        private OfferDao asyncTaskDao;

        public updateOfferAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(Offer... offers) {
            int rowsUpdated = asyncTaskDao.update(offers[0]);
            Log.d(TAG, "doInBackground: Rows updated = " + rowsUpdated);
            return null;
        }
    }
}
