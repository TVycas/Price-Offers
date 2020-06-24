package com.example.akcijos.scrapers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.akcijos.OffersRepository;
import com.example.akcijos.database.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsyncScrapeTask extends AsyncTask<Void, Void, List<Offer>> {

    private static final String TAG = AsyncScrapeTask.class.getName();
    private OffersRepository.TaskDelegate delegate;
    private Scraper scraper;

    AsyncScrapeTask(OffersRepository.TaskDelegate delegate, Scraper scraper) {
        this.delegate = delegate;
        this.scraper = scraper;
    }

    @Override
    protected List<Offer> doInBackground(Void... voids) {
        Log.d(TAG, scraper.getShopName() + " scraping started");
        ArrayList<Offer> offers = new ArrayList<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(scraper.getOffersUrl()).get();
            Elements elems = doc.getElementsByClass(scraper.getOffersContainer());

            for (Element e : elems) {
                if (scraper.isOffer(e)) {

                    String title = scraper.getTitle(e);
                    double price = scraper.getPrice(e);
                    int percentage = scraper.getPercentage(e, price);
                    String img = scraper.getImg(e);

                    offers.add(new Offer(title, percentage, price, img, scraper.getShopName()));
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + scraper.getShopName() + " offers scraping failed");
            e.printStackTrace();
        }

        Log.d(TAG, "doInBackground: " + scraper.getShopName() + " scraping finished, loaded " + offers.size() + " offers.");

        return offers;
    }

    @Override
    protected void onPostExecute(List<Offer> offers) {
        delegate.taskCompleted(offers);
        super.onPostExecute(offers);
    }
}
