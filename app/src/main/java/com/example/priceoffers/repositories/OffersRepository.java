package com.example.priceoffers.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.priceoffers.database.Offer;
import com.example.priceoffers.database.OfferDao;
import com.example.priceoffers.database.OfferRoomDatabase;
import com.example.priceoffers.scrapers.WebScrapingControl;

import java.util.List;

/**
 * A repository for getting and manipulation the list of offers shown to the user
 */
public class OffersRepository {

    private static final String TAG = OffersRepository.class.getName();
    private WebScrapingControl webScrapingControl;
    private OfferDao offerDao;
    private LiveData<List<Offer>> selectedOffers;

    public OffersRepository(Application application) {
        // Set up the database
        OfferRoomDatabase db = OfferRoomDatabase.getDatabase(application);
        offerDao = db.offerDao();

        // Get the list of selected offers
        selectedOffers = offerDao.getSelectedOffers();
        // Set up the web scraper
        webScrapingControl = new WebScrapingControl(application, this);
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }

    /**
     * Filters and returns the list off offers based on the filter id
     *
     * @param filterSelection The id of the filter to be used
     * @return A list of offers encapsulated in a LiveData object
     */
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
            case 5:
                Log.d(TAG, "filterOffers: LidlOffers, id = " + filterSelection);
                return offerDao.getAllLidlOffers();
            default:
                return offerDao.getAllOffersAlphabetic();
        }
    }

    public void insertAll(List<Offer> offers) {
        new insertAllAsyncTask(offerDao).execute(offers);
    }

    public void update(Offer offer) {
        new updateOfferAsyncTask(offerDao).execute(offer);
    }

    public void refreshDatabase() {
        deleteAllAndStartScraping();
    }

    /**
     * Deletes the database and starts a new scraping process to refresh the database
     */
    private void deleteAllAndStartScraping() {
        new deleteAllOffersAndScrapeAsyncTask(offerDao, new TaskDelegate() {
            @Override
            public void taskCompleted() {
                // Start scraping after the database is deleted
                webScrapingControl.startScraping();
            }

            @Override
            public void taskCompleted(List<Offer> offers) {/* no-op */}
        }).execute();
    }


    // Do database operations asynchronously

    /**
     * A delegate interface for notifying the main thread that the async tasks are completed
     */
    public interface TaskDelegate {
        void taskCompleted();

        void taskCompleted(List<Offer> offers);
    }

    private static class insertAllAsyncTask extends AsyncTask<List<Offer>, Void, Void> {
        private OfferDao asyncTaskDao;

        insertAllAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Offer>... lists) {
            // Insert the offers to the database
            asyncTaskDao.insertAll(lists[0]);
            return null;
        }
    }

    private static class updateOfferAsyncTask extends AsyncTask<Offer, Void, Void> {

        private OfferDao asyncTaskDao;

        updateOfferAsyncTask(OfferDao offerDao) {
            asyncTaskDao = offerDao;
        }

        @Override
        protected Void doInBackground(Offer... offers) {
            // Update the information of the offer with the object given
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
