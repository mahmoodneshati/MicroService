package ir.iotel.pojo;


import trigger.CoinThresholdTrigger;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

/**
 * Created by Mahmood on 4/5/2017.
 * mahmood.neshati@gmail.com
 */
@XmlRootElement
public class CoinRegisterInterface {
    private int sdp_appletId;
    private int sdp_userId;
    private String channelName;
    private String serviceName;
    private FilterVaribales filters;
    private ChannelUserData channelUserData;

    @Override
    public String toString() {
        return "channel_Name ="+ channelName +" service_Name = "+serviceName;
    }

    public int getSdp_appletId() {
        return sdp_appletId;
    }

    public void setSdp_appletId(int sdp_appletId) {
        this.sdp_appletId = sdp_appletId;
    }

    public int getSdp_userId() {
        return sdp_userId;
    }

    public void setSdp_userId(int sdp_userId) {
        this.sdp_userId = sdp_userId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public FilterVaribales getFilters() {
        return filters;
    }

    public void setFilters(FilterVaribales filters) {
        this.filters = filters;
    }

    public ChannelUserData getChannelUserData() {
        return channelUserData;
    }

    public void setChannelUserData(ChannelUserData channelUserData) {
        this.channelUserData = channelUserData;
    }

    public int insertCoinThresholdService() {
        // This function act like service registration switch.
        // Decide about the service that should be registered using the serviceName attribute of the the incoming registration request!
        int result =0;
        try {
            if(serviceName.equalsIgnoreCase("upper"))
                result = CoinThresholdTrigger.addTreshold( this.filters.tresholdValue, CoinThresholdTrigger.GOUP);
            else if(serviceName.equalsIgnoreCase("lower"))
                result = CoinThresholdTrigger.addTreshold( this.filters.tresholdValue, CoinThresholdTrigger.GODOWN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    static class FilterVaribales {
        private double tresholdValue;

        public double getTresholdValue() {
            return tresholdValue;
        }

        public void setTresholdValue(double tresholdValue) {
            this.tresholdValue = tresholdValue;
        }
    }

    static class ChannelUserData {
    }

}
