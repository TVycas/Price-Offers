package com.example.priceoffers.scrapers;

import com.example.priceoffers.database.Offer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * An Abstract class to be extended by all web scrapers.
 */
abstract class Scraper {
    /**
     * Method controlling the main scraping logic. It gets the Jsoup Document and scrapes a list of offer object from it
     * @return An ArrayList of offer objects
     */
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

    /**
     * Addition check if the Element e is and offer and not some other element
     * @param e An Element object to be checked
     * @return True if the element is an offer
     */
    abstract boolean isOffer(Element e);

    abstract String getTitle(Element e);

    abstract double getPrice(Element e);

    abstract int getPercentage(Element e, double price);

    /**
     * Given an Element e, extract the link to the img file from it
     * @param e Element of an offer
     * @return The link to the img file
     */
    abstract String getImg(Element e);
}
