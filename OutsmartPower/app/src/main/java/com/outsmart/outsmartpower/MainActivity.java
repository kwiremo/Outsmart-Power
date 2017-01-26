package com.outsmart.outsmartpower;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 *Name: MainActivity Class
 *
 * Description: This class is the activity that is responsible for setting up the application, as
 * well as handling events that occur throughout the life of the application.
 */

public class MainActivity extends AppCompatActivity
    //This line allows us to use the Navigation Drawer
    implements NavigationView.OnNavigationItemSelectedListener {



    SmartOutlet activeSmartoutlet;
    DatabaseOperations Db;

    //This method is called when the app is first started. It sets up everything the application
    //needs in order to run.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //This will be the 'add' button on the main page of the UI
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        //Get the database instance
        Db = DatabaseOperations.getInstance();
        OutsmartDeviceDataRecord record = new OutsmartDeviceDataRecord(new DateManager(1485626052),1.1,2.2,3.3,4.4,240.4,222);
        Db.addDataRecord(record);
        List<OutsmartDeviceDataRecord> records = Db.getAllRecordsAfter(222,DateManager.getTodayMidnightSeconds());
        double expected = 1.1;
        String result = records.get(0).getRecordTime().getMilitaryTime();
        Log.e("OUTPUT", ""+ expected + " " + result);
        //setupSmartoutlet("","","");

        //Start Server
        //UDPServer.getInstance().execute(Home);
    }

    public void setupSmartoutlet(String initialSsid, String initialPassword, String nickname )
    {
        //Creating an outsmart oubject: This will later be moved to a setup function.
        SmartOutlet smartOutlet = new SmartOutlet(initialSsid, initialPassword, nickname);
        smartOutlet.setIpAddress(Constants.IP_ADDRESS);

        //set the active smart outlet (For now this shall change later. The active smart outlet
        //will be the smartoutlet that the user selects in a navigation drawer.

        activeSmartoutlet = smartOutlet;

        //
        Db.addSmartOutletInfo(smartOutlet);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

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
}
