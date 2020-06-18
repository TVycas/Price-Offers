package com.example.akcijos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllOffersFragment extends Fragment {

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
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        final OfferListAdapter offerListAdapter = new OfferListAdapter(getContext());
        recyclerView.setAdapter(offerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainActivityViewModel.class);
        viewModel.getAllOffers().observe(this, new Observer<List<Offer>>() {
            @Override
            public void onChanged(List<Offer> offers) {
                offerListAdapter.setOffers(offers);
            }
        });
    }
}
