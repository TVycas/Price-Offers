package com.example.akcijos;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> {

    private static CheckedChangeListener checkedListener;

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferViewHolder holder, int position) {
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
            holder.checkBox.setChecked(current.getIsSelected());
        } else {
            // Covers the case of data not being ready yet.
            holder.offerTitleItemView.setText(R.string.no_offer);
        }
    }

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
        OfferListAdapter.checkedListener = listener;
    }

    interface OnItemCheckListener {
        void onItemCheck(ClipData.Item item);

        void onItemUncheck(ClipData.Item item);
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
            offerTitleItemView = itemView.findViewById(R.id.offer_title_textview);
            offerPriceItemView = itemView.findViewById(R.id.offer_price_textview);
            offerShopName = itemView.findViewById(R.id.shop_name);
            checkBox = itemView.findViewById(R.id.cart_checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkedListener != null) {
                        checkedListener.onCheckedChanged(buttonView, isChecked, getAdapterPosition());
                    }
                }
            });

        }
    }
}
