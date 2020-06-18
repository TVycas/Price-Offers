package com.example.akcijos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> {

    final LayoutInflater inflater;
    private List<Offer> offers;

    OfferListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    void setOffers(List<Offer> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferListAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferViewHolder holder, int position) {
        if (offers != null) {
            Offer current = offers.get(position);
            holder.offerTitleItemView.setText(current.getTitle());
            holder.offerPriceItemView.setText(String.valueOf(current.getPrice()));
        } else {
            // Covers the case of data not being ready yet.
            holder.offerTitleItemView.setText(R.string.no_offer);
        }
    }

    @Override
    public int getItemCount() {
        if (offers != null) {
            return offers.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        private final TextView offerPriceItemView;
        private final TextView offerTitleItemView;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerTitleItemView = itemView.findViewById(R.id.offer_title_textview);
            offerPriceItemView = itemView.findViewById(R.id.offer_price_textview);
        }
    }
}
