package com.example.akcijos.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akcijos.MainActivityViewModel;
import com.example.akcijos.R;
import com.example.akcijos.database.Offer;

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

        cartListAdapter.setOnCheckedChangedListener(new OfferListAdapter.CheckedChangeListener() {
            // Get the offer object that was selected by the user and update the database to store the new selection value
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked, int position) {
                Offer offer = cartListAdapter.getOfferAtPosition(position);
                Log.d(TAG, "onCheckedChanged: Checked status changed to " + offer.getIsSelected() + " for " + offer.getTITLE());
                offer.setIsSelected(isChecked);
                viewModel.updateOffer(offer);
            }
        });

        // Observe ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MainActivityViewModel.class);
        viewModel.getSelectedOffers().observe(this, new Observer<List<Offer>>() {
            // Update the cart as soon as the database changes.
            @Override
            public void onChanged(List<Offer> offers) {
                cartListAdapter.setDisplayedOffers(offers, true);
                setSelectionInfo(offers);
            }
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


        String selectionInfo = "";
        if (offerShopsCount.keySet().size() != 0) {
            selectionInfo = "Selected shops: ";
            for (String shopName : offerShopsCount.keySet()) {
                selectionInfo += shopName + " (" + offerShopsCount.get(shopName) + " offers), ";
            }

            selectionInfo = selectionInfo.substring(0, selectionInfo.length() - 2) + ".";
        } else {
            selectionInfo = "No selected shops.";
        }

        Log.d(TAG, "setSelectionInfo: " + selectionInfo);
        selectionInfoTextView.setText(selectionInfo);
    }
}
