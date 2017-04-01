package com.outsmart.outsmartpower.network.records;

/**
 * Created by Rene Moise on 3/27/2017.
 */

/**
 * This class is used to create an echo request record. This implements the toJSONString.
 * This method is responsible to make a request packet that will be sent to the smart-outlet.
 * It will be sent to request the presence status of the smart-outlet.
 */

public class EchoRequestRecord extends EchoRecord {
    /**
     * Inherited fields.
     *  1. data
     */

    //The constructor is not doing anything for now.
    public EchoRequestRecord() {
    }

    @Override
    public String toJSONString() {
        try{
            data.put("type","REQU");
            data.put("data","there?");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }
}
