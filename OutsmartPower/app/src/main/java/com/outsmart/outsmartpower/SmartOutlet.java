package com.outsmart.outsmartpower;

/**
 * Created by Rene Moise on 1/28/2017.
 */

/**
 * * '-' denotes private, while '+' denotes public
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
 */
public class SmartOutlet {

    private String nickname; //The name that the user will see for this outlet
    private String ssid; //The SSID broadcasted by this Smart Outlet
    private String password; //The password to connect to this Smart Outlet's broadcasted SSID
    private String ipAddress; //The most recent IP address for the smart outlet
    private String smart_Outlet_Device_ID;

    public SmartOutlet(String nickname, String ssid, String password,
                       String ipAddress, String smart_Outlet_Device_ID) {
        this.nickname = nickname;
        this.ssid = ssid;
        this.password = password;
        this.ipAddress = ipAddress;
        this.smart_Outlet_Device_ID = smart_Outlet_Device_ID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setSmart_Outlet_Device_ID(String smart_Outlet_Device_ID) {
        this.smart_Outlet_Device_ID = smart_Outlet_Device_ID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSsid() {
        return ssid;
    }

    public String getPassword() {
        return password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSmart_Outlet_Device_ID() {
        return smart_Outlet_Device_ID;
    }

    @Override
    public String toString() {
        return getNickname();
    }
}
