package com.example.akcijos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.akcijos.database.Offer;
import com.example.akcijos.repositories.OffersRepository;

import java.util.List;

/**
 * A view model responsible for providing the information on the offers
 */
public class OffersViewModel extends AndroidViewModel {

    private OffersRepository repo;
    private LiveData<List<Offer>> selectedOffers;
    private MutableLiveData<Integer> filterID = new MutableLiveData<>(0);

    public OffersViewModel(@NonNull Application application) {
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

    /**
     * Transform the LiveData for the all offers list  to be based on the filter id selected by the user
     *
     * @return An offer list encapsulated in LiveDate object
     */
    public LiveData<List<Offer>> getAllOffers() {
        return Transformations.switchMap(filterID, filterID -> repo.filterOffers(filterID));
    }

    /**
     * Update the filterID LiveData
     * @param filterSelection The index of the filter value the user selected
     */
    public void filterOffers(int filterSelection) {
        filterID.setValue(filterSelection);
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }
}
