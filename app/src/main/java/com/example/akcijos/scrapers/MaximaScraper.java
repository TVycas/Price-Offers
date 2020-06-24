package com.example.akcijos.scrapers;

import android.util.Log;

import com.example.akcijos.database.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class MaximaScraper {

    private static final String TAG = MaximaScraper.class.getName();
    private final String html;

    MaximaScraper(String html) {
        this.html = html;
    }

    ArrayList<Offer> scrapeOffers() {
        Log.d(TAG, "Maxima scraping started");
        Document doc = Jsoup.parse(html);

        Elements elems = doc.getElementsByClass("col-third");
        ArrayList<Offer> offers = new ArrayList<>();

        for (Element e : elems) {
            String title = e.getElementsByClass("title").text();
            double price = getPrice(e);
            int percentage = getPercentage(e);
            String img = getImageLink(e);

            offers.add(new Offer(title, percentage, price, img, "Maxima"));
        }

        Log.d(TAG, "scrapeOffers: Maxima scraping finished, loaded " + offers.size() + " offers.");

        return offers;
    }

    private String getImageLink(Element e) {
        String img = "";
        String imgHtml = e.getElementsByClass("img").get(0).html();
        if (imgHtml.contains("src=")) {
            img = "https://www.maxima.lt" + imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.length() - 2);
        }
        return img;
    }

    private int getPercentage(Element e) {
        int percentage = -1;
        Elements percentageElems = e.getElementsByClass("discount percents");
        if (percentageElems.size() != 0) {
            try {
                percentage = Integer.parseInt(percentageElems.get(0).getElementsByClass("value").text());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return percentage;
    }

    private double getPrice(Element e) {
        double price = -1;
        Elements priceElems = e.getElementsByClass("t1");

        if (priceElems.size() != 0) {
            try {
                String firstHalf = priceElems.get(0).getElementsByClass("value").text();
                String secondHalf = priceElems.get(0).getElementsByClass("cents").text();
                price = Double.parseDouble(firstHalf + "." + secondHalf);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return price;
    }

}
