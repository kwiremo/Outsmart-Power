package com.outsmart.outsmartpower;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity {


    SmartOutlet activeSmartoutlet;
    DatabaseOperations Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Bootloader has to run first.
        BootlLoader bootlLoader = new BootlLoader(this);

        //Get the database instance
        //Db = DatabaseOperations.getInstance();
        DatabaseOperations Db = DatabaseOperations.getInstance();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
