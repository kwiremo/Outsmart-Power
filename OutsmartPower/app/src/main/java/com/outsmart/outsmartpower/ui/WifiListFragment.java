package com.outsmart.outsmartpower.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.records.CredentialBaseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.content.ContentValues.TAG;
import static com.outsmart.outsmartpower.Support.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;

/**
 * Created by Rene Moise on 2/22/2017.
 */

public class WifiListFragment extends android.app.ListFragment implements StringInputDialog.onInputButtonClicked {

    ArrayAdapter wifiListAdapter;   //Responsible to display an array list of wifi on the screen.
    //ListView Lv;    //The list view on the screen that will display a list of wifi.
    private WifiManager wifiManager;    //wifimanager needed for the wifi_service class.
    //Create an instance of wifiscanreceiver to start listening for a wifi scan if requested.
    private WifiScanReceiver wifiScanReceiver;
    TextView Tv;    //Looking available wifi when scanning and displays WIFI list when found.

    ArrayList<String> allAvailableNetworks; //contains all available networks
    ArrayList<String> availableWifis;   //contains a list of available wifi network.
    ArrayList<String> availableOutsmartWifi;    //Contains available outsmart hotspots.

    boolean hasFinishedScanning = false;    //Set to true if the phone app has already scanned.
    /**
     *     This is true if the user has entered the password for an outsmart device.
     */
    boolean hasEnteredOutsmartPassword = false;

    /**
     *     This is true if the user has entered the password for a home wifi.
     */
    boolean hasEnteredHomeWifiPassword = false;

    public interface onReceivedPreferredWifis{
        void receivePreferredWifis(String homeWifiName, String outsmartWifiName, String homeWifiPassword, String outSmartWifiPassword,
                                   List<ScanResult> scannedResults);
    }

    List<ScanResult> scannedResults;

    String homeWifiName = "";
    String broadSmartOutletNetw = "";
    String homeWifiPassword = "";
    String broadSmartOutletNetwPassword = "";

    //This variable is for saving the activity that used this fragment.
    Activity baseActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Tv = (TextView) getView().findViewById(R.id.wifiListTitleTV);
        Tv.setText("Scanning...");
        //baseActivity = getActivity();
        if(!hasFinishedScanning){
            //Check to see if we have all wifi permissions to scan device's wifi.
            if(getPermissionsNeededToScan().size() == 0 )
            {
                startScanning();
            }
            else{
                requestPermissionsFromTheUser(getPermissionsNeededToScan());
            }
            hasFinishedScanning = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_list, container,false);
        return view;
    }

    private void startScanning() {
        wifiManager = (WifiManager) baseActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();      //Create and save a reference to the broadcastreceiver.

        //In order to scan a list of wireless networks, you also need to register your
        // BroadcastReceiver. It is given a broadcastreceiver and an action on which it will be called.
        baseActivity.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //before scanning, ensure that wifi is enabled.
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        //Now that we are read to scan, start scanning.
        wifiManager.startScan();
    }

    private void requestPermissionsFromTheUser(List<String> listPermissionsNeeded) {
        ActivityCompat.requestPermissions(baseActivity,
                listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    //Broadcast Receiver is also needed to listen and run upon the completetio of wifi scan. The action is wifiScan.
    private class WifiScanReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
                //Creating references to the list of available networks.
                allAvailableNetworks = new ArrayList<>();
                availableOutsmartWifi = new ArrayList<>();
                availableWifis = new ArrayList<>();

               scannedResults = wifiManager.getScanResults();

                for(ScanResult scanResult: scannedResults){
                    {
                        if (!(allAvailableNetworks.contains(scanResult.SSID))) {
                            allAvailableNetworks.add(scanResult.SSID);
                        }
                    }
                }

                //Save both outsmart hotspots and other wifis.
                for(String wifi: allAvailableNetworks){
                    if(wifi.contains(Constants.PASWWPRD_KEYWORD))
                        availableOutsmartWifi.add(wifi);
                    else
                        availableWifis.add(wifi);
                }

                if(!scannedResults.isEmpty()) {

                    wifiListAdapter = new ArrayAdapter(baseActivity,
                            android.R.layout.simple_list_item_1, availableOutsmartWifi);
                    setListAdapter(wifiListAdapter);

                    Tv.setText("AVAILABLE OUTSMART WIFI");
                }
                else
                    Tv.setText("No OutSmart Device Found.");
            }
        }

    //This is a callback to the dialog fragment. Since this class implements the getPassordDialog
    //interface, it has to implement this method. The input entered on the dialog is received here.

    @Override
    public void onFinishedEnteringInput(String password) {

        if(!hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            broadSmartOutletNetwPassword = password;
            hasEnteredOutsmartPassword = true;

            wifiListAdapter = new ArrayAdapter(baseActivity,
                    android.R.layout.simple_list_item_1, availableWifis);
            setListAdapter(wifiListAdapter);
            //Lv.setAdapter(wifiListAdapter);
            Tv.setText("AVAILABLE WIFI");
            //Switch to Smart Outlet network
            //switchNetwork(broadSmartOutletNetw,broadSmartOutletNetwPassword);
        }
        else if(hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            homeWifiPassword = password;
            hasEnteredHomeWifiPassword = true;
            onReceivedPreferredWifis receivedPreferredWifis = (onReceivedPreferredWifis) getActivity();
            if(receivedPreferredWifis != null) {
                // Send Credential Packet to Smart Outlet being setup
                //UDPManager.getInstance().sendPacket(new CredentialBaseRecord(homeWifiName, homeWifiPassword), Constants.REMOTE_IP_ADDRESS);

                receivedPreferredWifis.receivePreferredWifis(homeWifiName, broadSmartOutletNetw, homeWifiPassword,
                       broadSmartOutletNetwPassword, scannedResults);
                baseActivity.unregisterReceiver(wifiScanReceiver);
                baseActivity.getFragmentManager().popBackStack();
                //Switch to Smart Outlet Network
                //switchNetwork(homeWifiName,homeWifiPassword);
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if(!hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword)
        {
            broadSmartOutletNetw = availableOutsmartWifi.get(position);

            if(SmartOutletManager.getInstance().isRegistered(broadSmartOutletNetw)){
                showDialogOK("Would you like to remove it?",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                SmartOutletManager.getInstance().removeOutlet(broadSmartOutletNetw);
                                UIManager.getInstance().disPlayMessage(
                                        broadSmartOutletNetw + " was removed");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                        baseActivity.getFragmentManager().popBackStack();
                        return;
                    }
                });
            }
            else{
                StringInputDialog dialog = new StringInputDialog();
                dialog.setTargetFragment(this,1);
                dialog.show(baseActivity.getFragmentManager(),null);
            }
        }
        else if(hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            StringInputDialog dialog = new StringInputDialog();
            dialog.setTargetFragment(this,1);
            dialog.show(baseActivity.getFragmentManager(),null);

            homeWifiName = availableWifis.get(position);
        }
        else
        {
            //If It gets here there is a problem. Go back and try again!
            getActivity().getFragmentManager().popBackStack();
        }
    }

    private List<String> getPermissionsNeededToScan()
    {
        int permissionAcessCoarseLocation = ContextCompat.checkSelfPermission(baseActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionAcessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return listPermissionsNeeded;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("PERMISSIONS", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
                    {
                        Log.d(TAG, "ACCESS_COARSE_LOCATION GRANTED");
                        startScanning();    //Start scanning.
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showDialogOK("COARSE_Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    requestPermissionsFromTheUser(getPermissionsNeededToScan());
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(baseActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(baseActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    //Method to automatically switch networks
    private void switchNetwork(String ssidToSwitchTo, String passwordToUse){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssidToSwitchTo + "\"";
        conf.preSharedKey = "\"" + passwordToUse + "\"";
        WifiManager wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssidToSwitchTo + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }
}
