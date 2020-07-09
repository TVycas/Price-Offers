package com.example.akcijos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.akcijos.R;
import com.example.akcijos.database.Offer;
import com.example.akcijos.repositories.OffersRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A view model responsible for providing the information on the offers
 */
public class OffersViewModel extends AndroidViewModel {

    private Application application;
    private OffersRepository repo;
    private LiveData<List<Offer>> selectedOffers;
    private LiveData<String> userSelectionInfo;
    private MutableLiveData<Integer> filterID = new MutableLiveData<>(0);

    public OffersViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repo = new OffersRepository(application);

        selectedOffers = repo.getSelectedOffers();
        userSelectionInfo = Transformations.map(selectedOffers, offers -> constructSelectionInfoString(offers));
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
     *
     * @param filterSelection The index of the filter value the user selected
     */
    public void filterOffers(int filterSelection) {
        filterID.setValue(filterSelection);
    }

    public LiveData<List<Offer>> getSelectedOffers() {
        return selectedOffers;
    }

    /**
     * Creates a string to describe the user selection information. Tells how many items were selected from each shop.
     *
     * @param offers List of selected offers
     * @return String describing the user selection information.
     */
    private String constructSelectionInfoString(List<Offer> offers) {
        Map<String, Integer> offerShopsCount = getShopCountMap(offers);
        StringBuilder selectionInfo;

        if (offerShopsCount.keySet().size() != 0) {
            selectionInfo = new StringBuilder(application.getString(R.string.shop_selection_start));
            for (String shopName : offerShopsCount.keySet()) {
                selectionInfo.append(shopName)
                        .append(" (")
                        .append(offerShopsCount.get(shopName))
                        .append("), ");
            }
            selectionInfo = new StringBuilder(selectionInfo.substring(0, selectionInfo.length() - 2) + ".");
        } else {
            selectionInfo = new StringBuilder(application.getString(R.string.shop_selection_no_shops));
        }
        return selectionInfo.toString();
    }

    /**
     * A helper method to create a < Shop name, Offers from that shop > map
     *
     * @param offers List of selected offers
     * @return Map describing the user selections
     */
    private Map<String, Integer> getShopCountMap(List<Offer> offers) {
        Map<String, Integer> offerShopsCount = new HashMap<>();

        for (Offer offer : offers) {
            String shopName = offer.getSHOP_NAME();
            if (!offerShopsCount.containsKey(shopName)) {
                offerShopsCount.put(shopName, 1);
            } else {
                offerShopsCount.put(shopName, offerShopsCount.get(shopName) + 1);
            }
        }
        return offerShopsCount;
    }

    public LiveData<String> getUserSelectionInfo() {
        return userSelectionInfo;
    }
}
