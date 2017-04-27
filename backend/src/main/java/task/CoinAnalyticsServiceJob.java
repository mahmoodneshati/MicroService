package task;

import entity.Gold;
import service.GoldService;
import trigger.CoinAnalyticsTrigger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Mahmood on 4/6/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinAnalyticsServiceJob implements Job {
    // Regularly check the coin price and if the Hobab is larger than a given value then fire

    private static Properties prop = new Properties();
    private String Hobab_Level_HIGH_TRESHHOLD;
    private String Hobab_Level_MEDIUM_TRESHHOLD;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("New run of CoinAnalyticsServiceJob");
        runService();
    }

    private void runService() {
        try {
            setConfigs();
            ArrayList<Gold> newGold = GoldService.getInstance().callRemoteGoldService(); // including Hobab!
            String HobabLevel = getCurrentHobabLevel(newGold);
            if (HobabLevel == null) {
                // the hobab level is not high or medium
                return;
            }
            fireEligibleTriggers(newGold, HobabLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fireEligibleTriggers(ArrayList<Gold> newGold, String hobabLevel) {
        Gold completeCoin = getCompleteCoin(newGold);
        assert completeCoin != null;
        CoinAnalyticsTrigger coinAnalyticsTrigger = new CoinAnalyticsTrigger(completeCoin.price,completeCoin.realPrice,
                Math.abs(completeCoin.price - completeCoin.realPrice),hobabLevel);
        coinAnalyticsTrigger.fillParams();
        try {
            coinAnalyticsTrigger.fire();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Gold getCompleteCoin(ArrayList<Gold> newGold) {
        for (Gold next : newGold) {
            if (next.englishName.equalsIgnoreCase(StringUtil.Complete_Coin)) {
                return next;
            }

        }
        return null;
    }

    private String getCurrentHobabLevel(ArrayList<Gold> newCurrencies) {
        for (Gold next : newCurrencies) {
            if (Objects.equals(next.englishName, StringUtil.Complete_Coin)) {
                if (Math.abs(next.price - next.realPrice) > Double.parseDouble(Hobab_Level_HIGH_TRESHHOLD))
                    return StringUtil.Hobab_Level_HIGH;
                else if (Math.abs(next.price - next.realPrice) > Double.parseDouble(Hobab_Level_MEDIUM_TRESHHOLD))
                    return StringUtil.Hobab_Level_MEDIUM;
            }

        }
        return null;
    }

    private String setConfigs() {
        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            Hobab_Level_HIGH_TRESHHOLD = prop.getProperty("Hobab_Level_HIGH_TRESHHOLD");
            Hobab_Level_MEDIUM_TRESHHOLD = prop.getProperty("Hobab_Level_MEDIUM_TRESHHOLD");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
