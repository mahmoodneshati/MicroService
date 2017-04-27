package trigger;

import util.DBHelper;
import util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
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
    //private static String coinTypeKEY = "coinType";
    private static String tresholdKEY = "tresholdValue";
    private String messageKEY = "message";
    private static String currentValueKEY = "currentValue";
    public static int GOUP=1;


    public static int GODOWN=-1;
    public double treshold;
    public Double currentValue;

    public int goUpper; // -1 if go lower



    Properties prop = new Properties();
    private static String sdpCoinTriggerUpper;// = "http://172.16.4.199:1515/api/trigger/currency/upper";
    private static String sdpCoinTriggerLower;// = "http://172.16.4.199:1515/api/trigger/currency/lower";

    public static int addTreshold( double value, int flag) throws IOException {
        return DBHelper.getInstance().insertCoinTreshhold(new CoinThresholdTrigger(value, flag, null));
    }

    public CoinThresholdTrigger( double treshold, int goUpper, Double currentValue) {
        init();
        params = new HashMap<>();
        this.treshold = treshold;
        this.goUpper = goUpper;
        this.currentValue = currentValue;
        this.coinType = StringUtil.Complete_Coin;
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
        //params.put(coinTypeKEY,coinType);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        params.put(tresholdKEY,formatter.format(treshold));
        //params.put(currentValueKEY,formatter.format(currentValue));
        params.put(messageKEY,generateMessage());

    }

    private String generateMessage() {
        String messagePattern = "<table>\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>هشدار قیمت تمام سکه بهار آزادی از مبلغ {0,number,integer} ریال کمتر شد.</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>هم اکنون قیمت تمام سکه بهار آزادی {1,number,integer} ریال است.</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>";
        return MessageFormat.format(messagePattern,treshold,currentValue);
    }
}
