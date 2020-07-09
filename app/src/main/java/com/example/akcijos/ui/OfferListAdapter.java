package com.example.akcijos.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akcijos.R;
import com.example.akcijos.database.Offer;

import java.util.ArrayList;
import java.util.List;

class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> implements Filterable {

    private final LayoutInflater inflater;
    /**
     * A CheckedChangeListener to notify that an offer was selected
     */
    private CheckedChangeListener checkedListener;
    /**
     * List of offers after filtering to be displayed
     */
    private List<Offer> displayedOffers;
    /**
     * All of the offers available to display
     */
    private List<Offer> allOffers;

    /**
     * A flag for deciding whether to move the list to top position after the data has changed
     */
    private boolean shouldMoveListToTop = true;
    private RecyclerView recyclerView;


    /**
     * An offers filter which given a char sequence, filters the offers based on their title containing the char sequence
     */
    private Filter offersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Offer> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allOffers);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                // Filter the list of all offers to see if the pattern is in any of the offers
                for (Offer offer : allOffers) {
                    if (offer.getTITLE().toLowerCase().contains(filterPattern)) {
                        filteredList.add(offer);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Update the displayed offers and refresh the recyclerView
            displayedOffers.clear();
            displayedOffers.addAll((List) results.values);
            notifyDataChangedControl();
        }
    };

    OfferListAdapter(Context context, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
    }

    void setDisplayedOffers(List<Offer> offers, boolean shouldMoveListToTop, boolean shouldNotifyDataChanged) {
        if (offers != null) {
            // Make lists for storing and displaying the offers
            displayedOffers = offers;
            allOffers = new ArrayList<>(offers);

            this.shouldMoveListToTop = shouldMoveListToTop;
            if (shouldNotifyDataChanged) {
                notifyDataChangedControl();
            }
        }
    }

    /**
     * Helper method for notifying that the data has changed and moving or not moving the list to the top
     */
    private void notifyDataChangedControl() {
        if (shouldMoveListToTop) {
            notifyDataSetChanged();
            recyclerView.scrollToPosition(0);
        } else {
            notifyDataSetChanged();
        }
        shouldMoveListToTop = true;
    }

    @NonNull
    @Override
    public OfferListAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferViewHolder holder, final int position) {
        if (displayedOffers != null) {
            // Set up the data displayed in the viewHolder
            Offer current = displayedOffers.get(position);
            holder.offerTitleItemView.setText(current.getTITLE());

            // Display different information based on what is available
            if (current.getPRICE() == -1) {
                holder.offerPriceItemView.setText(holder.itemView.getContext().getString(R.string.percentage, current.getPERCENTAGE()));
            } else if (current.getPERCENTAGE() != -1) {
                holder.offerPriceItemView.setText(holder.itemView.getContext().getString(R.string.price_and_percentage, current.getPRICE(), current.getPERCENTAGE()));
            } else {
                holder.offerPriceItemView.setText(holder.itemView.getContext().getString(R.string.price, current.getPRICE()));
            }

            holder.offerShopName.setText(current.getSHOP_NAME());

            // Remove the old listener and replace with new one
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(current.getIsSelected());
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkedListener != null) {
                    checkedListener.onCheckedChanged(buttonView, isChecked, position);
                }
            });

        } else {
            // Covers the case of data not being ready yet.
            holder.offerTitleItemView.setText(R.string.no_offers_available);
        }
    }

    Offer getOfferAtPosition(int position) {
        return displayedOffers.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    void setOnCheckedChangedListener(CheckedChangeListener listener) {
        checkedListener = listener;
    }

    @Override
    public int getItemCount() {
        if (displayedOffers != null) {
            return displayedOffers.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return offersFilter;
    }

    public interface CheckedChangeListener {
        void onCheckedChanged(CompoundButton view, boolean isChecked, int position);
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {

        private final TextView offerPriceItemView;
        private final TextView offerTitleItemView;
        private final TextView offerShopName;
        private final CheckBox checkBox;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views to be set
            offerTitleItemView = itemView.findViewById(R.id.offer_title_textview);
            offerPriceItemView = itemView.findViewById(R.id.offer_price_textview);
            offerShopName = itemView.findViewById(R.id.shop_name);
            checkBox = itemView.findViewById(R.id.cart_checkbox);
        }
    }
}
