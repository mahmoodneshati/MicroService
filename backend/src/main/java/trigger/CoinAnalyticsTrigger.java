package trigger;

import util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Mahmood on 4/6/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinAnalyticsTrigger extends TriggerCaller {

    Properties prop = new Properties();
    private static String sdpTriggerCoinAnalytics;

    // JSON Keys are
    //private static String  currentCoinValueKEY = "currentCoinValue";
    //private static String  realCoinValueKEY = "realCoinValue";
    //private static String  hobabValueKEY = "hobabValue";
    private static String  hobabLevelNameKEY = "hobabLevelName";


    private double currentCoinValue;
    private double realCoinValue;
    private double  hobabValue;
    private String  hobabLevelName;

    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private String messageKEY = "message";


    public CoinAnalyticsTrigger(double currentCoinValue, double realCoinValue, double hobabLevel, String hobabLevelName) {
        init();
        this.currentCoinValue = currentCoinValue;
        this.realCoinValue = realCoinValue;
        this.hobabValue = hobabLevel;
        this.hobabLevelName = hobabLevelName;
    }
    private void init(){
        try {
            params = new HashMap<>();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            sdpTriggerCoinAnalytics= prop.getProperty("sdpTriggerCoinAnalytics");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void fillParams() {
        params.put(TriggerCaller.SDPURLKEY,sdpTriggerCoinAnalytics);
        //params.put(currentCoinValueKEY,formatter.format(currentCoinValue));
        //params.put(realCoinValueKEY,formatter.format(realCoinValue));
        //params.put(hobabValueKEY,formatter.format(hobabValue));
        params.put(hobabLevelNameKEY,hobabLevelName);
        params.put(messageKEY,getRenderedMessage());
    }

    private String getRenderedMessage() {

        String messagePattern  = "<table>\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<p>توجه امروز حباب سکه <strong>{0}</strong> است.</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<p>قیمت سکه تمام بهار آزادی در بازار <strong>{1,number}</strong> ریال است</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>حباب سکه <strong>{2,number,integer}</strong> ریال است</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>";
        return MessageFormat.format(messagePattern,getPersianName(hobabLevelName),currentCoinValue, hobabValue);

    }

    private String getPersianName(String hobabLevelName) {
        switch (hobabLevelName) {
            case StringUtil.Hobab_Level_HIGH:
                return " زیاد";
            case StringUtil.Hobab_Level_MEDIUM:
                return " متوسط";
        }
        return "خالی";

    }


}
