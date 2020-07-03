package com.example.akcijos.scrapers;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IkiScraper implements Scraper {

    private final String OFFERS_CONTAINER_CLASSNAME = "akcija__anchor akcija-inner";
    private final String SHOP_NAME = "Iki";
    private String ikiUrl;

    IkiScraper(String url) {
        ikiUrl = url;
    }

    @Override
    public String getOffersUrl() {
        return ikiUrl;
    }

    @Override
    public String getShopName() {
        return SHOP_NAME;
    }

    @Override
    public String getOffersContainer() {
        return OFFERS_CONTAINER_CLASSNAME;
    }

    @Override
    public boolean isOffer(Element e) {
        return true;
    }

    @Override
    public String getTitle(Element e) {
        return e.getElementsByClass("akcija__title").text();
    }

    @Override
    public double getPrice(Element e) {
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

    @Override
    public int getPercentage(Element e, double price) {
        int percentage = -1;

        Elements priceCents = e.getElementsByClass("price-cents");
        Elements top = e.getElementsByClass("top");
        Elements priceOld = e.getElementsByClass("price-old");

        // No price info, just the percentage
        if (priceCents.size() == 0) {
            Elements percentageElems = e.getElementsByClass("price-main");
            String percentageString = percentageElems.get(0).text();
            try {
                if (percentageString.contains("iki")) {
                    percentage = Integer.parseInt(percentageString.substring(5, percentageString.length() - 1));
                } else {
                    percentage = Integer.parseInt(percentageString.substring(1, percentageString.length() - 1));
                }
            } catch (Exception ex) {
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

    @Override
    public String getImg(Element e) {
        String img = "";
        String imgHtml = e.getElementsByClass("akcija__image").get(0).html();
        if (imgHtml.contains("src=")) {
            img = imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.indexOf("alt") - 2);
        }
        return img;
    }
}
