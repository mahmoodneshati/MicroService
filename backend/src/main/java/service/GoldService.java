package service;

import entity.Gold;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;

import static util.StringUtil.convertPersianDigitToEnglish;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
public class GoldService {
    private static GoldService service;
    private static Properties prop = new Properties();
    private static String serviceURL;
    private static String currencyServiceURL;

    private final double haghZarb = 50000;
    private Object[] hobabInfo;


    public static GoldService getInstance() {

        if (service == null) {
            service = new GoldService();
            setConfigs();

        }
        return service;


    }

    private static String setConfigs() {
        InputStream input;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            serviceURL = prop.getProperty("parsijoo_gold_service");
            currencyServiceURL = prop.getProperty("parsijoo_currency_service");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Gold> callRemoteGoldService() throws IOException {
        Document doc = Jsoup.connect(serviceURL).get();
        Elements gold_items = doc.select("item");
        Double ons_gold_price = getGoldOnsPrice(doc);
        Double usd_price = getUSDPrice();
        ArrayList<Gold> newGolds = new ArrayList<>();
        if (usd_price != null) {
            for (org.jsoup.nodes.Element goldItem : gold_items) {
                String goldName = goldItem.select("name").get(0).text().trim();
                Double goldPrice = getDoubleValue(goldItem.select("price").get(0).text());
                Gold gold = new Gold(goldName, null, goldPrice, getHobabPrice(goldName, ons_gold_price, usd_price));
                newGolds.add(gold);
            }
        }
        return newGolds;
    }

    private Double getUSDPrice() {
        try {
            Document doc = Jsoup.connect(currencyServiceURL).get();
            Elements currency_items = doc.select("item");
            for (org.jsoup.nodes.Element currencyItem : currency_items) {
                String currencyName = currencyItem.select("name").get(0).text().trim();
                Double currencyPrice = getDoubleValue(currencyItem.select("price").get(0).text());
                if (StringEscapeUtils.escapeJava(currencyName).equalsIgnoreCase(StringUtil.USD_PERSIAN)) {
                    return currencyPrice;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }


    private Double getGoldOnsPrice(Document doc) {
        Elements gold_items = doc.select("item");
        for (org.jsoup.nodes.Element goldItem : gold_items) {

            String goldName = goldItem.select("name").get(0).text().trim();
            goldName = StringEscapeUtils.escapeJava(goldName);
            if (goldName.equalsIgnoreCase(StringUtil.ONS_GOLD_PERSIAN)) {
                return getDoubleValue(goldItem.select("price").get(0).text());
            }
        }
        return null;
    }

    private Double getHobabPrice(String goldName, Double ons_gold_price, Double usd_price) {
        Double weight = null;
        switch (StringEscapeUtils.escapeJava(goldName)) {
            case StringUtil.Complete_Coin_PERSIAN:
                weight = 8.133;
                break;
            case StringUtil.Half_Coin_PERSIAN:
                weight = 4.066;
                break;
            case StringUtil.ROB_Coin_PERSIAN:
                weight = 2.033;
                break;
            case StringUtil.GERAMI_Coin_PERSIAN:
                weight = 1.0;
                break;

        }
        if (weight == null) return null;


        return haghZarb + (weight * 0.916 * (ons_gold_price * usd_price / 31.1));
    }

    private Double getDoubleValue(String price) {
        try {
            price = price.replaceAll(",", "");
            return Double.parseDouble(convertPersianDigitToEnglish((price)));
        } catch (NumberFormatException e) {
            e.printStackTrace(); //prints error
        }

        return null;
    }


    public String getCurrentPriceMessage() {
        try {
            ArrayList<Gold> newGolds = GoldService.getInstance().callRemoteGoldService();
            double seke_kamel=0, seke_nim=0, seke_rob=0, seke_gerami=0;
            for (Gold next : newGolds) {
                if(next.englishName==null) continue;
                switch (next.englishName) {
                    case StringUtil.Complete_Coin:
                    seke_kamel = next.price;
                    break;
                    case StringUtil.Half_Coin:
                    seke_nim = next.price;
                    break;
                    case StringUtil.ROB_Coin:
                    seke_rob = next.price;
                    break;
                    case StringUtil.GERAMI_Coin:
                    seke_gerami = next.price;
                    break;
                }
            }
            return MessageFormat.format("<p>قیمت انواع سکه به شرح زیر است</p>\n" +
                    "<ul>\n" +
                    "<li>قیمت <strong>تمام سکه بهار آزادی</strong> مبلغ {0,number} ریال است</li>\n" +
                    "<li>قیمت <strong>نیم سکه بهار آزادی</strong> مبلغ {1,number} ریال است</li>\n" +
                    "<li>قیمت <strong>ربع سکه بهار آزادی</strong> مبلغ {2,number}ریال است</li>\n" +
                    "<li>قیمت <strong>سکه گرمی</strong> مبلغ {3,number} ریال است</li>\n" +
                    "</ul>", seke_kamel, seke_nim, seke_rob, seke_gerami);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "خطا در دسترسی به سرویس رخ داده است.";

    }


}