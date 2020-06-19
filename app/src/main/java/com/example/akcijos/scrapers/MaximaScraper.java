package com.example.akcijos.scrapers;

import android.util.Log;

import com.example.akcijos.database.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MaximaScraper {

    private static final String TAG = MaximaScraper.class.getName();
    private final String html;
    private ArrayList<Offer> offers;

    public MaximaScraper(String html) {
        this.html = html;
    }

    //tures buti asynctask
    public ArrayList<Offer> scrapeOffers() {
        Log.d(TAG, "Scraping started");
        Document doc = Jsoup.parse(html);
        Elements elems = doc.getElementsByClass("col-third");
        offers = new ArrayList<>();

        for (Element e : elems) {
            String title = e.getElementsByClass("title").text();
            int percentage = getPercentage(e);
            double price = getPrice(e);
            String img = getImageLink(e);

            offers.add(new Offer(title, percentage, price, img, "Maxima"));
        }

        return offers;
    }

    private String getImageLink(Element e) {
        String img = "";
        String imgHtml = e.getElementsByClass("img").get(0).html();
        if (imgHtml.contains("src=")) {
            img = imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.length() - 2);
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
                int firstHalf = Integer.parseInt(priceElems.get(0).getElementsByClass("value").text());
                int secondHalf = Integer.parseInt(priceElems.get(0).getElementsByClass("cents").text());
                price = Double.parseDouble(firstHalf + "." + secondHalf);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return price;
    }

}
