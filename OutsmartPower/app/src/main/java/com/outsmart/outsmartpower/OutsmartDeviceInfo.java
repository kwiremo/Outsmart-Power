package com.outsmart.outsmartpower;

/**
 * Created by Rene Moise on 1/28/2017.
 */

public class OutsmartDeviceInfo {
    DatabaseOperations Db = DatabaseOperations.getInstance();

    public OutsmartDeviceInfo(String nickname, String ssid, String password, String ipAddress, int smart_Outlet_Device_ID) {
        this.nickname = nickname;
        this.ssid = ssid;
        this.password = password;
        this.ipAddress = ipAddress;
        this.smart_Outlet_Device_ID = smart_Outlet_Device_ID;
    }

    private String nickname; //The name that the user will see for this outlet
    private String ssid; //The SSID broadcasted by this Smart Outlet
    private String password; //The password to connect to this Smart Outlet's broadcasted SSID
    private String ipAddress; //The most recent IP address for the smart outlet
    private int smart_Outlet_Device_ID;

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
        smart_Outlet_Device_ID = smart_Outlet_Device_ID;
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

    public int getSmart_Outlet_Device_ID() {
        return smart_Outlet_Device_ID;
    }
}
