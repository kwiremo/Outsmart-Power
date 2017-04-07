package com.outsmart.outsmartpower;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.managers.ConnectionManager;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.network.UDPServer;
import com.outsmart.outsmartpower.records.ControlRecord;
import com.outsmart.outsmartpower.records.CredentialBaseRecord;
import com.outsmart.outsmartpower.records.EchoRequestRecord;
import com.outsmart.outsmartpower.ui.DisplayPowerFragment;
import com.outsmart.outsmartpower.ui.UIManager;
import com.outsmart.outsmartpower.ui.WifiListFragment;
import com.outsmart.outsmartpower.ui.GetNickNameDialog;

import java.util.List;

import static android.content.ContentValues.TAG;
/*
 *Name: MainActivity Class
 *
 * Description: This class is the activity that is responsible for setting up the application, as
 * well as handling events that occur throughout the life of the application.
 */

public class MainActivity extends AppCompatActivity implements WifiListFragment.onReceivedPreferredWifis,
    //This line allows us to use the Navigation Drawer
        NavigationView.OnNavigationItemSelectedListener, UDPServer.reportOutsmartCred, GetNickNameDialog.onInputButtonClicked{

    //This method is called when the app is first started. It sets up everything the application
    //needs in order to run.

    /**
     * This variable is a broadcast receiver. It receives the connection status when wifi switches.
     * It is initialized when the list of wifis list is received. At this point it is ready
     * for a new connection. Once the onReceive is received
     */
    ConnectedReceived received;

    //These fields are used to setup a new outsmart device.
    String nickname;
    String ssid;
    String password;
    String ipAddress;
    String smart_Outlet_Device_ID;
    String homeWifiName;
    String homeWifiPassword;
    List<ScanResult> scannedResults;
    UDPManager udpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Start display Power Fragment
        fragmentTransaction.replace(R.id.wifiListFragmentContainer,new DisplayPowerFragment());
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO This will be the 'add' button on the main page of the UI
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UDPManager.getInstance().sendPacket(new EchoRequestRecord(), Constants.REMOTE_IP_ADDRESS);
                UDPManager.getInstance().sendPacket(new ControlRecord("on2"),
                        Constants.REMOTE_IP_ADDRESS);
            }
        });


        //This is the instance of the Navigation Drawer for the UI
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //This is the instance of the Action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //This is the instance of the list inside of the navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //This sets the listener for navigation drawer selections to the main activity so that we
        //can handle selections of the navigation options
        navigationView.setNavigationItemSelectedListener(this);
        //Bootloader has to setup everything first.

        BootlLoader bootlLoader = new BootlLoader(this);
        udpManager = UDPManager.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This method closes the navigation drawer when the back button is pressed if it is open,
    //otherwise it just excutes a normal onBackPressed()
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //OnNavigationItemSelected is the handler for when nagivation drawer items are selected. It
    //replaces the view and executes an action depending on which item was pressed
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_smart_outlet_list) {
            UIManager.getInstance().displayAvailableSmartOutlet();
        } else if (id == R.id.nav_setup) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            android.app.ListFragment fragment = new WifiListFragment();
            fragmentTransaction.replace(R.id.wifiListFragmentContainer,fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } */ else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //This closes the navigation drawer becuase we've selected an item
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void receivePreferredWifis(String homeWifiName, String outsmartWifiName,
                                      String homeWifiPassword, String outSmartWifiPassword,
                                      List<ScanResult> scannedResults) {
        this.scannedResults =scannedResults;

        //Set the first arguments for a new device.
        ssid = outsmartWifiName;
        password = outSmartWifiPassword;

        //Create a receiver for the connected action.
        received = new ConnectedReceived();

        this.homeWifiName = homeWifiName;
        this.homeWifiPassword = homeWifiPassword;

        //Get the connectivity manager.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Create a filter for to connectivity action.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(cm.CONNECTIVITY_ACTION);

        //Register the receiver.
        registerReceiver(received,intentFilter);

        ConnectionManager.getInstance().connectToWifi(outsmartWifiName, outSmartWifiPassword,
                scannedResults);
    }

    @Override
    public void onOutsmartCredReceived(String id, String ip) {
        smart_Outlet_Device_ID = id;
        ipAddress = ip;

        /**
         *  Now that we are have id, and ip from the UDP Server,
         *  And that we have Outsmart Name and password from the WIFIListFragment
         *  We are going to request a nickname for this outsmart.
         *  The input from the user will be received rom the onFinishedEnteringInput.
         */

        GetNickNameDialog dialog = new GetNickNameDialog();
        dialog.show(getFragmentManager(),null);
    }

    @Override
    public void onFinishedEditDialog(String userInput) {
        if(userInput != null && !userInput.equals("")){
            nickname = userInput;
        }
        else
            nickname = ssid;
        /**
         * At this point we have everything we need to save a new outsmart device.
         */
        SmartOutlet smartOutlet = new SmartOutlet(nickname,ssid,
                password,ipAddress,smart_Outlet_Device_ID);

        SmartOutletManager.getInstance().saveSmartOutlet(smartOutlet);


        //TODO: Create global references for connectivity manager and filters. Also fix the arguments of connectToWifiBelow:
        //Get the connectivity manager.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Create a filter for to connectivity action.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(cm.CONNECTIVITY_ACTION);

        //Register the receiver.
        registerReceiver(received,intentFilter);

        ConnectionManager.getInstance().connectToWifi(homeWifiName, homeWifiPassword,
                scannedResults);
    }

    private class ConnectedReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (action.equals(cm.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // Wifi is connected
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();

                    //This is when we just got connected to the smart-outlet network
                    if (ssid.contains(Constants.PASWWPRD_KEYWORD)) {    //Used contains rather than equals because the ssid returned has extra quotes that I am not sure why
                        //they are added. Change it to equals if you can fix it.
                        Log.e(TAG, " -- Wifi connected --- " + " SSID " + ssid);
                        CredentialBaseRecord credentialRecord = new CredentialBaseRecord(homeWifiName, homeWifiPassword);
                        udpManager.sendPacket(credentialRecord, Constants.REMOTE_IP_ADDRESS);
                        unregisterReceiver(received);
                    }

                    //This is when we just got connected to the home-wifi.
                    else if(ssid.contains(homeWifiName)){
                        SmartOutlet deviceInfo = SmartOutletManager.getInstance().getActiveSmartOutlet();
                        if(deviceInfo != null){
                            String ipAddress =deviceInfo.getIpAddress();
                            udpManager.sendPacket(new EchoRequestRecord(), ipAddress);
                            unregisterReceiver(received);
                        }
                    }
                }
            }

        }
    }
}
