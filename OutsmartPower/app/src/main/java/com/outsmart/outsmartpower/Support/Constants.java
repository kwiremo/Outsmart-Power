package com.outsmart.outsmartpower.Support;

import com.outsmart.outsmartpower.SmartOutlet;

/**
 * Created by Rene Moise on 1/16/2017.
 */
public class Constants {
    private static Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    public static int NUMBER_OF_PLUGS = 4;  //The number of plugs on one smart outle.
    public static int BUFFER_SIZE = 4096;
    public static int SERVER_PORT = 4000;
    public static int SERVER_SLEEP_TIME = 1; //server sleep time in milliseconds.
    public String REMOTE_IP_ADDRESS;
    public static int REMOTE_PORT = 2390;

    private Constants() {

        REMOTE_IP_ADDRESS = "192.168.4.1";
    }


}
