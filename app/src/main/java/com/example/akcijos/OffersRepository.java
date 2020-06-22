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
    private LiveData<List<Offer>> selectedOffers;

    OffersRepository(Application application) {
        OfferRoomDatabase db = OfferRoomDatabase.getDatabase(application);
        offerDao = db.offerDao();

        selectedOffers = offerDao.getSelectedOffers();
        webScraper = new WebScraper(application, this);
    }

    LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }

    void startScraping() {
        webScraper.startScraping();
    }

    public LiveData<List<Offer>> filterOffers(int filterSelection) {
        switch (filterSelection) {
            case 0:
                Log.d(TAG, "filterOffers: Alphabetic, id = " + filterSelection);
                return offerDao.getAllOffersAlphabetic();
            case 1:
                Log.d(TAG, "filterOffers: ByDiscountHighToLow, id = " + filterSelection);
                return offerDao.getAllOffersByDiscountHighToLow();
            case 2:
                Log.d(TAG, "filterOffers: ByDiscountLowToHigh, id = " + filterSelection);
                return offerDao.getAllOffersByDiscountLowToHigh();
            default:
                return offerDao.getAllOffersAlphabetic();
        }
    }

    public void insert(List<Offer> offers) {
        new insertAsyncTask(offerDao).execute(offers);
    }

    void update(Offer offer) {
        new updateOfferAsyncTask(offerDao).execute(offer);
    }

//    public void deleteAll()  {
//        new deleteAllOffersAsyncTask(offerDao).execute();
//    }

    // Must run off main thread
//    public void deleteWord(Offer offer) {
//        new deleteOfferAsyncTask(offerDao).execute(offer);
//    }


    // Do database operations asynchronously
    private static class insertAsyncTask extends AsyncTask<List<Offer>, Void, Void> {
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

    private static class updateOfferAsyncTask extends AsyncTask<Offer, Void, Void> {
        private OfferDao asyncTaskDao;

        public updateOfferAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(Offer... offers) {
            asyncTaskDao.update(offers[0]);
            return null;
        }
    }
}
