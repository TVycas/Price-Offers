package com.example.akcijos.scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LidlScraper extends Scraper {

    private final String OFFERS_CONTAINER_CLASSNAME = "col col--sm-4 col--xs-6";
    private final String SHOP_NAME = "Lidl";
    private final String LIDL_URL;

    LidlScraper(String url) {
        LIDL_URL = url;
    }

    @Override
    public String getShopName() {
        return SHOP_NAME;
    }

    @Override
    Document getDocument() {
        try {
            return Jsoup.connect(LIDL_URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getOffersContainer() {
        return OFFERS_CONTAINER_CLASSNAME;
    }

    @Override
    public boolean isOffer(Element e) {
        return e.getElementsByClass("pricebox pricebox--highlight-offer pricebox--negative").size() != 0;
    }

    @Override
    public String getTitle(Element e) {
        return e.getElementsByClass("product__title").text().trim();
    }

    @Override
    public double getPrice(Element e) {
        String priceString = e.getElementsByClass("pricebox__price").text().replace(",", ".");
        return Double.parseDouble(priceString);
    }

    @Override
    public int getPercentage(Element e, double price) {
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

    @Override
    public String getImg(Element e) {
        String img = "";

        String imgHtml = e.getElementsByTag("img").toString();
        if (imgHtml.contains("src=")) {
            img = imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.length() - 2);
        }
        return img;
    }
}
