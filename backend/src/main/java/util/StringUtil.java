package util;

import java.util.HashMap;

/**
 * Created by Mahmood on 4/25/2017.
 * mahmood.neshati@gmail.com
 */
public class StringUtil {


    static char[] englishChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static char[] arabicCharCode = {1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785};
    private static HashMap<String, String> goldName_goldSymbol;
    private static HashMap<String, String> goldSymbol_goldName;

    public static String USD_PERSIAN = "\\u062F\\u0644\\u0627\\u0631";


    public static final String Complete_Coin = "Complete_Coin";
    public static final String Complete_Coin_PERSIAN = "\\u0633\\u06A9\\u0647 \\u0628\\u0647\\u0627\\u0631 \\u0622\\u0632\\u0627\\u062F\\u06CC";

    public static final String GERAMI_Coin = "GERAMI_Coin";
    public static final String GERAMI_Coin_PERSIAN = "\\u0633\\u06A9\\u0647 \\u06AF\\u0631\\u0645\\u06CC";


    public static final String Half_Coin = "Half_Coin";
    public static final String Half_Coin_PERSIAN = "\\u0646\\u06CC\\u0645 \\u0633\\u06A9\\u0647";

    public static final String ROB_Coin = "ROB_Coin";
    public static final String ROB_Coin_PERSIAN = "\\u0631\\u0628\\u0639 \\u0633\\u06A9\\u0647";
    public static final String ONS_GOLD_PERSIAN = "\\u0627\\u0646\\u0633 \\u0637\\u0644\\u0627";


    /*_______________________ Coin Hobab ____________________________*/

    public static final String Hobab_Level_HIGH_PERSIAN = "حباب زیاد (بیش از 100 هزارتومان)";
    public static final String Hobab_Level_HIGH = "Hobab_Level_HIGH";

    public static final String Hobab_Level_MEDIUM_PERSIAN = "حباب متوسط (بیش از 50 هزارتومان)";
    public static final String Hobab_Level_MEDIUM = "Hobab_Level_MEDIUM";





    public static HashMap<String, String> GoldNameMapper() {
        if (goldName_goldSymbol == null) {
            goldName_goldSymbol = new HashMap<>();
            goldName_goldSymbol.put(Complete_Coin_PERSIAN, Complete_Coin);
            goldName_goldSymbol.put(Half_Coin_PERSIAN, Half_Coin);
            goldName_goldSymbol.put(ROB_Coin_PERSIAN, ROB_Coin);
            goldName_goldSymbol.put(GERAMI_Coin_PERSIAN, GERAMI_Coin);
        }
        return goldName_goldSymbol;
    }

    public static HashMap<String, String> GoldNameMapperReverse() {
        if (goldSymbol_goldName == null) {
            goldSymbol_goldName = new HashMap<>();
            goldSymbol_goldName.put(Complete_Coin, Complete_Coin_PERSIAN);
            goldSymbol_goldName.put(Half_Coin, Half_Coin_PERSIAN);
            goldSymbol_goldName.put(ROB_Coin, ROB_Coin_PERSIAN);
            goldSymbol_goldName.put(GERAMI_Coin, GERAMI_Coin_PERSIAN);
        }
        return goldSymbol_goldName;
    }






    public static String convertPersianDigitToEnglish(String input) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) >= 1776 && input.charAt(i) <= 1785) {

                int index = getIndexByname(input.charAt(i));
                if (index == -1) {
                    System.err.println("Error Converting digits");
                    break;
                } else {
                    builder.append(englishChars[index]);
                }
            } else if (input.charAt(i) == 46) {
                builder.append('.');
            }

        }
        return builder.toString().trim();
    }


    private static int getIndexByname(char ch) {
        int index = 0;
        for (char _item : arabicCharCode) {
            if (_item == ch)
                return index;
            index++;
        }
        return -1;
    }


}
