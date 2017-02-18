package com.outsmart.outsmartpower;

import android.support.annotation.NonNull;

import com.outsmart.outsmartpower.Support.Constants;

import org.json.JSONStringer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by christian.wagner on 1/14/17.
 *
 * '-' denotes private, while '+' denotes public
 *
 * Description: This class is made to represent a smart outlet.
 *
 * Fields:
 * - List<Plug> plugs: A list of plugs associated with this Smart Outlet
 * - String nickname: The name that the user will see for this outlet.
 * - String ssid: The SSID broadcasted by this Smart Outlet
 * - String password: The password to connect to the Smart Outlet's broadcasted SSID
 * - String ipAddress: The most recent IP address for the smart outlet
 * - boolean active: True means this smart outlet is actively communicating with the android application
 * - DatabaseOperations database: The power profile database associated with this smart outlet
 *
 * Methods:
 * + smartOutlet(String password, String ssid) - This is the constructor for the Smart Outlet Class
 * + boolean addEntry(String JSONstring) - This adds an entry into the sqlLite database associated with this Smart Outlet
 * + getPresentEntry() - Gets the latest Power profile (voltage, current, etc) from the sqlLite database associated with this Smart Outlet
 *
 */

public class SmartOutlet {


    private DatabaseOperations database; //The database associated with this smart outlet
    private OutsmartDeviceInfo smartOutletInfo;
    private int id;     //There is id in the OutsmartDeviceInfo. HOwever we are also saving it here
                        //SO that we can retrieve data of this instance with this ID.

    //Constructor for the SmartOutlet Class
    public SmartOutlet( String initialSsid, String initialPassword, String nickname, String ipAddress, int id)  {
        saveDatabaseInfo(initialSsid,initialPassword,nickname,ipAddress,id);
        this.id = id;
    }

    //Adds power profile entry to the data base associated with this Smart Outlet
    public boolean addEntry(String jsonString) {
        //Pass jsonString to DatabaseOperations class to test for

        return false;
    }

    private void saveDatabaseInfo(String initialSsid, String initialPassword, String nickname, String ipAddress, int id)
    {
        smartOutletInfo = new OutsmartDeviceInfo(nickname,initialSsid,initialPassword,ipAddress,id);
        database.addSmartOutletInfo(smartOutletInfo);
    }

    private OutsmartDeviceInfo getDatabaseInfo()
    {
        return smartOutletInfo;
    }

    //Getter for the ssid field
    public String getSsid() {
        return smartOutletInfo.getSsid();
    }

    //Getter for the password field
    public String getPassword() {
        return smartOutletInfo.getPassword();
    }

    //Getter for the nickname field
    public String getNickname() {
        return smartOutletInfo.getSsid();
    }


    public String getIpAddress() {
        return smartOutletInfo.getIpAddress();
    }
}
