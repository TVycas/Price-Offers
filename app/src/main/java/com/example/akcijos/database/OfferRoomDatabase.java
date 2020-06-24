package com.example.akcijos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Offer.class}, version = 6, exportSchema = false)
public abstract class OfferRoomDatabase extends RoomDatabase {
    private static OfferRoomDatabase INSTANCE;

    public static OfferRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OfferRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OfferRoomDatabase.class, "offer_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract OfferDao offerDao();


}
