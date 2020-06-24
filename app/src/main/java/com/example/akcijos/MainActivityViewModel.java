package com.example.akcijos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.akcijos.database.Offer;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private OffersRepository repo;
    private LiveData<List<Offer>> selectedOffers;
    private MutableLiveData<Integer> filterID = new MutableLiveData<>(0);

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new OffersRepository(application);
        selectedOffers = repo.getSelectedOffers();
    }

    public void updateOffer(Offer newOffer) {
        repo.update(newOffer);
    }

    public void refreshDatabase() {
        repo.refreshDatabase();
    }

    public LiveData<List<Offer>> getAllOffers() {
        return Transformations.switchMap(filterID, filterID -> repo.filterOffers(filterID));
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }

    public void filterOffers(int filterSelection) {
        filterID.setValue(filterSelection);
    }
}
