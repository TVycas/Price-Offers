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

public class LidlScraper extends AsyncTask<String, Void, List<Offer>> {

    private static final String TAG = LidlScraper.class.getName();
    private OffersRepository.TaskDelegate delegate;

    public LidlScraper(OffersRepository.TaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Offer> doInBackground(String... strings) {
        Log.d(TAG, "Lidl scraping started");
        ArrayList<Offer> offers = new ArrayList<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(strings[0]).get();
            Elements elems = doc.getElementsByClass("col col--sm-4 col--xs-6");

            for (Element e : elems) {
                if (e.getElementsByClass("product__title").size() != 0) {

                    String title = e.getElementsByClass("product__title").text().trim();
                    String priceString = e.getElementsByClass("pricebox__price").text().replace(",", ".");
                    double price = Double.parseDouble(priceString);
                    int percentage = getPercentage(e, price);
                    String img = getImageLink(e);

                    offers.add(new Offer(title, percentage, price, img, "Lidl"));
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Lidl offers scraping failed");
            e.printStackTrace();
        }

        Log.d(TAG, "doInBackground: Lidl scraping finished");

        return offers;
    }

    @Override
    protected void onPostExecute(List<Offer> offers) {
        delegate.taskCompleted(offers);
        super.onPostExecute(offers);
    }

    private String getImageLink(Element e) {
        String img = "";

        String imgHtml = e.getElementsByTag("img").toString();
        if (imgHtml.contains("src=")) {
            img = imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.length() - 2);
        }
        return img;
    }

    private int getPercentage(Element e, double price) {
        int percentage = -1;

        String highlight = e.getElementsByClass("pricebox__highlight").text();

        if (highlight.matches(".*\\d.*") && highlight.contains("%")) {
            highlight = highlight.replaceAll("[-|%]", "").trim();
            percentage = Integer.parseInt(highlight);
        } else {
            Elements oldPriceElemets = e.getElementsByClass("pricebox__discount-wrapper");
            if (oldPriceElemets.size() != 0 && !oldPriceElemets.get(0).text().equals("")) {
                String oldPriceString = oldPriceElemets.get(0).text().split(" ")[0].replace(",", ".");
                double oldPrice = Double.parseDouble(oldPriceString);
                percentage = (int) Math.round(100 - (price * 100 / oldPrice));
            }
        }

        return percentage;
    }

}
