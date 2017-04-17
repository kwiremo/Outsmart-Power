package com.outsmart.outsmartpower.Activities;
import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.managers.ConnectionManager;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.records.CredentialBaseRecord;
import com.outsmart.outsmartpower.records.EchoRequestRecord;
import com.outsmart.outsmartpower.ui.DisplayPowerFragment;
import com.outsmart.outsmartpower.ui.SettingsActivity;
import com.outsmart.outsmartpower.ui.UIManager;
import com.outsmart.outsmartpower.ui.WifiListFragment;
import com.outsmart.outsmartpower.ui.GetNickNameDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.outsmart.outsmartpower.Support.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;
/**
 * Created by Rene Moise Kwibuka
 *
 *Name: MainActivity Class
 *
 * Description: This class is the activity that is responsible for setting up the application, as
 * well as handling events that occur throughout the life of the application. It owns the navigation
 * bar and the floating + button on the main page. As a consequence, it handles all the clicks made
 * on these views. In addition to start the whole application by calling the bootlaoder, it also
 * launch the list of the available smartoutlets on request. The list is shown in a fragment.
 * It is also used to setup a new outlet. When setup new from the navigation drawer is clicked,
 * a new fragment is started that scans available smart outlets and available wifis to allow
 * the user to setup a new smart outlet. This class also starts a mainContent fragment. this acts as
 * the main page. It is not added on the stack.It means that you can not pop this fragment off the
 * stack. Also means that the backbutton on this page will result in leaving the application.
 * However, all other fragments are added on the stacks and can be popped. Something also worth
 * mentioning is that when a button that starts a new fragment is clicked, before starting a new
 * fragment, we pop all the current fragments (obviously not the main content fragment because it
 * is not added on the stack.
 *
 */

/**
 * This class implements the onReceivedPreferredWifis so the function that it implements can be
 * called to receive the wifis and passwords selected on the wifilistfragment fragment.
 *
 * It implements onNavigationItemSelectedListener to handle navigation drawer clicks.
 *
 * Also, when the UDPManager receives a setup packet with ipAddress and an outsmart id, it calls
 * the function implemented by the mainActivity to pass it those variables. They are in turn
 * used to create a new smartOutlet info. (this operation can be moved to a different class).
 *
 * The nickname Dialog gets the nickname from the user and is used to set the nickname of the
 * being-created smart outlet.
 */
public class MainActivity extends AppCompatActivity implements WifiListFragment.onReceivedPreferredWifis,
        NavigationView.OnNavigationItemSelectedListener, UDPManager.reportOutsmartCred,
        GetNickNameDialog.onInputButtonClicked{

    /**
     * It is not used. See the connectedReceived comments to know why.
     * This variable is a broadcast receiver. It receives the connection status when wifi switches.
     * It is initialized when the list of wifis list is received. At this point it is ready
     * for a new connection. Once the onReceive is received
     */
    ConnectedReceived received;

    /**
     * Received Credentials variable is being used to prevent showing dialogs more than once asking
     * the nickname of the smart-outlet to be saved. Note that it is possible to receive more than
     * one packet for setting up since we also send the setup packets every 2 seconds.
     */
    boolean waitingForCredentials;

    //These fields are used to setup a new outsmart device.
    String nickname;
    String ssid;
    String password;
    String ipAddress;
    String smart_Outlet_Device_ID;
    String homeWifiName;
    String homeWifiPassword;

    //The udpManager is used to send setup packets
    UDPManager udpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Display the main content page.
         */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wifiListFragmentContainer,new DisplayPowerFragment());
        fragmentTransaction.commit();

        //Make sure we have permissions that the phone app need.
        if(getPermissionsNeededToScan().size() == 0 ) {
            //I already have permission, continue.
        }
        else{
            //Else, request the user the permissions needed for the app to function properly.
            requestPermissionsFromTheUser(getPermissionsNeededToScan());
        }

        //Initialize the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Used to setup a new smart Outlet.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }

                fragmentTransaction.replace(R.id.wifiListFragmentContainer,new WifiListFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        /**
         * Setup all other views.
         */
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

        //Bootloader has to setup most of observers and notify that everthing is ready to go.
        BootlLoader bootlLoader = new BootlLoader(this);
        udpManager = UDPManager.getInstance();

        //This is set so that we do not ask the user for a nickname multiple times. See where it is
        //used.
        waitingForCredentials = false;
    }

    /**
     * Inflate the menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle the item clicks.
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item
     * @return
     *
     * This function should start a settings page. It is not fully implemented now.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

        /**
         * If any item on the navigation bar is selected, pop off any fragment that was already
         * running. This so that no fragments overlaps. When they are all on the stack, going
         * back to the main page may require the user to click the backbutton over and over again.
         */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        if (id == R.id.nav_smart_outlet_list) {

            UIManager.getInstance().displayAvailableSmartOutlet();
        } else if (id == R.id.nav_setup) {
            fragmentTransaction.replace(R.id.wifiListFragmentContainer,new WifiListFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }

        //This closes the navigation drawer becuase we've selected an item
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This is a listener to the wifiListFragment. It returns the following values.
     * @param homeWifiName
     * @param outsmartWifiName
     * @param homeWifiPassword
     * @param outSmartWifiPassword
     *
     * They are all used in the process of creating a new outsmart.
     */
    @Override
    public void receivePreferredWifis(String homeWifiName, String outsmartWifiName,
                                      String homeWifiPassword, String outSmartWifiPassword){
        //Set the first arguments for a new device.
        ssid = outsmartWifiName;
        password = outSmartWifiPassword;

        //Create a receiver for the connected action.
        received = new ConnectedReceived();

        this.homeWifiName = homeWifiName;
        this.homeWifiPassword = homeWifiPassword;

        /**
         * once I have both the outsmart wifi name and the password, I try to connect.
         * I then start a job that runs every second sending setup packets.
         */
        ConnectionManager.getInstance().connectToWifi(outsmartWifiName, outSmartWifiPassword);

        udpManager.startTimerSendingSetupPackets(new CredentialBaseRecord
                (homeWifiName, homeWifiPassword), Constants.REMOTE_IP_ADDRESS);

        UIManager.getInstance().disPlayMessage("Connecting ... \n Please  wait for 5s to 10 s");
        waitingForCredentials = true;

        /**
         *  This code was used to register for wifi change listeners. However because of the reason
         *  that I describe above the connectionReceiver class, I am not using.
         //Get the connectivity manager.
         //ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         //Create a filter for to connectivity action.
         //IntentFilter intentFilter = new IntentFilter();
         //intentFilter.addAction(cm.CONNECTIVITY_ACTION);

         //Register the receiver.
         //registerReceiver(received,intentFilter);

         */
    }

    /**
     * This is called by the UDPManager passing the Ipaddress and the id of the smart-outelt.
     * Once we receive this, we have all the information we need to successfully talk to the
     * remote smart outlet.
     * @param id
     * @param ip
     */
    @Override
    public void onOutsmartCredReceived(String id, String ip) {

        /**
         * If waitingFroCredentials is set to true, it means we are still waiting for the remote
         * smart outlet to send us the setup packet. After receiving it, this variable is set
         * to false. Measing that all duplicates will be ignored. It is again setup to true
         * when the user start the setup process.
         */
        if(waitingForCredentials) {
            smart_Outlet_Device_ID = id;
            ipAddress = ip;
            waitingForCredentials = false;

            /**
             *  Now that we are have id, and ip from the UDP Server,
             *  And that we have Outsmart Name and password from the WIFIListFragment
             *  We are going to request a nickname for this outsmart.
             *  The input from the user will be received rom the onFinishedEnteringInput.
             */

            if (id != "0") {
                GetNickNameDialog dialog = new GetNickNameDialog();
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), null);
            } else {
                UIManager.getInstance().disPlayMessage("Setup failed! Please try again");
            }
        }
    }

    /**
     * THis is called when the user finishes entering the nickname of the being-setup smartoutlet
     * If the user does not enter a nickname, the nickname is setup to its ssid.
     * @param userInput
     */
    @Override
    public void onFinishedEditDialog(String userInput) {

        if(userInput != null && !userInput.equals(""))
            nickname = userInput;
        else
            nickname = ssid;

        /**
         * At this point we have everything we need to save a new outsmart device.
         */
        SmartOutlet smartOutlet = new SmartOutlet(nickname,ssid,
                password,ipAddress,smart_Outlet_Device_ID);

        //Switch back to the home wifi.
        ConnectionManager.getInstance().connectToWifi(homeWifiName, homeWifiPassword);

        //Save the smartOutlet.
        SmartOutletManager.getInstance().saveSmartOutlet(smartOutlet);

        /**
         * This was used to listen to the change of network when we switch back to eagles.
         //Get the connectivity manager.
         //ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         //Create a filter for to connectivity action.
         //IntentFilter intentFilter = new IntentFilter();
         //intentFilter.addAction(cm.CONNECTIVITY_ACTION);

         //Register the receiver.
         //registerReceiver(received,intentFilter);
         */

    }

    /**
     * This broadcast recever class was intended to be used to listen to network change. On the
     * network change, I wanted to send a setup packet to the smart outlet and start the setup
     * process. However, I either didn't implement it well or it was totally unreliable.
     * I replaced it by starting a task that runs every 2 seconds and sends the setup packets
     * to the remote until the setup is successful or fails.
     */
    private class ConnectedReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (action.equals(cm.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    //Wifi is connected
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();

                    //This is when we just got connected to the smart-outlet network
                    if (ssid.contains(Constants.PASWWPRD_KEYWORD)) {    //Used contains rather than equals because the ssid returned has extra quotes that I am not sure why
                        //they are added. Change it to equals if you can fix it.
                        Log.e(TAG, " -- Wifi connected --- " + " SSID " + ssid);
                        //CredentialBaseRecord credentialRecord = new CredentialBaseRecord(homeWifiName, homeWifiPassword);
                            //udpManager.startTimerSendingSetupPackets(credentialRecord, Constants.REMOTE_IP_ADDRESS);
                        unregisterReceiver(received);
                    }

                    //This is when we just got connected to the home-wifi.
                    else if(ssid.contains(homeWifiName)){
                        SmartOutlet deviceInfo = SmartOutletManager.getInstance().getActiveSmartOutlet();
                        if(deviceInfo != null){
                            String ipAddress =deviceInfo.getIpAddress();
                            //udpManager.sendPacket(new EchoRequestRecord(), ipAddress);
                            //unregisterReceiver(received);
                        }
                    }
                }
            }

        }
    }

    /**
     * This function is responsible of requesting permissions.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
                        //startScanning();    //Start scanning.
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    /**
     * This is a helper function in needed permissions.
     * @return
     */
    private List<String> getPermissionsNeededToScan()
    {
        int permissionAcessCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionAcessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return listPermissionsNeeded;
    }

    //This is used to request permissions from the user.
    private void requestPermissionsFromTheUser(List<String> listPermissionsNeeded) {
        ActivityCompat.requestPermissions(this,
                listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    /**
     * When the activity pauses, we are stopping the server listening from the smart outlet.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(UDPManager.getInstance().isServerRunning())
            UDPManager.getInstance().stopServer();
    }

    /**
     * If the server is not started, we are starting it on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!UDPManager.getInstance().isServerRunning())
            UDPManager.getInstance().startServer();
    }

    @Override
    protected void onDestroy() {
        UDPManager.getInstance().stopServer();
        super.onDestroy();
    }
}
