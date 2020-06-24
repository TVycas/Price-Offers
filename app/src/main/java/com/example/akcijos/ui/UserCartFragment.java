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
import com.example.akcijos.viewmodels.MainActivityViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserCartFragment extends Fragment {

    private static final String TAG = UserCartFragment.class.getName();
    private MainActivityViewModel viewModel;

    public UserCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up the RecyclerView.
        RecyclerView recyclerView = getView().findViewById(R.id.user_cart_recyclerview);
        final OfferListAdapter cartListAdapter = new OfferListAdapter(getContext());
        recyclerView.setAdapter(cartListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the offer object that was selected by the user and update the database to store the new selection value
        cartListAdapter.setOnCheckedChangedListener((view, isChecked, position) -> {
            Offer offer = cartListAdapter.getOfferAtPosition(position);
            Log.d(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
            offer.setIsSelected(isChecked);
            viewModel.updateOffer(offer);
        });

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainActivityViewModel.class);
        // Update the cart as soon as the database changes.
        viewModel.getSelectedOffers().observe(this, offers -> {
            cartListAdapter.setDisplayedOffers(offers, true);
            setSelectionInfo(offers);
        });
    }

    private void setSelectionInfo(List<Offer> offers) {
        TextView selectionInfoTextView = getView().findViewById(R.id.selection_info);
        Map<String, Integer> offerShopsCount = new HashMap<>();

        for (Offer offer : offers) {
            String shopName = offer.getSHOP_NAME();
            if (!offerShopsCount.containsKey(shopName)) {
                offerShopsCount.put(shopName, 1);
            } else {
                offerShopsCount.put(shopName, offerShopsCount.get(shopName) + 1);
            }
        }

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

        selectionInfoTextView.setText(selectionInfo.toString());
    }
}
