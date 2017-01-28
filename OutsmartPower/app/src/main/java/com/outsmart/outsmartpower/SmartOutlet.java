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

    private List<Plug> plugs; //A list of the plugs associated with this Smart Outlet
    private String nickname; //The name that the user will see for this outlet
    private String ssid; //The SSID broadcasted by this Smart Outlet
    private String password; //The password to connect to this Smart Outlet's broadcasted SSID
    private String ipAddress; //The most recent IP address for the smart outlet
    private boolean active; //True means this smart outlet is actively communicating with the android application
    private DatabaseOperations database; //The database associated with this smart outlet

    //Constructor for the SmartOutlet Class
    public SmartOutlet( String initialSsid, String initialPassword, String nickname) {

        //Set the initial ssid and password of this Smart outlet
        setSsid(initialSsid);
        setPassword(initialPassword);
    }

    //Adds power profile entry to the data base associated with this Smart Outlet
    public boolean addEntry(String jsonString) {
        //Pass jsonString to DatabaseOperations class to test for

        return false;
    }

    //Getter for the ssid field
    public String getSsid() {
        return ssid;
    }

    //Setter for the ssid field
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    //Getter for the password field
    public String getPassword() {
        return password;
    }

    //Setter for the password field
    public void setPassword(String password) {
        this.password = password;
    }

    //Getter for the nickname field
    public String getNickname() {
        return nickname;
    }

    //Setter for the nickname field
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    //Getter for the active field
    public boolean isActive() {
        return active;
    }

    //Setter for the active field
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
