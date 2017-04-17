package com.outsmart.outsmartpower.managers;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.ParentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.ContentValues.TAG;

/**
 * Created by Rene Moise on 3/25/2017.
 */

/**
 * The connection manager class is used to connect to a given SSID. It also configures network
 * and save them.
 */
public class ConnectionManager implements Observer{

    //Unique instance of this class.
    private static final ConnectionManager ourInstance = new ConnectionManager();
    private WifiManager wifiManager;    //It is used to get configured networks.
    WifiConfiguration wifiConfig;       //it is used to configure networks.
    private Activity parentActivity;

    /**
     * Scanned results are scanned in the wifiListFragment. This is a bad practice. This class
     * should be the one that scans the network since it is the connectionManager. This is set
     * to the list of scanned results as soon as they are received from the wifiListFragment.
     * This is open to anyone who has time to move the scanning functionality to this class.
     */
    private List<ScanResult> scannedResults;

    //Initialization.
    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        return ourInstance;
    }

    /**
     * This function takes the ssid and the password of the network. It tries connecting to it.
     * @param ssid
     * @param password
     */
    public void  connectToWifi(String ssid, String password){

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

    /**
     * A helper function that connects to a given ssid and configure it if it is not already
     * configured.
     * @param ssid
     */
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

    /**
     * A helper function that connects to a given network id.
     * @param id
     */
    private void connectToNetworkID(int id){
        wifiManager.disconnect();
        wifiManager.enableNetwork(id, true);
        wifiManager.reconnect();
    }

    /**
     * The function required to implement from the observer interface. It is called by the bootloader
     * to initialize fields.
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(BootlLoader.class)){
            parentActivity = ParentActivity.getParentActivity();
            wifiManager = (WifiManager) parentActivity.getApplicationContext().
                    getSystemService(Context.WIFI_SERVICE);
            wifiConfig = new WifiConfiguration();
            scannedResults = new ArrayList<>();
        }
    }

    /**
     * This is called by the WIfiListFragment to set up the list of the scanned networks.
     * @param scanResults
     */
    public void setScanResults(List<ScanResult> scanResults) {
        this.scannedResults = scanResults;
    }
}
