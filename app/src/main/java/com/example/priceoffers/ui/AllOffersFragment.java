package com.example.priceoffers.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.priceoffers.R;
import com.example.priceoffers.adapters.OfferListAdapter;
import com.example.priceoffers.database.Offer;
import com.example.priceoffers.viewmodels.OffersViewModel;


/**
 * {@link Fragment} subclass.
 */
public class AllOffersFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AllOffersFragment.class.getName();

    private OffersViewModel viewModel;

    private boolean userSelectedOffer = false;
    /**
     * Variable used to store the query text in case of filter changes
     */
    private String queryText = "";

    public AllOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_offers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final OfferListAdapter offerListAdapter = setUpRecyclerView();
        setUpSearchView(offerListAdapter);
        setUpFiltersSpinner();

        // Observe ViewModel to display the list of offers
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(OffersViewModel.class);
        viewModel.getAllOffers().observe(this, offers -> {
            if (offers.size() != 0) {
                // Stop the progress circle
                getActivity().findViewById(R.id.progress_circular).setVisibility(View.GONE);
            }

            // Multiple settings for list update control.
            if (userSelectedOffer && queryText.equals("")) {
                // User selected an offer.
                offerListAdapter.setDisplayedOffers(offers, false, true);
                userSelectedOffer = false;
            } else if (userSelectedOffer) {
                // User selected and offer while searching for offers.
                offerListAdapter.setDisplayedOffers(offers, false, false);
                offerListAdapter.getFilter().filter(queryText);
                userSelectedOffer = false;
            } else if (!queryText.equals("")) {
                // User is searching of offers.
                offerListAdapter.setDisplayedOffers(offers, true, false);
                offerListAdapter.getFilter().filter(queryText);
            } else {
                // User refreshed the offers.
                offerListAdapter.setDisplayedOffers(offers, true, true);
            }
        });


    }

    /**
     * Sets up the Spinner used to select the filter of the offer list.
     */
    private void setUpFiltersSpinner() {
        // Set up spinner for filtering the offers
        Spinner filtersSpinner = getView().findViewById(R.id.filters_spinner);
        ArrayAdapter<CharSequence> filterSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_names, android.R.layout.simple_spinner_item);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtersSpinner.setAdapter(filterSpinnerAdapter);
        filtersSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Sets up the SearchView of the fragment.
     *
     * @param offerListAdapter The adapter used in the RecyclerView.
     */
    private void setUpSearchView(OfferListAdapter offerListAdapter) {
        SearchView searchView = getView().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!queryText.equals("")) {
                    offerListAdapter.getFilter().filter(queryText);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Store the query text to redo search if data filter changes
                queryText = newText;
                offerListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Sets up the RecyclerView by initiating it with a LinearLayoutManager and a custom OfferListAdapter.
     *
     * @return The OfferListAdapter used to set up the RecyclerView.
     */
    private OfferListAdapter setUpRecyclerView() {
        // Set up the RecyclerView.
        RecyclerView recyclerView = getView().findViewById(R.id.all_offers_recyclerview);
        final OfferListAdapter offerListAdapter = new OfferListAdapter(getContext(), recyclerView);
        recyclerView.setAdapter(offerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the offer object that was selected by the user and update the database to mark the object as selected
        offerListAdapter.setOnCheckedChangedListener((view, isChecked, position) -> {
            Offer offer = offerListAdapter.getOfferAtPosition(position);
            offer.setIsSelected(isChecked);
            Log.i(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
            userSelectedOffer = true;
            viewModel.updateOffer(offer);
        });
        return offerListAdapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // When the user changes the filter setting, notify the ViewModel to give a different LiveData object based on the filter value
        viewModel.filterOffers(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
}
