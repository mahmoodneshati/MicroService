package trigger;

import util.DBHelper;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinThresholdTrigger extends TriggerCaller{

    public String coinType;

    // JSON Keys are
    private static String coinTypeKEY = "coinType";
    private static String tresholdKEY = "tresholdValue";
    private static String currentValueKEY = "currentValue";

    public static int GOUP=1;
    public static int GODOWN=-1;


    public double treshold;
    public Double currentValue;
    public int goUpper; // -1 if go lower

    Properties prop = new Properties();



    private static String sdpCoinTriggerUpper;// = "http://172.16.4.199:1515/api/trigger/currency/upper";
    private static String sdpCoinTriggerLower;// = "http://172.16.4.199:1515/api/trigger/currency/lower";

    public static int addTreshold(String coinType, double value, int flag) throws IOException {
        return DBHelper.getInstance().insertCoinTreshhold(new CoinThresholdTrigger(coinType, value, flag, null));
    }

    public CoinThresholdTrigger(String coinType, double treshold, int goUpper, Double currentValue) {
        init();
        params = new HashMap<>();
        this.coinType = coinType;
        this.treshold = treshold;
        this.goUpper = goUpper;
        this.currentValue = currentValue;
    }

    private void init(){
        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");


            prop.load(input);
            sdpCoinTriggerUpper= prop.getProperty("sdpCoinTriggerUpper");
            sdpCoinTriggerLower= prop.getProperty("sdpCoinTriggerLower");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void fillParams() {

        if(goUpper==GOUP){
            params.put(TriggerCaller.SDPURLKEY,sdpCoinTriggerUpper);
        }
        else if(goUpper==GODOWN){
            params.put(TriggerCaller.SDPURLKEY,sdpCoinTriggerLower);
        }
        else{
            System.err.println("Not supported Trigger");
        }
        params.put(coinTypeKEY,coinType);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        params.put(tresholdKEY,formatter.format(treshold));
        params.put(currentValueKEY,formatter.format(currentValue));

    }
}
