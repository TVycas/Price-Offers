package com.example.priceoffers.scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class MaximaScraper extends Scraper {

    private final String html;
    private final String OFFERS_CONTAINER_CLASSNAME = "col-third";
    private final String SHOP_NAME = "Maxima";

    MaximaScraper(String html) {
        this.html = html;
    }

    @Override
    String getShopName() {
        return SHOP_NAME;
    }

    @Override
    Document getDocument() {
        return Jsoup.parse(html);
    }

    @Override
    String getOffersContainer() {
        return OFFERS_CONTAINER_CLASSNAME;
    }

    @Override
    boolean isOffer(Element e) {
        return true;
    }

    @Override
    String getTitle(Element e) {
        return e.getElementsByClass("title").text();
    }

    @Override
    double getPrice(Element e) {
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

    @Override
    int getPercentage(Element e, double price) {
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

    @Override
    String getImg(Element e) {
        String img = "";
        String imgHtml = e.getElementsByClass("img").get(0).html();
        if (imgHtml.contains("src=")) {
            img = "https://www.maxima.lt" + imgHtml.substring(imgHtml.indexOf("src=") + 5, imgHtml.length() - 2);
        }
        return img;
    }

}
