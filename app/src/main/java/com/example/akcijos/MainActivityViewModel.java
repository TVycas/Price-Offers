package com.example.akcijos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private OffersRepository repo;
    private LiveData<List<Offer>> allOffers;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new OffersRepository(application);
        allOffers = repo.getAllOffers();
    }


    public void initScraping() {
        repo.startScraping();
    }

    public LiveData<List<Offer>> getAllOffers() {
        return allOffers;
    }
}
