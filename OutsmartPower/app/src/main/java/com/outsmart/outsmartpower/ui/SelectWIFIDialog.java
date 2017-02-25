package com.outsmart.outsmartpower.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.outsmart.outsmartpower.Support.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;

/**
 * Created by Rene Moise on 2/25/2017.
 */

public class SelectWIFIDialog extends DialogFragment {

    ArrayAdapter wifiListAdapter;   //Responsible to display an array list of wifi on the screen.
    //ListView Lv;    //The list view on the screen that will display a list of wifi.
    private WifiManager wifiManager;    //wifimanager needed for the wifi_service class.
    //Create an instance of wifiscanreceiver to start listening for a wifi scan if requested.
    private WifiScanReceiver wifiScanReceiver;

    private TextView title;
    private ListView wifiList;

    public SelectWIFIDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_wifi_dialog,container,false);

        //parentActivity = ParentActivity.getParentActivity();
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        title = (TextView) rootView.findViewById(R.id.wifi_title_dialog_tv);
        wifiList = (ListView) rootView.findViewById(R.id.wifi_list_dialog_lv);

        getDialog().setTitle("WIFI LIST");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check to see if we have all wifi permissions to scan device's wifi.
        if(getPermissionsNeededToScan().size() == 0)
        {
            startScanning();
        }
        else{
            requestPermissionsFromTheUser(getPermissionsNeededToScan());
        }
    }

    private void startScanning() {
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE); //get a wifi_service
        wifiScanReceiver = new WifiScanReceiver();      //Create and save a reference to the broadcastreceiver.

        //In order to scan a list of wireless networks, you also need to register your
        // BroadcastReceiver. It is given a broadcastreceiver and an action on which it will be called.
        getActivity().registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //before scanning, ensure that wifi is enabled.
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        //Now that we are read to scan, start scanning.
        wifiManager.startScan();
    }

    private void requestPermissionsFromTheUser(List<String> listPermissionsNeeded) {
        ActivityCompat.requestPermissions(getActivity(),
                listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    //Broadcast Receiver is also needed to listen and run upon the completetio of wifi scan. The action is wifiScan.
    private class WifiScanReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> scannedResultSring = new ArrayList<>();
            List<ScanResult> scannedResults = wifiManager.getScanResults();

            for(ScanResult scanResult: scannedResults){
                if(!(scannedResultSring.contains(scanResult.SSID)))
                    scannedResultSring.add(scanResult.SSID);
            }

            if(!scannedResults.isEmpty()) {
                wifiListAdapter = new ArrayAdapter(getActivity(),
                        android.R.layout.simple_list_item_1, scannedResultSring);

                wifiList.setAdapter(wifiListAdapter);
                title.setText("AVAILABLE WIFI");
            }
            else
                title.setText("No Available Wifi");
        }
    }

    private List<String> getPermissionsNeededToScan()
    {
        int permissionAcessCoarseLocation = ContextCompat.checkSelfPermission(getActivity(),
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
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
                            Toast.makeText(getActivity(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
