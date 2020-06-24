package com.example.akcijos.scrapers;

import org.jsoup.nodes.Element;

public interface Scraper {

    String getOffersUrl();

    String getShopName();

    String getOffersContainer();

    boolean isOffer(Element e);

    String getTitle(Element e);

    double getPrice(Element e);

    int getPercentage(Element e, double price);

    String getImg(Element e);
}
