package com.outsmart.outsmartpower.managers;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Rene Moise on 3/25/2017.
 */

public class ConnectionManager {
    private static final ConnectionManager ourInstance = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return ourInstance;
    }

    private ConnectionManager() {
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
