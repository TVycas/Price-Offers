package com.example.akcijos.scrapers;

import com.example.akcijos.database.Offer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public abstract class Scraper {

    ArrayList<Offer> scrapeOffers() {
        ArrayList<Offer> offers = new ArrayList<>();
        Document doc = getDocument();
        Elements elems = doc.getElementsByClass(getOffersContainer());

        for (Element e : elems) {
            if (isOffer(e)) {

                String title = getTitle(e);
                double price = getPrice(e);
                int percentage = getPercentage(e, price);
                String img = getImg(e);

                offers.add(new Offer(title, percentage, price, img, getShopName()));
            }
        }

        return offers;
    }

    abstract String getShopName();

    abstract Document getDocument();

    abstract String getOffersContainer();

    abstract boolean isOffer(Element e);

    abstract String getTitle(Element e);

    abstract double getPrice(Element e);

    abstract int getPercentage(Element e, double price);

    abstract String getImg(Element e);
}
