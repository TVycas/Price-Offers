package com.example.akcijos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OfferDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Offer offer);

    @Query("DELETE FROM offer_table")
    void deleteAll();

    @Delete
    void deleteOffer(Offer offer);

    @Query("SELECT * from offer_table LIMIT 1")
    Offer[] getAnyWord();

    @Query("SELECT * FROM offer_table ORDER BY title ASC")
    LiveData<List<Offer>> getAllOffers();

    @Update
    void update(Offer... offers);

    @Query("SELECT * FROM offer_table WHERE isSelected = 1")
    LiveData<List<Offer>> getSelectedOffers();
}
