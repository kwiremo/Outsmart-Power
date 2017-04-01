package com.outsmart.outsmartpower.managers;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.outsmart.outsmartpower.Support.ParentActivity;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Rene Moise on 3/25/2017.
 */

public class ConnectionManager {
    private static final ConnectionManager ourInstance = new ConnectionManager();
    private WifiManager wifiManager;
    WifiConfiguration wifiConfig;
    private Activity parentActivity = ParentActivity.getParentActivity();

    public static ConnectionManager getInstance() {
        return ourInstance;
    }

    private ConnectionManager() {
        wifiManager = (WifiManager) parentActivity.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
        wifiConfig = new WifiConfiguration();
    }

    //TODO: Fix the arguments of this method.
    public void  connectToWifi(String ssid, String password, List<ScanResult> scannedResults){

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
        connectAndConfigureNetw(ssid);
    }

    private void connectAndConfigureNetw(String ssid){
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                connectToNetworkID(i.networkId);
                return;
            }
        }

        int newNetworkId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(newNetworkId, true);
        wifiManager.saveConfiguration();
        wifiManager.setWifiEnabled(true);
    }

    private void connectToNetworkID(int id){
        wifiManager.disconnect();
        wifiManager.enableNetwork(id, true);
        wifiManager.reconnect();
    }
}
