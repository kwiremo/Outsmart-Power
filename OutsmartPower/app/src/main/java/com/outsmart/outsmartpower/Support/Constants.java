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
    public static final String SMART_OUTLET_ID = "smartOutletId";


    //OUTSMART DEVICE COLUMNS.
    public static final String DEVICE_TABLE_NAME = "smart_outlet_information";
    public static final String DEVICE_RECORD_ID = "device_record_id";
    public static final String DEVICE_ID = "device_ID";
    public static final String DEVICE_SSID = "device_ssid";
    public static final String DEVICE_PASSWORD = "device_password";
    public static final String IP_ADDRESS = "IP_Address";
    public static final String DEVICE_NAME = "device_name";

    //IP AdDRESS TABLE
    public static final String IPADDRESS_NAME = "ip_address_table";
    public static final String IP_ADDRESS_COL = "ip_address_table";

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

    //PROTOCOL keys

    //RECORD TYPES
    /**
     * I have made a simple protocol consisting of 6 types of packets that will be being exchanged and they will sent as json information.
     * As a part of a protocol, I have also included delimiters that will be used to extract specific information in a given type.
     * This is a protocol and definition:
     * Example:
     1. Credentials: type:CRED,p:12345678, s:Outsmart 288 where p is password and s is ssid.
     2. Control: type: type:CONT,p:1 where p means toggle plug number the value given. in this case plug 1.
     3. Power BaseRecord: check 11/09/2016 for an example.
     4. Echo Request: type:REQU, ip:10.30.10.138: Where ip is the ipp address of the sender.
     5. ip the ip address of the sender.
     6. ip the ip address of the sender.
     */
    public static final String CRED_RECORD = "CRED"; //credential record
    public static final String CONT_RECORD = "CONT";    //control record
    public static final String PORE_RECORD = "PORE"; //power record
    public static final String REQU_RECORD = "REQU";   //echo request record
    public static final String REPL_RECORD = "REPL";    //reply record
    public static final String IPAD_RECORD = "IPAD";    //IP address record.

    public static final String TYPE_LABEL = "type";
    public static final String OUTLET_TO_TOGGLE_LABEL = "toggle";

    public static final String IP_CONTENT = "ip";
    public static final String ID_CONTENT = "id";
}
