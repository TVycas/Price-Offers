package com.example.akcijos.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akcijos.R;
import com.example.akcijos.database.Offer;

import java.util.List;

class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> {

    private final LayoutInflater inflater;
    private CheckedChangeListener checkedListener;
    private List<Offer> offers;


    OfferListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    void setOffers(List<Offer> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferViewHolder holder, final int position) {
        if (offers != null) {

            Offer current = offers.get(position);
            holder.offerTitleItemView.setText(current.getTITLE());

            if (current.getPRICE() == -1) {
                // TODO use placeholders
                holder.offerPriceItemView.setText("-" + current.getPERCENTAGE() + "%");
            } else {
                holder.offerPriceItemView.setText(current.getPRICE() + "€");
            }
            // TODO add previous price
            holder.offerShopName.setText(current.getSHOP_NAME());

            // Remove the old listener and replace with new one
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(current.getIsSelected());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkedListener != null) {
                        checkedListener.onCheckedChanged(buttonView, isChecked, position);
                    }
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.offerTitleItemView.setText(R.string.no_offer);
        }
    }

    @NonNull
    @Override
    public OfferListAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new OfferViewHolder(itemView);
    }

    public Offer getOfferAtPosition(int position) {
        return offers.get(position);
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

    public void setOnCheckedChangedListener(CheckedChangeListener listener) {
        checkedListener = listener;
    }

    public interface CheckedChangeListener {
        void onCheckedChanged(CompoundButton view, boolean isChecked, int position);
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {
        private final TextView offerPriceItemView;
        private final TextView offerTitleItemView;
        private final TextView offerShopName;
        private final CheckBox checkBox;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerTitleItemView = itemView.findViewById(R.id.offer_title_textview);
            offerPriceItemView = itemView.findViewById(R.id.offer_price_textview);
            offerShopName = itemView.findViewById(R.id.shop_name);
            checkBox = itemView.findViewById(R.id.cart_checkbox);
        }
    }
}
