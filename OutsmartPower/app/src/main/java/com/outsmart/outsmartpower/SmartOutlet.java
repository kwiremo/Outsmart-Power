package com.outsmart.outsmartpower;

/**
 * Created by christian.wagner on 1/14/17.
 *
 * '-' denotes private, while '+' denotes public
 *
 * Description: This class is made to represent a smart outlet.
 *
 * Fields:
 * - List<Plug>: A list of plugs associated with this Smart Outlet
 * - nickname: The name that the user will see for this outlet.
 * - ssid: The SSID broadcasted by this Smart Outlet
 * - password: The password to connect to the Smart Outlet's broadcasted SSID
 * - active: Boolean value that lets us know if this is the outlet communication with the android application
 * - sqlLite Database: The data associated with this smart outlet for storing power and status information
 *
 * Methods:
 * + smartOutlet(String password, String ssid) - This is the constructor for the Smart Outlet Class
 * + setPassword(String password) - This sets the password for the Smart Outlet object
 * + setNickname(String name) - This sets the nickname for the Smart Outlet object
 * + addEntry(String JSONstring) - This adds an entry into the sqlLite database associated with this Smart Outlet
 * + getPresentEntry() - Gets the latest Power profile (voltage, current, etc) from the sqlLite database associated with this Smart Outlet
 *
 */

public class SmartOutlet {

    private String ipAddress;

    public void setRemoteIPAddress(String remoteIPAddress)
    {
        if(remoteIPAddress == null)
            return;

        REMOTE_IP_ADDRESS = remoteIPAddress;
    }

    public void getRemoteIPAddress()
    {
        if()
    }
}
