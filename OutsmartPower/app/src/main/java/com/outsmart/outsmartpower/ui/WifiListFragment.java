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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.managers.ConnectionManager;
import com.outsmart.outsmartpower.managers.SmartOutletManager;

import java.util.ArrayList;
import java.util.List;

import static com.outsmart.outsmartpower.Support.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;

/**
 * Created by Rene Moise on 2/22/2017.
 */

/**
 * As of 4/16/2017, this fragment is owned by the mainActivity. when started, it scans for
 * available wifis. If there are available smart outlets, it asks the user for what home wifi
 * he/she is willing to use for connection (talking between phone app and smart outlet). Once the
 * user selects it, the phone app sends both home wifi and passwords to the main activity (responsible
 * for setting up a new smart outlet for now), which in turn sends the credentials to the smart
 * outlet.
 */
public class WifiListFragment extends android.app.ListFragment implements StringInputDialog.onInputButtonClicked {

    ArrayAdapter wifiListAdapter;   //Responsible to display an array list of wifi on the screen.
    private WifiManager wifiManager;    //wifimanager needed for the wifi_service class.
    //Create an instance of wifiscanreceiver to start listening for a wifi scan if requested.
    private WifiScanReceiver wifiScanReceiver;
    TextView availWifisTV;    //Looking available wifi when scanning and displays WIFI list when found.
    ArrayList<String> allAvailableNetworks; //contains all available networks
    ArrayList<String> availableWifis;   //contains a list of available wifi network.
    ArrayList<String> availableOutsmartWifi;    //Contains available outsmart hotspots.
    boolean hasFinishedScanning = false;    //Set to true if the phone app has already scanned.
    //This is true if the user has entered the password for an outsmart device.
    boolean hasEnteredOutsmartPassword = false;
    // This is true if the user has entered the password for a home wifi.
    boolean hasEnteredHomeWifiPassword = false;
    //When this button is clicked the fragment is popped off the stack thus returning back to the
    //previous fragment.
    Button cancelButton;

    /**
     * This interface is used to send homeWifiName, outsmartWifiName, homeWifiPassword,
     * outSmartWifiPassword to the mainActivity.
     */
    public interface onReceivedPreferredWifis{
        void receivePreferredWifis(String homeWifiName, String outsmartWifiName,
                                   String homeWifiPassword, String outSmartWifiPassword);
    }

    List<ScanResult> scannedResults;
    String homeWifiName = "";
    String broadSmartOutletNetw = "";
    String homeWifiPassword = "";
    String broadSmartOutletNetwPassword = "";

    //This variable is for saving the activity that used this fragment.
    Activity baseActivity;

    /**
     * On attach, we get the activity that started this fragment.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (Activity) context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize the cancel button.
        cancelButton = (Button) getActivity().findViewById(R.id.cancelListBTN);

        availWifisTV = (TextView) getView().findViewById(R.id.wifiListTitleTV);
        availWifisTV.setText("Scanning...");
        //baseActivity = getActivity();
        if(!hasFinishedScanning){
            //Check to see if we have all wifi permissions to scan device's wifi.
            if(getPermissionsNeededToScan().size() == 0 ) {
                startScanning();
            }
            else{
                UIManager.getInstance().disPlayMessage("Please go to settings " +
                        "and provide required permissions.");
                getFragmentManager().popBackStack();
            }
            hasFinishedScanning = true;
        }

        //Set listener.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
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

    //Broadcast Receiver is also needed to listen and run upon the completetio of wifi scan. The action is wifiScan.
    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            baseActivity.unregisterReceiver(wifiScanReceiver);
            //Creating references to the list of available networks.
            allAvailableNetworks = new ArrayList<>();
            availableOutsmartWifi = new ArrayList<>();
            availableWifis = new ArrayList<>();

            scannedResults = wifiManager.getScanResults();

            //Setup the scanned results.
            ConnectionManager.getInstance().setScanResults(scannedResults);

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

            if(!availableOutsmartWifi.isEmpty()) {

                wifiListAdapter = new ArrayAdapter(baseActivity,
                        android.R.layout.simple_list_item_1, availableOutsmartWifi);
                setListAdapter(wifiListAdapter);

                availWifisTV.setText("AVAILABLE OUTSMART WIFI");
            }
            else
            {
                showDialogOK("No Smart Outlets were found!\nTry again?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                startScanning();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                getFragmentManager().popBackStack();
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }
    }

    /**
     * This is a callback to the dialog fragment. Since this class implements the getPassordDialog
     * interface, it has to implement this method. The input entered on the dialog is received here.
     */
    @Override
    public void onFinishedEnteringInput(String password) {

        if(!hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            broadSmartOutletNetwPassword = password;
            hasEnteredOutsmartPassword = true;

            wifiListAdapter = new ArrayAdapter(baseActivity,
                    android.R.layout.simple_list_item_1, availableWifis);
            setListAdapter(wifiListAdapter);
            availWifisTV.setText("AVAILABLE WIFI");
        }
        else if(hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            homeWifiPassword = password;
            hasEnteredHomeWifiPassword = true;
            onReceivedPreferredWifis receivedPreferredWifis = (onReceivedPreferredWifis) getActivity();
            if(receivedPreferredWifis != null) {
                receivedPreferredWifis.receivePreferredWifis(homeWifiName, broadSmartOutletNetw, homeWifiPassword,
                       broadSmartOutletNetwPassword);
                baseActivity.getFragmentManager().popBackStack();
            }
        }
    }

    /**
     * This is a callback that listens to an item click.
     * @param l
     * @param v
     * @param position
     * @param id
     */
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
                                baseActivity.onBackPressed();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                baseActivity.onBackPressed();
                                break;
                            default:
                                break;
                        }
                    }
                });
                return;
            }
            else{
                StringInputDialog dialog = new StringInputDialog();
                dialog.setTargetFragment(this,1);
                dialog.setCancelable(false);
                dialog.show(baseActivity.getFragmentManager(),null);
            }
        }
        else if(hasEnteredOutsmartPassword && !hasEnteredHomeWifiPassword){
            StringInputDialog dialog = new StringInputDialog();
            dialog.setCancelable(false);
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

    /**
     * This is also used in mainActivity. The improvement would be handling all the listening
     * operations in the Connection Manager.
     * @return
     */
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

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(baseActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    /**
     * Not used: It would be used to display a message to the user.
     */
    private void displayNoAvailableOutsmart(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle("No Available Smart Outlets");
        builder1.setMessage("No Smart Outlets were found!\nPlease try again");
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
