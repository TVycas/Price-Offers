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
            case 3:
                Log.d(TAG, "filterOffers: IkiOffers, id = " + filterSelection);
                return offerDao.getAllIkiOffers();
            case 4:
                Log.d(TAG, "filterOffers: MaximaOffers, id = " + filterSelection);
                return offerDao.getAllMaximaOffers();
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

    void refreshDatabase() {
        deleteAllAndStartScraping();
    }

    private void deleteAllAndStartScraping() {
        new deleteAllOffersAndScrapeAsyncTask(offerDao, new TaskDelegate() {
            @Override
            public void taskCompleted() {
                // Start scraping after the database is deleted
                webScraper.startScraping();
            }

            @Override
            public void taskCompleted(List<Offer> offers) {
            }
        }).execute();
    }


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

    // A delegate interface for notifying the main thread that the async tasks are completed
    public interface TaskDelegate {
        void taskCompleted();

        void taskCompleted(List<Offer> offers);
    }

    private static class updateOfferAsyncTask extends AsyncTask<Offer, Void, Void> {
        private OfferDao asyncTaskDao;

        updateOfferAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(Offer... offers) {
            asyncTaskDao.update(offers[0]);
            return null;
        }
    }

    private static class deleteAllOffersAndScrapeAsyncTask extends AsyncTask<Void, Void, Void> {
        private OfferDao asyncTaskDao;
        private TaskDelegate delegate;

        deleteAllOffersAndScrapeAsyncTask(OfferDao offerDao, TaskDelegate delegate) {
            this.delegate = delegate;
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncTaskDao.deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Notify the repository that the database is empty
            delegate.taskCompleted();
            super.onPostExecute(aVoid);
        }
    }
}
