package entity;

import org.apache.commons.lang.StringEscapeUtils;
import util.StringUtil;

import java.util.HashMap;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
public class Gold {
    public String persianName;
    public String englishName;
    public Double price;
    public Double realPrice;

    public Gold(String persianName, String englishName, Double price, Double hobab) {
        this.persianName = persianName;
        if (englishName == null)
            this.englishName = getEnglishName(persianName);
        else if (persianName == null)
            this.persianName = getPersianName(englishName);
        this.price = price;
        this.realPrice = hobab;
    }

    private String getEnglishName(String persianName) {
        String test = StringEscapeUtils.escapeJava(persianName);
        HashMap<String, String> name_code = StringUtil.GoldNameMapper();
        for (String name : name_code.keySet()) {
            if (name.equalsIgnoreCase(test))
                return name_code.get(name);
        }
        return null;    }

    private String getPersianName(String englishName) {
        HashMap<String, String> code_name = StringUtil.GoldNameMapperReverse();
        for (String name : code_name.keySet()) {
            if (name.equalsIgnoreCase(englishName))
                return code_name.get(name);
        }
        return null;
    }

    @Override
    public String toString() {
        return persianName + "\t" + englishName + "\t" + price + "\t" + realPrice;

    }
}
