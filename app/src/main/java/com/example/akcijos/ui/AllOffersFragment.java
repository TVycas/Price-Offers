package com.example.akcijos.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akcijos.MainActivityViewModel;
import com.example.akcijos.R;
import com.example.akcijos.database.Offer;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllOffersFragment extends Fragment {

    private static final String TAG = AllOffersFragment.class.getName();
    private MainActivityViewModel viewModel;

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

        offerListAdapter.setOnCheckedChangedListener(new OfferListAdapter.CheckedChangeListener() {
            // Get the offer object that was selected by the user and update the database to store the new selection value
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked, int position) {
                Offer offer = offerListAdapter.getOfferAtPosition(position);
                Log.d(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
                offer.setIsSelected(isChecked);
                viewModel.updateOffer(offer);

            }
        });

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainActivityViewModel.class);
        viewModel.getAllOffers().observe(this, new Observer<List<Offer>>() {
            @Override
            public void onChanged(List<Offer> offers) {
                offerListAdapter.setDisplayedOffers(offers);
            }
        });

        SearchView searchView = getView().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                offerListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
}
