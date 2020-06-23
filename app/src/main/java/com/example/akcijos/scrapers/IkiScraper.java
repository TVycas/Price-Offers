package com.example.akcijos.scrapers;

import android.util.Log;

import com.example.akcijos.database.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class IkiScraper {

    private static final String TAG = IkiScraper.class.getName();
    private final String html;
    private ArrayList<Offer> offers;

    public IkiScraper(String html) {
        this.html = html;
    }

    ArrayList<Offer> scrapeOffers() {
        Log.d(TAG, "Iki scraping started");

        Document doc = Jsoup.parse(html);
        Elements elems = doc.getElementsByClass("akcija__anchor akcija-inner");
        offers = new ArrayList<>();

        for (Element e : elems) {
            String title = e.getElementsByClass("akcija__title").text();
            double price = getPrice(e);
            int percentage = getPercentage(e, price);
            String img = getImageLink(e);
            offers.add(new Offer(title, percentage, price, img, "Iki"));
        }

        return offers;
    }

    private String getImageLink(Element e) {
        String img = "";
        String imgHtml = e.getElementsByClass("akcija__image").get(0).html();
        if (imgHtml.contains("src=")) {
            img = imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.indexOf("alt") - 2);
        }
        return img;
    }

    private int getPercentage(Element e, double price) {
        int percentage = -1;

        Elements priceCents = e.getElementsByClass("price-cents");
        Elements top = e.getElementsByClass("top");
        Elements priceOld = e.getElementsByClass("price-old");

        // No price info, just the percentage
        if (priceCents.size() == 0) {
            Elements percentageElems = e.getElementsByClass("price-main");
            try {
                String percentageString = percentageElems.get(0).text();
                percentage = Integer.parseInt(percentageString.substring(1, percentageString.length() - 1));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            // Percentage is written at the top bar, above the price
        } else if (top.size() != 0) {
            String topString = top.get(0).getElementsByClass("text").text();
            if (!topString.equals("Tik") && !topString.contains("+")) {
                percentage = Integer.parseInt(topString.substring(1, topString.length() - 1));
            }
            // Not percentage information, but there is the old price so we can try calculating the percentage ourselves
        } else if (priceOld.size() != 0) {
            String oldPriceString = priceOld.text();
            double oldPrice = Double.parseDouble(oldPriceString.substring(0, oldPriceString.length() - 2)
                    + "." +
                    oldPriceString.substring(oldPriceString.length() - 2));

            percentage = (int) Math.round(100 - (price * 100 / oldPrice));
        }

        return percentage;
    }

    private double getPrice(Element e) {
        double price = -1;
        Elements priceCents = e.getElementsByClass("price-cents");

        if (priceCents.size() != 0) {
            Elements priceMain = e.getElementsByClass("price-main");
            try {
                price = Double.parseDouble(priceMain.get(0).text() + "." + priceCents.get(0).text());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return price;
    }
}
