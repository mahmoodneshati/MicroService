package task;

import entity.Gold;
import service.GoldService;
import service.MicroServiceStates;
import trigger.CoinThresholdTrigger;
import trigger.TriggerCaller;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.DBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mahmood on 4/6/2017.
 * mahmood.neshati@gmail.com
 */
public class CoinServiceJob implements Job {
    // This task execute automatically and fire the threshold triggers if it is necessary

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Checking for coin threshold service!");
        printDateTime();
        runService();
    }

    private void printDateTime() {
        Calendar cal = Calendar.getInstance();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
        String time = timestamp.getHours() + ":" + timestamp.getMinutes() +":"+timestamp.getSeconds();
        String date = timestamp.getYear() + "/" + timestamp.getMonth() +"/"+timestamp.getDate();
        System.out.println(date +"\t" +time);
    }

    private void runService() {
        try {
            ArrayList<Gold> newGolds = GoldService.getInstance().callRemoteGoldService();
            fireEligibleTriggers(newGolds);
            updateMicroServiceStateVariables(newGolds);
            updateCoinDB(newGolds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCoinDB(ArrayList<Gold> newGolds) {
        for (Gold gold : newGolds) {
            DBHelper.getInstance().insertCoin(gold);
        }

    }

    private void updateMicroServiceStateVariables(ArrayList<Gold> newGolds) {
        for (Gold gold : newGolds) {
            MicroServiceStates.getInstance().setLastGoldPrice(gold);
        }
    }

    private void fireEligibleTriggers(ArrayList<Gold> newGolds) {
        ArrayList<TriggerCaller> allTriggers = new ArrayList<>();
        for (Gold gold : newGolds) {
            ArrayList<TriggerCaller> triggers = getThresholdTriggers(gold);
            allTriggers.addAll(triggers);
        }
        for (TriggerCaller trigger : allTriggers) {
            try {
                trigger.fillParams();
                trigger.fire();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ArrayList<TriggerCaller> getThresholdTriggers(Gold gold) {
        // a trigger caller should be generated if there is an active registration on alert!
        ArrayList<TriggerCaller> allThresholds = new ArrayList<>();
        Double lastPrice = MicroServiceStates.getInstance().getLastCoinPrice(gold.englishName);
        if (lastPrice == null) return allThresholds;
        ArrayList<TriggerCaller> validThresholdsLower =
                DBHelper.getInstance().generateLowerCoinThresholdTriggers(gold.englishName,
                        lastPrice,
                        gold.price);
        ArrayList<TriggerCaller> validThresholdsUpper =
                DBHelper.getInstance().generateUpperCoinThresholdTriggers(gold.englishName,
                        lastPrice,
                        gold.price);

        allThresholds.addAll(validThresholdsLower);
        allThresholds.addAll(validThresholdsUpper);
        for (TriggerCaller next : allThresholds) {
            ((CoinThresholdTrigger) next).currentValue = gold.price;
        }
        return allThresholds;
    }

}
