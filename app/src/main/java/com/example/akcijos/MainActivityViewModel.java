package com.example.akcijos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.akcijos.database.Offer;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = MainActivityViewModel.class.getName();
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
        return Transformations.switchMap(filterID, new Function<Integer, LiveData<List<Offer>>>() {
            @Override
            public LiveData<List<Offer>> apply(Integer filterID) {
                return repo.filterOffers(filterID);
            }
        });
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }

    public void filterOffers(int filterSelection) {
        filterID.setValue(filterSelection);
    }
}
