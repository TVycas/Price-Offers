package com.example.priceoffers.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OfferDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Offer offer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Offer> offers);

    @Query("DELETE FROM offer_table")
    void deleteAll();

    @Query("SELECT * FROM offer_table ORDER BY title ASC")
    LiveData<List<Offer>> getAllOffersAlphabetic();

    @Query("SELECT * FROM offer_table ORDER BY PERCENTAGE DESC")
    LiveData<List<Offer>> getAllOffersByDiscountHighToLow();

    @Query("SELECT * FROM offer_table ORDER BY PERCENTAGE ASC")
    LiveData<List<Offer>> getAllOffersByDiscountLowToHigh();

    @Query("SELECT * FROM offer_table WHERE SHOP_NAME = 'Iki' ORDER BY PERCENTAGE DESC")
    LiveData<List<Offer>> getAllIkiOffers();

    @Query("SELECT * FROM offer_table WHERE SHOP_NAME = 'Maxima' ORDER BY PERCENTAGE DESC")
    LiveData<List<Offer>> getAllMaximaOffers();

    @Query("SELECT * FROM offer_table WHERE SHOP_NAME = 'Lidl' ORDER BY PERCENTAGE DESC")
    LiveData<List<Offer>> getAllLidlOffers();

    @Update
    void update(Offer... offers);

    @Query("SELECT * FROM offer_table WHERE isSelected = 1")
    LiveData<List<Offer>> getSelectedOffers();
}
