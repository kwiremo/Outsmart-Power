package com.outsmart.outsmartpower.Support;

/**
 * Created by Rene Moise on 2/17/2017.
 */

import android.content.Context;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *  This class does multiple functions that does not necessarily have to be included in a class.
 */
public class Utilities {
    private static Utilities ourInstance = new Utilities();
    public static Utilities getInstance() {
        return ourInstance;
    }

    private Utilities() {
    }

    public String getLocalIpAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void getAvailableWIFI()
    {
       // WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }
}
