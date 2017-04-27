package ir.iotel.server;

/**
 * Created by Mahmood on 4/25/2017.
 * mahmood.neshati@gmail.com
 */

import entity.Gold;
import ir.iotel.pojo.CoinRegisterInterface;
import ir.iotel.pojo.GoldCurrentPriceRequest;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.GoldService;
import util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


@Path("coin")
public class SDPEndpoint {
   /* private static Properties prop = new Properties();
    private static String gold_endpoint;*/


    @GET
    @Produces("application/json")
    public String getCoinTypes() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(getJsonObject(StringUtil.Complete_Coin, StringEscapeUtils.unescapeJava(StringUtil.Complete_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.Half_Coin, StringEscapeUtils.unescapeJava(StringUtil.Half_Coin_PERSIAN)));
        array.put(getJsonObject(StringUtil.ROB_Coin, StringEscapeUtils.unescapeJava(StringUtil.ROB_Coin_PERSIAN)));
        return array.toString();
    }

    private JSONObject getJsonObject(String usd, String name) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Code", usd);
        json.put("Title", name);
        return json;
    }

/*    @PostConstruct
    private static String setConfigs() {
        try {
            System.out.println("SDPEndpoint.setConfigs");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            gold_endpoint = prop.getProperty("gold_endpoint");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    @Path("instant")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getCurrentGoldPrice(GoldCurrentPriceRequest goldPriceRequest) throws JSONException {
        JSONObject out = new JSONObject();
        out.put("message", GoldService.getInstance().getCurrentPriceMessage());
        return out.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCurrenyService(CoinRegisterInterface service) throws JSONException {
        int result = service.insertCoinThresholdService();
        if(result>0) {
            return Response.status(200).build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    // The Java method will process HTTP GET requests
    @Path("HobabLevel")
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getHobabLevel() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(getJsonObject(StringUtil.Hobab_Level_HIGH, StringEscapeUtils.unescapeJava(StringUtil.Hobab_Level_HIGH_PERSIAN)));
        array.put(getJsonObject(StringUtil.Hobab_Level_MEDIUM, StringEscapeUtils.unescapeJava(StringUtil.Hobab_Level_MEDIUM_PERSIAN)));
        return array.toString();
    }







}