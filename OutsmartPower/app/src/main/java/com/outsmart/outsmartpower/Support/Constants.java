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
    public static String REMOTE_IP_ADDRESS = "192.168.4.1";
    public static int REMOTE_PORT = 2390;
    public static final String DATABASE_NAME = "outsmart";  //database name


    //RECORD table COLUMNS
    public static final String RECORD_TABLE_NAME = "record_table_name";
    public static final String RECORD_ID = "record_id";
    //public static final String DATE = "d";
    //public static final String TIME = "time";
    public static final String SECONDS = "t";
    public static final String CURRENT_1 = "c1";
    public static final String CURRENT_2 = "c2";
    public static final String CURRENT_3 = "c3";
    public static final String CURRENT_4 = "c4";
    public static final String VOLTAGE = "v";
    public static final String OUTSMART_DEVICE_ID = "outsmart_device_id";


    //OUTSMART DEVICE COLUMNS.
    public static final String DEVICE_TABLE_NAME = "smart_outlet_information";
    public static final String DEVICE_RECORD_ID = "device_record_id";
    public static final String DEVICE_ID = "device_ID";
    public static final String DEVICE_SSID = "device_ssid";
    public static final String DEVICE_PASSWORD = "device_password";
    public static final String IP_ADDRESS = "IP_Address";
    public static final String DEVICE_NAME = "device_name";

    //SETTINGS TABLE COLUMNS
    public static final String SETTINGS_TABLE_NAME = "settings_table_name";
    public static final String COST = "cost_kwh";
    public static final String DATE_FORMAT = "date_format";
    public static final String TIME_FORMAT = "time_format";
    public static final String UNIT_PREFERENCE = "unit_preference";


    //DATE MANAGER Constants.
    public static final int MONTH_DAYS = 30;
    public static final int WEEK_DAYS = 7;
    public static final int DAY_SECONDS = 86400;


    //PERMISSIONS
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private Constants() {
        REMOTE_IP_ADDRESS = "192.168.4.1";
    }

    //PASSWORDS
    public  static final String PASWWPRD_KEYWORD = "OutSmart";
}
