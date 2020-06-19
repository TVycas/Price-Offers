package com.example.akcijos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private OffersRepository repo;
    private LiveData<List<Offer>> allOffers;
    private LiveData<List<Offer>> selectedOffers;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new OffersRepository(application);
        allOffers = repo.getAllOffers();
        selectedOffers = repo.getSelectedOffers();
    }

    public void updateOffer(Offer newOffer) {
        repo.update(newOffer);
    }


    public void initScraping() {
        repo.startScraping();
    }

    public LiveData<List<Offer>> getAllOffers() {
        return allOffers;
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }
}
