package com.example.vewviewtests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainActivityViewModel extends AndroidViewModel {

    private OffersRepository repo;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        repo = new OffersRepository(application);
    }


    public void initScraping() {
        repo.startScraping();
    }
}
