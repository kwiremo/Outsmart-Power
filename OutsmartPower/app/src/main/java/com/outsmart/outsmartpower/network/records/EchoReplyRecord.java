package com.outsmart.outsmartpower.network.records;

/**
 * Created by Rene Moise on 3/27/2017.
 */

/**
 * This class is used to create an echo reply record. This implements the toJSONString.
 * This method is responsible to make a reply packet that will be sent to the smart-outlet.
 * It will be sent to notify that the phone app is app and running.
 */

public class EchoReplyRecord extends EchoRecord {

    /**
     * Inherited fields.
     *  1. data
     */

    /**
     * It doesn't initialize anything for now.
     */
    public EchoReplyRecord() {
    }

    @Override
    public String toJSONString() {
        try{
            data.put("type","REPL");
            data.put("data","here!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }
}
