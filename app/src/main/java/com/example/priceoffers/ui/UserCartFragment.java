package com.example.priceoffers.ui;

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

import com.example.priceoffers.R;
import com.example.priceoffers.adapters.OfferListAdapter;
import com.example.priceoffers.database.Offer;
import com.example.priceoffers.viewmodels.OffersViewModel;


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

    //TODO add an option for filtering the offers.

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView selectionInfoTextView = getView().findViewById(R.id.selection_info);

        final OfferListAdapter cartListAdapter = setUpRecyclerView();

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(OffersViewModel.class);

        // Update the cart as soon as the database changes.
        viewModel.getSelectedOffers().observe(this, offers -> {
            cartListAdapter.setDisplayedOffers(offers, true, true);
        });
        // Update the selection info TextView if selections change
        viewModel.getUserSelectionInfo().observe(this, userSelectionInfo -> selectionInfoTextView.setText(userSelectionInfo));
    }

    /**
     * Sets up the RecyclerView by initiating it with a LinearLayoutManager and a custom OfferListAdapter.
     *
     * @return The OfferListAdapter used to set up the RecyclerView.
     */
    private OfferListAdapter setUpRecyclerView() {
        // Set up the RecyclerView.
        RecyclerView recyclerView = getView().findViewById(R.id.user_cart_recyclerview);
        final OfferListAdapter cartListAdapter = new OfferListAdapter(getContext(), recyclerView);
        recyclerView.setAdapter(cartListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the offer object that was deselected by the user and update the database to mark the object as deselected
        cartListAdapter.setOnCheckedChangedListener((view, isChecked, position) -> {
            Offer offer = cartListAdapter.getOfferAtPosition(position);
            offer.setIsSelected(isChecked);
            Log.i(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
            viewModel.updateOffer(offer);
        });
        return cartListAdapter;
    }

}
