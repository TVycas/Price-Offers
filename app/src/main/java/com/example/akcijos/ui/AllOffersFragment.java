package com.example.akcijos.ui;

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

import com.example.akcijos.R;
import com.example.akcijos.database.Offer;
import com.example.akcijos.viewmodels.MainActivityViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllOffersFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AllOffersFragment.class.getName();
    private MainActivityViewModel viewModel;
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

        // Set up the RecyclerView.
        RecyclerView recyclerView = getView().findViewById(R.id.all_offers_recyclerview);
        final OfferListAdapter offerListAdapter = new OfferListAdapter(getContext());
        recyclerView.setAdapter(offerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the offer object that was selected by the user and update the database to store the new selection value
        offerListAdapter.setOnCheckedChangedListener((view, isChecked, position) -> {
            Offer offer = offerListAdapter.getOfferAtPosition(position);
            Log.d(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
            offer.setIsSelected(isChecked);
            viewModel.updateOffer(offer);

        });

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainActivityViewModel.class);
        viewModel.getAllOffers().observe(this, offers -> {
            if (offers.size() != 0) {
                getActivity().findViewById(R.id.progress_circular).setVisibility(View.GONE);
            }

            if (!queryText.equals("")) {
                // Don't update the dataSetChanged because it will update after filtering
                offerListAdapter.setDisplayedOffers(offers, false);
                offerListAdapter.getFilter().filter(queryText);
            } else {
                offerListAdapter.setDisplayedOffers(offers, true);
            }
        });

        // Set up the search view to search offers
        SearchView searchView = getView().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

        // Set up spinner for filtering the offers
        Spinner filtersSpinner = getView().findViewById(R.id.filters_spinner);
        ArrayAdapter<CharSequence> filterSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_names, android.R.layout.simple_spinner_item);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtersSpinner.setAdapter(filterSpinnerAdapter);
        filtersSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewModel.filterOffers(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
