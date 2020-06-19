package com.example.akcijos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Offer.class}, version = 4, exportSchema = false)
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
//                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
//    private static RoomDatabase.Callback sRoomDatabaseCallback =
//            new RoomDatabase.Callback() {
//
//                @Override
//                public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                    super.onOpen(db);
////                    new PopulateDbAsync(INSTANCE).execute();
//                }
//            };
    public abstract OfferDao offerDao();

    //TODO initial values
    // Populate the database with the initial data set
    // only if the database has no entries.

//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final OfferDao mDao;
//
//        // Initial data set
//        private static String [] words = {"dolphin", "crocodile", "cobra", "elephant", "goldfish",
//                "tiger", "snake"};
//
//        PopulateDbAsync(OfferRoomDatabase db) {
//            mDao = db.offerDao();
//        }
//
//        @Override
//        protected Void doInBackground(final Void... params) {
//            // If we have no words, then create the initial list of words.
//            if (mDao.getAnyWord().length < 1) {
//                for (int i = 0; i <= words.length - 1; i++) {
//                    Offer word = new Offer(words[i]);
//                    mDao.insert(word);
//                }
//            }
//            return null;
//        }
//    }

}
