package trigger;

import java.io.IOException;
import java.io.InputStream;
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
    private static String  currentCoinValueKEY = "currentCoinValue";
    private static String  realCoinValueKEY = "realCoinValue";
    private static String  hobabValueKEY = "hobabValue";
    private static String  hobabLevelNameKEY = "hobabLevelName";


    private double currentCoinValue;
    private double realCoinValue;
    private double  hobabValue;
    private String  hobabLevelName;

    NumberFormat formatter = NumberFormat.getCurrencyInstance();



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
        params.put(currentCoinValueKEY,formatter.format(currentCoinValue));
        params.put(realCoinValueKEY,formatter.format(realCoinValue));
        params.put(hobabValueKEY,formatter.format(hobabValue));
        params.put(hobabLevelNameKEY,hobabLevelName);
    }
}
