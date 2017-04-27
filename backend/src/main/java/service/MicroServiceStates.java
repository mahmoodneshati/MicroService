package service;

import entity.Gold;
import util.DBHelper;

import java.util.HashMap;

/**
 * Created by neshati on 1/29/2017.
 * Behpardaz
 */
public class MicroServiceStates {
    private static MicroServiceStates ms;
    private HashMap<String,Gold> lastCoinPrice;

    public static MicroServiceStates getInstance(){
        if(ms==null){
            ms = new MicroServiceStates();
        }
        return ms;
    }


    public Double getLastCoinPrice(String coinName) {
        if(lastCoinPrice ==null) {
            lastCoinPrice = new HashMap<>();
            lastCoinPrice.putAll(DBHelper.getInstance().loadLastCoinPrice());
        }
        return lastCoinPrice.get(coinName)==null?null: lastCoinPrice.get(coinName).price;
    }

    public void setLastGoldPrice(Gold gold) {
        lastCoinPrice.put(gold.englishName, gold);
    }
}
