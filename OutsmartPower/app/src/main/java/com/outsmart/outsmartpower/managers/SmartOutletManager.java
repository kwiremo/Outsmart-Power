package com.outsmart.outsmartpower.managers;

import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.OutsmartDeviceInfo;
import com.outsmart.outsmartpower.Support.BootlLoader;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 2/14/2017.
 */
public class SmartOutletManager implements Observer {
    private static SmartOutletManager ourInstance = new SmartOutletManager();
    public static SmartOutletManager getInstance() {
        return ourInstance;
    }


    /**
     * The OutsmartDeviceInfo class represents info for a single remote outsmart device that is
     * already saved in the database. We need to have this list at the start of the program.
     * This manager fetched a list from the database.
     */
    private List<OutsmartDeviceInfo> outSmartsInfoList;

    /**
     * DatabaseOperation Instance. SmartOutletManager will have access to the database. It will
     * Save or retrieve data as needed.
     */
    DatabaseOperations databaseOperations;

    private SmartOutletManager() {
    }

    //Get the smart outlet informations.


    public List<OutsmartDeviceInfo> getOutSmartsInfoList() {
        return outSmartsInfoList;
    }

    /**
     * This is called when the observable to which this observer is subscribed notifies its observers.
     * If the bootloader notifies the observers, this OutsmartManager will load a list of
     * smart outlets already saved.
     */

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BootlLoader.class)
        {
            //Save the database reference
            databaseOperations = DatabaseOperations.getInstance();

            //Initialize SettingsRecord with data from the database
            outSmartsInfoList = databaseOperations.getSmartOutlerInfo();
        }
    }

    public void saveSmartOutlet(OutsmartDeviceInfo info){
        databaseOperations.addSmartOutletInfo(info);
        outSmartsInfoList.add(info);
        //TODO: Make sure to update the list of available smart Outlet.
    }
}
