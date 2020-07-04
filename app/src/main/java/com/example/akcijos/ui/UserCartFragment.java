package com.example.akcijos.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akcijos.R;
import com.example.akcijos.database.Offer;
import com.example.akcijos.viewmodels.OffersViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@link Fragment} subclass.
 */
public class UserCartFragment extends Fragment {

    private static final String TAG = UserCartFragment.class.getName();
    private OffersViewModel viewModel;

    public UserCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_cart, container, false);
    }

    //TODO add a filter option

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up the RecyclerView.
        RecyclerView recyclerView = getView().findViewById(R.id.user_cart_recyclerview);
        final OfferListAdapter cartListAdapter = new OfferListAdapter(getContext(), recyclerView);
        recyclerView.setAdapter(cartListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the offer object that was deselected by the user and update the database to mark the object as deselected
        cartListAdapter.setOnCheckedChangedListener((view, isChecked, position) -> {
            Offer offer = cartListAdapter.getOfferAtPosition(position);
            Log.d(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
            offer.setIsSelected(isChecked);
            viewModel.updateOffer(offer);
        });

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(OffersViewModel.class);
        // Update the cart as soon as the database changes.
        viewModel.getSelectedOffers().observe(this, offers -> {
            cartListAdapter.setDisplayedOffers(offers, true);
            setSelectionInfo(offers);
        });
    }

    /**
     * Set the bottom text view to display information about the user selection - how many offers were selected for each shop
     *
     * @param offers the list off all user selected offers
     */
    private void setSelectionInfo(List<Offer> offers) {
        TextView selectionInfoTextView = getView().findViewById(R.id.selection_info);

        Map<String, Integer> offerShopsCount = getShopCountMap(offers);

        String selectionInfo = constructSelectionInfoString(offerShopsCount);

        selectionInfoTextView.setText(selectionInfo);
    }

    // TODO extract to the view model?

    /**
     * Creates a string to describe the user selection information. Tells how many items were selected from each shop.
     *
     * @param offerShopsCount A String - Integer map where String is the shop and Integer is the number of offers from that shop
     * @return String describing the user selection information.
     */
    private String constructSelectionInfoString(Map<String, Integer> offerShopsCount) {
        StringBuilder selectionInfo;

        if (offerShopsCount.keySet().size() != 0) {
            selectionInfo = new StringBuilder(getString(R.string.shop_selection_start));
            for (String shopName : offerShopsCount.keySet()) {
                selectionInfo.append(shopName)
                        .append(" (")
                        .append(offerShopsCount.get(shopName))
                        .append("), ");
            }
            selectionInfo = new StringBuilder(selectionInfo.substring(0, selectionInfo.length() - 2) + ".");
        } else {
            selectionInfo = new StringBuilder(getString(R.string.shop_selection_no_shops));
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
}
