package task;

import entity.Gold;
import service.GoldService;
import trigger.CoinAnalyticsTrigger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Mahmood on 4/6/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinAnalyticsServiceJob implements Job {
    // Regularly check the coin price and if the Hobab is larger than a given value then fire

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("New run of CoinAnalyticsServiceJob");
        runService();
    }

    private void runService() {
        try {
            ArrayList<Gold> newGold = GoldService.getInstance().callRemoteGoldService(); // including Hobab!
            String HobabLevel = getCurrentHobabLevel(newGold);
            if (HobabLevel == null) return;
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
                if (Math.abs(next.price - next.realPrice) > 1000000)
                    return StringUtil.Hobab_Level_HIGH;
                else if (Math.abs(next.price - next.realPrice) > 500000)
                    return StringUtil.Hobab_Level_MEDIUM;
            }

        }
        return null;
    }


}
