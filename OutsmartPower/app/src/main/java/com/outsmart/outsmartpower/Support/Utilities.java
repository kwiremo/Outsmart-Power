package com.outsmart.outsmartpower.Support;

/**
 * Created by Rene Moise on 2/17/2017.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.content.ContentValues.TAG;

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

    public void  connectToWifi(String ssid, String password, List<ScanResult> scannedResults, WifiManager wifiManager){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);


        for (ScanResult network : scannedResults)
        {
            //check if current connected SSID
            if (ssid.equals(network.SSID)){
                //get capabilities of current connection
                String Capabilities =  network.capabilities;
                Log.d (TAG, network.SSID + " capabilities : " + Capabilities);

                if (Capabilities.contains("WPA2")) {
                    wifiConfig.preSharedKey = "\""+ password +"\"";
                }
                else if (Capabilities.contains("WPA")) {
                    wifiConfig.preSharedKey = "\""+ password +"\"";
                }
                else if (Capabilities.contains("WEP")) {
                    wifiConfig.wepKeys[0] = "\"" + password + "\"";
                    wifiConfig.wepTxKeyIndex = 0;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                }
                else
                {
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                break;
            }
        }

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
