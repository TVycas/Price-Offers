package com.example.akcijos.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OfferDao {

    // TODO add id as primary key because multiple shops could have the same thing as an offer. Or maybe use composite primary key but idk about the efficiency of that.
    // https://stackoverflow.com/questions/1110349/how-can-i-define-a-composite-primary-key-in-sql

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Offer offer);

    @Query("DELETE FROM offer_table")
    void deleteAll();

    @Query("SELECT * FROM offer_table ORDER BY title ASC")
    LiveData<List<Offer>> getAllOffersAlphabetic();

    @Query("SELECT * FROM offer_table ORDER BY PERCENTAGE DESC")
    LiveData<List<Offer>> getAllOffersByDiscountHighToLow();

    @Query("SELECT * FROM offer_table ORDER BY PERCENTAGE ASC")
    LiveData<List<Offer>> getAllOffersByDiscountLowToHigh();

    @Query("SELECT * FROM offer_table WHERE SHOP_NAME = 'Iki'")
    LiveData<List<Offer>> getAllIkiOffers();

    @Query("SELECT * FROM offer_table WHERE SHOP_NAME = 'Maxima'")
    LiveData<List<Offer>> getAllMaximaOffers();

    @Update
    void update(Offer... offers);

    @Query("SELECT * FROM offer_table WHERE isSelected = 1")
    LiveData<List<Offer>> getSelectedOffers();
}
